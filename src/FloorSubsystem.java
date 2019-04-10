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
	
	
	// number of floors in building
	private int numberOfFloors;
	
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
	public FloorSubsystem() throws IOException  {

		//INIT #OF FLOORS
		this.numberOfFloors = sysctrl.getNumberOfFloors();

		//INIT LIST OF FLOORS IN SYSTEM
		floors = new ArrayList<Floor>();
		for(int i = 1; i <= numberOfFloors; i++) {
			floors.add(new Floor(i));
		}
		
	}
		
		
	/**
	 * 
	 * Extracts the requests to be made from a *.txt file
	 * 
	 * THEN sends each request to the Scheduler
	 * 
	 * @throws IOException 
	 *   
	 */
	private void retrieveRequests(File f) throws IOException {  //create request

		//origin and destination
		int originFloor,destinationFloor;
		
		//We will need to make calls to update the floor's call button lamps
		String upOrDownPressed;
		
		//direction of travel
		boolean wantsToGoUp;
		
		//fault injection
		int faultCode, faultLocation;
		
		
		
		//Takes text file contents and 
		BufferedReader input = new BufferedReader(new FileReader(f));
		String line;
		
		while((line = input.readLine()) != null) {
			String data[]=line.split(" ");
			
			//prepare request info
			time = data[0];
			originFloor = Integer.parseInt(data[1]);
			
			if(data[2].equals("up")) {wantsToGoUp = true;}
			else {wantsToGoUp = false;}
			
			destinationFloor = Integer.parseInt(data[3]);
			
			faultCode = Integer.parseInt(data[4]);
			faultLocation = Integer.parseInt(data[5]);
			
			
			//create person
			Person p = new Person( time, originFloor, destinationFloor, wantsToGoUp, faultCode, faultLocation);
			
			
			//add request to appropriate Floor object
			
			//NOTE: *** index in list is floor#-1 ***
			floors.get(originFloor-1).getRequests().add(p);
			
			//Floor containing request sends to Scheduler
			floors.get(originFloor-1).sendRequestToScheduler(p);
			
		}//end of while loop
		
		input.close();

	}
		
			////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////
			//////////////////MAIN AND RUN METHOD///////////////////////
			////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////


	/**
	 * The main() of FloorSubsystem
	 * 
	 * @param args 
	 * @throws IOException 
	 */
	public static void main( String args[] ) throws IOException {

		now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
		startTime = System.currentTimeMillis();
		
		
		//We can change the number of Floors in the system 
		//using the parameter in FloorSubsystem constructor
		
		//Thread f = new FloorSubsystem();
		
		//START runnable FloorSubsystem
		
		//f.start();
	}
	
	/**
	 * run() method is used to initiate floorSubsystem
	 */
	@Override
	public void run() {
		
		System.out.println("\nFloorSubsystem: sending requests...\n");
		
		//request Data is received and sent to Scheduler
		try {
			retrieveRequests( new File("data.txt") );
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		while(true) {
			//Floors interact with rest of system
		}
	}
	
	
}