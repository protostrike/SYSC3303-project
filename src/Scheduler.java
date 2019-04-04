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
 * 
 ************************************************************/
public class Scheduler {

	// Class variables

	private DatagramPacket  floorPacket, elevatorPacket;
	private DatagramSocket floorSystemSocket, elevatorSystemSocket;

	private ArrayList<Person> requestList = new ArrayList<Person>();
	private Map<Integer, ElevatorStatus> elevatorStatuses = new HashMap<Integer, ElevatorStatus>();
	

	//byte[] statusByte = {(byte)0};
	//byte[] moveUpCommandByte = {(byte)1};
	//byte[] moveDownCommandByte = {(byte)2};
	//byte[] StartEngineCommandByte = {(byte)3};
	//byte[] StopEngineCommandByte = {(byte)4};
	//byte[] openDoorCommandByte = {(byte)5};
	//byte[] closeDoorCommandByte = {(byte)6};
	//byte[] turnLampOnCommandByte = {(byte)7,0};
	//byte[] turnLampOffCommandByte = {(byte)8};

	public Calendar cal;

	public Sysctrl sysctrl = new Sysctrl();

	//int currentFloor;

	//LinkedList<Integer> destinationList = new LinkedList<Integer>();

	/**
	 * Constructor of Scheduler
	 * 
	 * 
	 * @param numberOfFloors
	 * @param numberOfElevators
	 * 
	 ************************************************************/
	public Scheduler(int numberOfFloors, int numberOfElevators) {
		
		int temp;
		
		try {
			//elevatorSystemSocket.send(numberOfElevators);
		}
		catch(Exception e) {
			
		}
	}
	
	
	/**????????????? DO WE NEED A MAIN ??????????????????**/
	
	public static void main( String args[] ) {
		Thread personThread;
		//Scheduler scheduler = new Scheduler(5,2);
		//GiveDirections GiveDirections = new GiveDirections(scheduler);
		
		//Create a thread that gives directions to the Scheduler
		
		//personThread = new Thread(GiveDirections, "New request");
		
		//personThread.start();
	}
	/**			??????????????????????????????			**/
	

   /**
	* 
	* 
	* 
	* 
	* the run method is where we initialize and start the SCHEDULER utility threads
	* 
	* 
	* 
	* 
	* 
	**************************************************************/
	public void run() {

		/*
		 * 
		 * ReceiverThread thread receives incoming elevator request
		 *
		 *   
		 ************************************/
		Thread ReceiverThread = new Thread() {
			
			
			public void run() {
				
				while(true) {
					
					
					try {
						waitForRequest();
					} 
					catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
						
				}
				
			}
		};//end of Receiver Thread

		
		/*
		 * requestHandler thread  
		 * 
		 *************************************/
		Thread requestHandler = new Thread() {
			
			public void run() {
				
				
				
				while(true) {
					handleRequest();
					
				
			}
			}
		};//end of RequestHandler thread
		

		/*
		 * StatusUpdater thread
		 * 
		 ************************************/
		Thread statusUpdater = new Thread() {
			
			public void run() {
				
				while(true) {
					//waitForStatus();
				}
			}
		};//end of statusUpdater thread
		
		
		//Start the Scheduler's threads
		requestHandler.start();
		ReceiverThread.start();
		statusUpdater.start();
	}

	
	
	/**
	 * 
	 * this method waits for Floor to send a request to packet
	 * to the Scheduler and converts the request into a Person object
	 * 
	 * 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * 
	 ************************************************************/
	private  void waitForRequest() throws IOException, ClassNotFoundException {
		
		//Prepare Datagram packet
		byte[] data = new byte[500];
		DatagramPacket requestFromFloor = new DatagramPacket(data, data.length);
		
		//receive Pacekt sent from floor
		floorSystemSocket.receive(requestFromFloor);
			
		// copy contents of packet into local variable 'data'
		data = Arrays.copyOfRange(data,0,floorPacket.getLength());

				
		//	!!!!Convert request Data into a Person object!!!!
		try {
			Person waitingPerson = (Person)sysctrl.convertFromBytes(data);
			System.out.println("request received from floor");
	
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
			
			
			
			

	
	/**
	 * 
	 * @param es
	 * @param port
	 * 
	 ************************************************************/
	private void updateStatus(ElevatorStatus es, int port) {
		for(int i = 1; i < 3; i ++) {
			if(sysctrl.getPort("Elevator"+i) == port) {
				elevatorStatuses.put(i, es);
				
			}
		}
	}

	/**
	 * 
	 * Handle request in list
	 * Wait if no requests in list
	 *
	 ************************************************************/
	private void handleRequest() {
		
		//No request
		//wait here
		try {
			wait();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*
		if(requestList.isEmpty())
			
			//wait here
			try {
				wait();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		//There is a request in requestList
		else {
			
			while(!requestList.isEmpty()) {
				Person currentPerson = requestList.getFirst();
				//
				assignRequest(currentPerson);
				//when request is sent, remove for
				requestList.remove(currentPerson);
			}
		}
		*/

	
		/*
		 * synchronized requestList
		 * 
		 ***************************/
		synchronized (requestList) {
			while (requestList.isEmpty()) {
		try {
			requestList.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}
			//Person currentPerson = requestList.remove(requestList.getFirst());
			//assignRequest(currentPerson);
			System.out.println("Request sent to elevator");
			requestList.notifyAll();
			
		}
		//end of synchronized requestList
	}

	/**
	 * assignRequest is a method that prepares a datagram packet containing Person data
	 * that is sent to an Elevator to be store into their requestList
	 * 
	 * @param person
	 * 
	 ************************************************************/
	private void assignRequest(Person person) {
		
		int id = assignPickUpToElevator(person);
		//(person);

		
		//Prepare a DatagramPacket  
		try {
			byte[] data;
			data = sysctrl.convertToBytes(person);
			DatagramPacket elevatorPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), sysctrl.getPort("Elevator"+id));
			System.out.println("sending request...");
			//send packet to appropriate Elevator
			try {
				elevatorSystemSocket.send(elevatorPacket);
			} 
			
			catch (IOException e) {
				e.printStackTrace();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**		!!!!!!!!!!!!!!!TO BE REMOVED!!!!!!!!!
	 * 
	 * 
	 * assignPickUpToElevator; is a method that iterates across the list of 
	 * elevators in the system and select the best elevator for a request
	 *  
	 * 
	 * @param person - Person object containing the request info
	 * @return - the INDEX of the Elevator best suited to fulfill the request
	 * 
	 ************************************************************/
	private int assignPickUpToElevator(Person person) {
		
		//local variable status list
		List<ElevatorStatus> statusList = new ArrayList<ElevatorStatus>();
		for(int i = 1; i <= 2; i++) {
			statusList.add(elevatorStatuses.get(i));
		}
		
		//local vars
		int count = 1000;
		int i = 0;
		int distance;
		
		
		for(ElevatorStatus es : statusList) {
			
			//if the elevator is going same way as Person
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
			
			//if elevator is going up
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
}



/**
 * 
 *
 *
 ************************************************************/
class GiveDirections implements Runnable
{
	private Scheduler scheduler;

	public GiveDirections(Scheduler scheduler) {
		this.scheduler = scheduler; 
	}

	@Override
	public void run() {
		
			scheduler.run();
			
			
		

	}

}