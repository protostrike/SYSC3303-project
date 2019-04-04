// Floor subsystem
// Read requests and send requests to scheduler

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Floor system manages the floor side interactions
 * of the Elevator Simulation
 */
public class FloorSubsystem implements Runnable {

	//Attributes of the Floor Subsystem
	
	//List of pending requests from all Floors in System
	ArrayList<Person> requests;
	
	
	// number of floors in building
	private int numFloors;
	
	// List of Floors accessible  by Elevator
	ArrayList<Floor> floors;
	
	// Packets used for receiving and sending info to Scheduler
	DatagramPacket sendPacket,receivePacket;
	
	// Sockets for information transmission between FloorSubsystem and Scheduler
	DatagramSocket receiveSocket, sendSocket;
	
	
	//USED TO INJECT REQUEST INTO FLOOR OBJECTS
	private String time;
	private Scanner x;
	static Calendar now;
	
	//Timer used for measuring execution times
	private static Timer timer;
	private static long startTime;

	
	//Utility class used to convert objects to and from bytes
	Sysctrl sysctrl = new Sysctrl();


	/**
	 * Constructor FloorSubsystem() creates a FloorSubsystem object
	 * @throws IOException 
	 * 
	 */
	public FloorSubsystem( int numberOfFloors) throws IOException  {

		this.numFloors = numberOfFloors;
		

		floors = new ArrayList<Floor>();
		
		// Adds the Floor objects to list in FloorSubsystem
		for(int i = 1; i <= numberOfFloors; i++) {
			floors.add(new Floor(i));
		}
		
		//Requests will be retrieved from data.txt
		File f = new File("data.txt");
		
		retrieveRequests(f);
	}

	


	/**
	 * The start() method is used to initialize the floor subsystem 
	 * and prepare it for accepting and handling requests
	 
	public synchronized void start() {
		while (!requests.isEmpty())  {    // sending the first request and so on...
			long duration = System.currentTimeMillis() - startTime;
		
			
			//START TIME OF THE FLOORSUBSYSTEM THREAD
		}
	}
	 */
		
		
		
	/**
	 * 
	 * 
	 *   Retrieves data from the the text file f
	 * @throws IOException 
	 *   
	 */
	private void retrieveRequests(File f) throws IOException {  //create request

		//origin and destination
		int floor,desiredFloor;
		
		//We will need to make calls to update the floor's call button lamps
		String upOrDownPressed;
		
		boolean wantsToGoUp;
		
		int faultCode, faultLocation;
		
		try {
			x = new Scanner(f);
		}
		catch(Exception e) {
			sysctrl.printLog("File doesnt exist");
		}

		
		//while there is still text in the data.txt
		while (x.hasNext()) {   
			
			//PARSE text file to get:
			
			//the time of the request
			time = x.next();
			
			//the starting floor of the request
			floor = Integer.parseInt(x.next());
			
			//the direction of travel
			upOrDownPressed  = x.next();
			
			//parse to get the destination
			desiredFloor = Integer.parseInt(x.next());
			
			//parse to get the fault code of the request
			faultCode = Integer.parseInt(x.next());

			//parse to get the floor where the fault will occur
			faultLocation = Integer.parseInt(x.next());
			
			
			//Sets the direction of travel of request
			if(upOrDownPressed.equals("up")) {
				wantsToGoUp = true;
			}
			else {
				wantsToGoUp = false;
			}

			//create person object who holds request info
			Person p = new Person( time, floor, desiredFloor, wantsToGoUp);

			//Call elevator from Floor object
			floors.get(floor-1).getRequests().add(p);// adds person to list in Floor object	
			
			//call elevator returns a Datagram packet
			sendPacket = floors.get(floor-1).callElevator(p);//index in list is floor#-1
			
			//send Packet to scheduler
			try {
				sendSocket.send(sendPacket);
			} 
			catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			

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
		} 
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(), sysctrl.getPort("floorSendPort"));
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		sysctrl.printLog("Floor: Sending packet to Scheduler...:");
		sysctrl.printLog("Person: " + p.toString());

		try {
			sendSocket.send(sendPacket);
		} 
		catch (IOException e) {
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

	/*
	private void updateArrowLamps(ElevatorStatus es) {
		direction = (es.up?"up":"Down");
		for (int i=0;i<numFloors;i++) {
			// update all floor arrow lamps
			floors.get(i).direction=direction;
		}

		sysctrl.printLog(es);

	}
	*/


	
	
	////////////////////////////////////////////////////////////
	//////////////////MAIN AND RUN METHODS//////////////////////
	////////////////////////////////////////////////////////////

	/**
	 * The main() of FloorSubsystem
	 * 
	 * @param args 
	 */
	public static void main( String args[] )
	{

		
		now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
		startTime = System.currentTimeMillis();
		//FloorSubsystem f = new FloorSubsystem();
		//floorHandler h = new floorHandler(f);
		//new Thread(h).start();
	}
	
	/**
	 * run() method is used to initiate floorSubsystem
	 */
	@Override
	public void run() {
		
		while (true) {
			//floorSubsystem.start();
		}

	}
}