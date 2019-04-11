// Floor subsystem
// Read requests and send requests to scheduler

import java.io.*;
import java.net.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The Floor system manages the floor side interactions
 * of the Elevator Simulation
 */
public class FloorSubsystem {

	int numFloors =10;								// number of floors in building
	ArrayList<floor> floors;
	LinkedList <Person> requests;
	DatagramPacket sendPacket,receivePacket;
	DatagramSocket receiveSocket, sendSocket;
	String upOrDownPressed ;
	String upOrDownLamp;
	String direction; //of elevator
	String time;
	Time t,z;
	Scanner x;
	int currentFloor;
	int desiredFloor;
	int errType,errFloor;
	static Calendar now;
	private static Timer timer;
	private static long startTime;
	Date d;

	Sysctrl sysctrl = new Sysctrl();
	
	
	
	
	//ip setting
	InetAddress SchedulerIP = InetAddress.getLocalHost();
	//Unquote line below and replace the "host" with ip address of Scheduler
	//InetAddress SchedulerIP = InetAddress.getByName(host);


	/**
	 * Default constructor FloorSubsystem() creates a FloorSubsystem object
	 * @throws UnknownHostException 
	 * 
	 */
	public FloorSubsystem() throws UnknownHostException  {

		try {

			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(sysctrl.getPort("floorReceivePort"),SchedulerIP);
	
		}catch (SocketException se) {   
			se.printStackTrace();
			System.exit(1);
		}

		floors = new ArrayList<floor>();
		requests = new LinkedList<Person>();


		for (int i=0;i<numFloors;i++) {                     // initialize all floors
			floors.add(new floor(i+1));
		}

		File f = new File("data.txt");
		getData(f);
	}

	/**
	 * The main() of FloorSubsystem
	 * 
	 * @param args 
	 * @throws UnknownHostException 
	 */
	public static void main( String args[] ) throws UnknownHostException
	{
		now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
		startTime = System.currentTimeMillis();
		FloorSubsystem f = new FloorSubsystem();
		floorHandler h = new floorHandler(f);
		new Thread(h).start();
	}


	/**
	 * The start() method is used to initialize the floor subsystem 
	 * and prepare it for accepting and handling requests
	 */
	public void start() {
		while (!requests.isEmpty())  {
			long duration = System.currentTimeMillis()-startTime;
			DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
			
			try {
				d = dateFormat.parse(requests.getFirst().getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		Calendar requestTime = Calendar.getInstance();
		requestTime.setTime(d);
				
		if(requestTime.get(Calendar.SECOND)== (int)(duration/1000)
				&& requestTime.get(Calendar.MINUTE) == (int)(duration/60000)
				&& requestTime.get(Calendar.HOUR) == (int)(duration/3600000))
		{
			System.out.println("reached");
			sendSingleRequest(requests.remove());
		}	
		
	}

		
	}

	/**  
	 * The getData() method read a text file and creates a request
	 */
	private void getData(File f) {                      //create request

		try {
			x = new Scanner(f);
		}catch(Exception e) {
			sysctrl.printLog("File doesnt exist");
		}

		while (x.hasNext()) {                              //reads input
			time = x.next();
			currentFloor = Integer.parseInt(x.next());
			upOrDownPressed = upOrDownLamp = x.next();
			desiredFloor = Integer.parseInt(x.next());
			errType = Integer.parseInt(x.next());
			errFloor = Integer.parseInt(x.next());

			Person p = new Person(time,currentFloor,desiredFloor,upOrDownPressed.trim().equals("up")?true:false,errType,errFloor);

			floors.get(currentFloor-1).setButtonPressed(upOrDownPressed);
			//sysctrl.printLog("floor "+currentFloor+" pressed button "+upOrDownPressed+" to floor "+desiredFloor);//updates buttons pressed per floor
			floors.get(currentFloor-1).requests.add(p);				      // adds request to floor requests		
			requests.add(p);                                              // add request to subsystem requests
		}


	}

	private void sendSingleRequest(Person p) {
		// Wait for packet
		//ElevatorStatus es = waitForElevatorStatus();

		//Update all floor arrow lamps
		//updateArrowLamps(es);
		byte msg[]=null;
		try {
			msg = sysctrl.convertToBytes(p);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(), sysctrl.getPort("floorSendPort"));
		}catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		sysctrl.printLog("Floor: Sending packet to Scheduler...:");
		sysctrl.printLog("Person: " + p.toString());

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		sysctrl.printLog("Floor "+ p.getOriginFloor()+" is pressed "+( p.up?"up":"Down"));
		sysctrl.printLog("waiting...");
	}
	/**
	 * Wait and return elevator's status
	 * @return elevator status received from elevator
	 */
	private ElevatorStatus waitForElevatorStatus() {
		byte data[] = new byte[500];
		receivePacket = new DatagramPacket (data,data.length);

		try { 
			receiveSocket.receive(receivePacket);  //receives elevator status from floor
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		sysctrl.printLog("Packet received\n");

		int len = receivePacket.getLength();
		data = Arrays.copyOfRange(data,0,len);

		//Get elevator status
		ElevatorStatus es = new ElevatorStatus();

		try {
			es = (ElevatorStatus) sysctrl.convertFromBytes(data);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e2){
			e2.printStackTrace();
		}
		return es;
	}

	private void updateArrowLamps(ElevatorStatus es) {
		direction = (es.up?"up":"Down");
		for (int i=0;i<numFloors;i++) {
			// update all floor arrow lamps
			floors.get(i).direction=direction;
		}

		sysctrl.printLog(es);

	}
}

/**
 * floorHandler class is meant to handle runnable floor interactions
 */
class floorHandler implements Runnable 
{

	//Class Variables
	private FloorSubsystem floorSubsystem;

	/**
	 * Non-default constructor for creating and initializing floorHandler
	 * 
	 * @param floorSubsystem - FloorSubsystem object
	 */
	public floorHandler(FloorSubsystem floorSubsystem) {
		this.floorSubsystem = floorSubsystem; 
	}

	/**
	 * run() method is used to initiate floorSubsystem
	 */
	@Override
	public void run() {
		while (true) {
			floorSubsystem.start();
		}

	}
}