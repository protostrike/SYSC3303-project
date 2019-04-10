import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import sun.awt.SunHints.Value;

/**
 * The Scheduler is responsible for routing each elevator 
 * to requested floors. 
 * 
 * Scheduler must be prepared to handle 
 * possible faults and failures in the system
 */
public class Scheduler {

	// Class variables
	gui g = new gui();
	DatagramPacket  floorPacket, elevatorPacket;
	DatagramSocket floorSocket, elevatorSocket, elevatorSocket2;

	LinkedList<Person> personList = new LinkedList<Person>();
	Map<Integer, ElevatorStatus> elevatorStatuses = new HashMap<Integer, ElevatorStatus>();
	

	//command bytes for the evelator
	byte[] statusByte = {(byte)0};
	byte[] moveUpCommandByte = {(byte)1};
	byte[] moveDownCommandByte = {(byte)2};
	byte[] StartEngineCommandByte = {(byte)3};
	byte[] StopEngineCommandByte = {(byte)4};
	byte[] openDoorCommandByte = {(byte)5};
	byte[] closeDoorCommandByte = {(byte)6};
	byte[] turnLampOnCommandByte = {(byte)7,0};
	byte[] turnLampOffCommandByte = {(byte)8};

	Calendar cal;

	Sysctrl sysctrl = new Sysctrl();

	int currentFloor;

	LinkedList<Integer> destinationList = new LinkedList<Integer>();

	/*
	 * Constructor
	 */
	public Scheduler()
	{
		for (int i=1;i<=sysctrl.numElevators;i++) {
			elevatorStatuses.put(i, new ElevatorStatus());
		}
		
		//Construct the GUI for all elevators
		for (int i: elevatorStatuses.keySet()) {
		g.updateGrid(i, elevatorStatuses.get(i).currentFloor);
		}
		
		try {
			elevatorSocket = new DatagramSocket(sysctrl.getPort("SchedulerReceiveElevatorPort"));	// socket for receiving floor arrival updates
			elevatorSocket2 = new DatagramSocket(sysctrl.getPort("ElevatorStatusPort"));   // socket for receiving elevator status
			floorSocket = new DatagramSocket(sysctrl.getPort("floorSendPort")); // socket for receiving floor request

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
	}

	public static void main( String args[] )
	{
		Thread personThread;
		Scheduler scheduler = new Scheduler();
		PersonHandler personHandler = new PersonHandler(scheduler);
		personThread = new Thread(personHandler, "New request");
		personThread.start();
	}

	/**********************New Changes Below**********************/
	///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * the run method is where we initialize and start the SCHEDULER utility threads
	 */
	public void run() {

		/*
		 * requestWaiter thread receives incoming elevator request
		 * and stores them in the  
		 */
		Thread requestWaiter = new Thread() {
			
			public void run() {
				while(true) {
						waitForRequest();						
				}
				
			}
		};

		/*
		 * requestHandler thread  
		 */
		Thread requestHandler = new Thread() {
			
			public void run() {
				
				while(true) {
					handleRequest();
					
				
			}
			}
		};

		Thread statusUpdater = new Thread() {
			public void run() {
				while(true) {
					waitForStatus();
				}
			}
		};
	
		//Start the three threads to updateStatus, handle Requests and wait for requests
		statusUpdater.start();
		requestHandler.start();
		requestWaiter.start();
		
	}

	private  void waitForRequest() {
		
		synchronized (personList) {
		byte[] data = new byte[500];
		DatagramPacket floorPacket = new DatagramPacket(data, data.length);

		//Receive request from floor system
		//Wait if no new request
	
			try {
				floorSocket.receive(floorPacket);
				int len = floorPacket.getLength();
				data = Arrays.copyOfRange(data,0,len);
				try {
					personList.add((Person)sysctrl.convertFromBytes(data));
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("request received from floor");
			
				personList.notifyAll();
				
				
			try {
				personList.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		
		} 
	
	//Wait for elevator status update
	private void waitForStatus() {
		byte[] data = new byte[1000];
		DatagramPacket esPacket = new DatagramPacket(data, data.length);

		try {
		
				elevatorSocket2.receive(esPacket);
				ElevatorStatus es;
				try {
					es = (ElevatorStatus) sysctrl.convertFromBytes(esPacket.getData());
					updateStatus(es, esPacket.getPort());
					
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// Some kind of IO Exception
				return;
				//e.printStackTrace();
			}

		} 
	
	
	// Update the status of Elevator
	private void updateStatus(ElevatorStatus es, int port) {
		for(int i = 1; i <= sysctrl.numElevators; i ++) {
			if(sysctrl.getPort("Elevator"+i) == port) {
				elevatorStatuses.put(i, es);
				g.updateGrid(i,es.currentFloor);
				g.repaint();
				if (es.fault==1) {
					g.updateRequests(i,"Stuck");
					g.repaint();
					
				}

			}
		}
	}

	/*
	 * Handle request in list
	 * Wait if no requests in list
	 */
	private void handleRequest() {
		
		synchronized (personList) {
			while (personList.isEmpty()) {
		try {
			personList.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}
			Person currentPerson = personList.remove();
			sendRequest(currentPerson);
			System.out.println("Request sent to elevator");
			personList.notifyAll();
			
		}
	}

	/**
	 * sendRequest is a method that prepares a datagram packet containing Person data
	 * that is sent to an Elevator to be store into their requestList
	 * @param person
	 */
	private void sendRequest(Person person) {
		int id = determineElevator(person);

		if (id!=0) {
		//Prepare a DatagramPacket  
		try {
			byte[] data;
			data = sysctrl.convertToBytes(person);
			DatagramPacket elevatorPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), sysctrl.getPort("Elevator"+id));
			System.out.println("sending request...");
			//send packet to appropriate Elevator
			try {
				elevatorSocket.send(elevatorPacket);
			} 
			
			catch (IOException e) {
				e.printStackTrace();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		}
	}

	/**
	 * determineElevator is a method that iterates across the list of 
	 * elevators in the system and select the best elevator for a request
	 *  
	 * 
	 * @param person - Person object containing the request info
	 * @return - the INDEX of the Elevator best suited to fulfill the request
	 */

		private int determineElevator(Person person) {
			int selectedEl=0;
			
			Map<Integer,ElevatorStatus> statusList = new HashMap<Integer,ElevatorStatus>();
			
			for (int i: elevatorStatuses.keySet()) {
				if (elevatorStatuses.get(i).fault==0) 
					statusList.put(i, elevatorStatuses.get(i));
			}
			
		
			for (int i: statusList.keySet()){
				ElevatorStatus e = statusList.get(i);

				
				if (e.up &&person.originFloor>e.currentFloor) {
					selectedEl=i;
				break;
			}
				else if (!e.up && person.originFloor<e.currentFloor ) {
					selectedEl= i;
					break;
				}
				else if (person.originFloor==e.currentFloor) {
					selectedEl = i;
					break;
				}
				
				}
			
			
			
			g.updateRequests(selectedEl, person.toString());
			g.repaint();
			
			return selectedEl;
			
			
		
		}
	
}

// The Runnable class to handle different persons
class PersonHandler implements Runnable
{
	private Scheduler scheduler;

	public PersonHandler(Scheduler scheduler) {
		this.scheduler = scheduler; 
	}

	@Override
	public void run() {
		
			scheduler.run();
		

	}

}