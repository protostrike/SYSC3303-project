import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The Scheduler is responsible for routing each elevator 
 * to requested floors. 
 * 
 * Scheduler must be prepared to handle 
 * possible faults and failures in the system
 */
public class Scheduler {

	// Class variables

	DatagramPacket  floorPacket, elevatorPacket;
	DatagramSocket floorSocket, elevatorSocket, elevatorSocket2;

	LinkedList<Person> personList = new LinkedList<Person>();
	Map<Integer, ElevatorStatus> elevatorStatuses = new HashMap<Integer, ElevatorStatus>();
	

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
		elevatorStatuses.put(1,new ElevatorStatus());
		elevatorStatuses.put(2, new ElevatorStatus());
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
				//synchronized (personList) {
				while(true) {
						waitForRequest();
					//	personList.notifyAll();
						//System.out.println("Notified");
						
			//	}
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
		requestHandler.start();
		requestWaiter.start();
		statusUpdater.start();
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
			System.out.println("Notified..");
			try {
				personList.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		
		} 
	

	private void waitForStatus() {
		//Wait for elevator status update
		byte[] data = new byte[500];
		DatagramPacket esPacket = new DatagramPacket(data, data.length);
		

		try {
			elevatorSocket2.setSoTimeout(2);
			try {
				elevatorSocket2.receive(esPacket);
				ElevatorStatus es;
				try {
					es = (ElevatorStatus) sysctrl.convertFromBytes(esPacket.getData());
					
					switch(es.workingStatus) {
					case -1: 
						System.out.println("Elevator door stuck, keep waiting!");
						waitForStatus();
						return;
					case -2:
						System.out.println("Elevator not responding, keep waiting!");
						waitForStatus();
						return;
					case -3:
						System.out.println("Elevator broke down, waiting for help!");
						waitForStatus();
						return;
					}
										
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
		} catch (SocketException e2) {
			//2 ms has passed and there is no new Person, therefore WAIT!
			try {
				sysctrl.printLog("waiting..");
				wait();
				return;
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}

	private void updateStatus(ElevatorStatus es, int port) {
		for(int i = 1; i < 3; i ++) {
			if(sysctrl.getPort("Elevator"+i) == port) {
				elevatorStatuses.put(i, es);
				
			}
		}
	}

	/*
	 * Handle request in list
	 * Wait if no requests in list
	 */
	private void handleRequest() 
	
	{
		
		//No request
		/*
		if(personList.isEmpty())
			
			//wait here
			try {
				wait();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		//There is a request in personList
		else {
			
			while(!personList.isEmpty()) {
				Person currentPerson = personList.getFirst();
				//
				sendRequest(currentPerson);
				//when request is sent, remove for
				personList.remove(currentPerson);
			}
		}
		*/

	
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

	/**
	 * determineElevator is a method that iterates across the list of 
	 * elevators in the system and select the best elevator for a request
	 *  
	 * 
	 * @param person - Person object containing the request info
	 * @return - the INDEX of the Elevator best suited to fulfill the request
	 */
	private int determineElevator(Person person) {
		
		List<ElevatorStatus> statusList = new ArrayList<ElevatorStatus>();
		for(int i = 1; i <= 2; i++) {
			statusList.add(elevatorStatuses.get(i));
		}
		int count = 1000;
		int i = 0;
		int distance;
		for(ElevatorStatus es : statusList) {
			if(es.isUp() == person.isUp()) {
				if(es.getCurrentFloor() <= person.getOriginFloor())
					return statusList.indexOf(es)+1;
				else if(es.getCurrentFloor() > person.getOriginFloor()) {
					distance = (es.getFarthestDestination() - es.getCurrentFloor()) + 
							(es.getFarthestDestination() - person.getOriginFloor());
					if(distance <= count) {
						count = distance;
						i = statusList.indexOf(es);
					}
				}
			}
			else {
				if(es.isUp()) {
					distance = (es.getFarthestDestination() - es.getCurrentFloor()) + 
							(es.getFarthestDestination() - person.getOriginFloor());
				}
				else {
					distance = (es.getCurrentFloor() - es.getFarthestDestination()) + 
							(person.getOriginFloor() - es.getFarthestDestination());
				}

				if(distance <= count) {
					count = distance;
					i = statusList.indexOf(es);
				}
			}
		}
		return i + 1;
	}
	
	/**
	 * handleError
	 */
	public void handleError(int errorType) {
		
	}
}


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