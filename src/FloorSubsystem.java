// Floor subsystem
// Read requests and send requests to scheduler

import java.io.*;
import java.net.*;
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
	Scanner x;
	int currentFloor;
	int desiredFloor;

	Sysctrl sysctrl = new Sysctrl();


	/**
	 * Default constructor FloorSubsystem() creates a FloorSubsystem object
	 * 
	 */
	public FloorSubsystem()  {

		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(sysctrl.getPort("floorReceivePort"));  //receives on port 5010
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
	 */
	public static void main( String args[] )
	{

		FloorSubsystem f = new FloorSubsystem();
		floorHandler h = new floorHandler(f);
		new Thread(h).start();
	}


	/**
	 * The start() method is used to initialize the floor subsystem 
	 * and prepare it for accepting and handling requests
	 */
	public synchronized void start() {
		while (!requests.isEmpty())  {    // sending the first request and so on...
			sendSingleRequest(requests.remove());
			// Wait for packet
			ElevatorStatus es = waitForElevatorStatus();

			//Update all floor arrow lamps
			updateArrowLamps(es);
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

			Person p = new Person(time,currentFloor,desiredFloor,upOrDownPressed.trim().equals("up")?true:false);

			floors.get(currentFloor-1).setButtonPressed(upOrDownPressed);
			sysctrl.printLog("floor "+currentFloor+" pressed button "+upOrDownPressed+" to floor "+desiredFloor);//updates buttons pressed per floor
			floors.get(currentFloor-1).requests.add(p);				      // adds request to floor requests		
			requests.add(p);                                              // add request to subsystem requests
		}


	}

	private void sendSingleRequest(Person p) {
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






