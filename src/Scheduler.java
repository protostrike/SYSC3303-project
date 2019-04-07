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
public class Scheduler extends Thread{

	// Class Attributes
	
	//Sockets
	private DatagramSocket floorSystemSocket, elevatorSystemSocket,statusSocket;

	//list of requests to be assighned
	private ArrayList<Person> requestList = new ArrayList<Person>();
	
	//this is never used...........
	private Map<Integer, RedefinedStatus> elevatorStatuses = new HashMap<Integer, RedefinedStatus>();
	
	//used in assigning requests to elevators
	private int numberOfElevators;
	
	public Calendar cal;

	public Sysctrl sysctrl = new Sysctrl();


	/**
	 * Constructor of Scheduler
	 * 
	 * 
	 * @param numberOfFloors
	 * @param numberOfElevators
	 * @throws UnknownHostException 
	 * @throws SocketException 
	 * 
	 ************************************************************/
	public Scheduler() throws UnknownHostException, SocketException {
		
		elevatorSystemSocket = new DatagramSocket(sysctrl.getPort("Scheduler<--Elevators"));
		floorSystemSocket = new DatagramSocket(sysctrl.getPort("Scheduler<--Floors"));
		statusSocket = new DatagramSocket(sysctrl.getPort("SchedulerStatusPort"));

		
		this.numberOfElevators = sysctrl.getNumberOfElevators();
		
		//prepare packet to be sent to ElevatorSubsystem
		//byte[] msg  = { (byte)numberOfElevators };
		//DatagramPacket packet = new DatagramPacket(msg,msg.length, InetAddress.getLocalHost(), sysctrl.getPort("SchedulerElevatorPort"));
		
		//send packet to ElevatorSubsystem
		//try {
		//	elevatorSystemSocket.send(packet);
		//}
		//catch(Exception e) {	
		//}
	}
	
	/**
	 * 
	 * this method receives the requests sent to "Scheduler<--Floors" 
	 * and adds them to requestList
	 * 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void receive_requests() throws IOException, ClassNotFoundException {
		
		// local var packet only meant to receive data
		DatagramPacket request = new DatagramPacket(new byte[100],100);
		
		// socket bound to port "Scheduler<--Floors"
		floorSystemSocket.receive(request);
		
		// local var Person used to add request to list
		Person p = (Person)sysctrl.convertFromBytes(request.getData());
		
		//add request to list
		requestList.add(p);
		
	}	
	
	/**
	 * this method is used to assign requests to the best elevator
	 * 
	 * 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * 
	 *************************************************************************************/
	private void assign_request_to_elevator() throws IOException, ClassNotFoundException {
		
		DatagramPacket packet = null;
		
		ArrayList<RedefinedStatus> statuses = new ArrayList<RedefinedStatus>();
		
		int bestID = 0 ;
		int closest_distance = 1000;
		byte statusRequest[]= {1};
		
		
		//there are requests to be assigned
		while(!requestList.isEmpty()) {
			
			//send packet to signal Elevator subsystem to send statuses of all elevators
			packet = new DatagramPacket(statusRequest,statusRequest.length,InetAddress.getLocalHost(),sysctrl.getPort("ElevatorStatusPort"));
			
			// receive packet for each elevator in system and store into temporary list
			for(int i = 1;i <= numberOfElevators; i++) {
				elevatorSystemSocket.receive(packet);
				statuses.add((RedefinedStatus)sysctrl.convertFromBytes(packet.getData()));
			}
			
			//first closest elevator will be sent a packet
			for(RedefinedStatus status : statuses) {
				
				//quick calculation of the distance between elevator and floor
				int distance = Math.abs(status.getCurrentFloor() - requestList.get(0).getOriginFloor());
				
				//this is the ideal condition
				if( distance < closest_distance && status.isUp()==requestList.get(0).isUp()) {
					 closest_distance = distance;
					 bestID = status.getID();
				}
				//this is in case no elevators are going the same direction as request ATM but are still close
				else if(distance < closest_distance) {
					 closest_distance = distance;
					 bestID = status.getID();
				}
				
			}
			
			//send request to best Elevator
			byte[] request = sysctrl.convertToBytes(requestList.get(0));
			packet = new DatagramPacket(request,request.length,InetAddress.getLocalHost(),sysctrl.getPort("ElevatorBasePort"+bestID));
			elevatorSystemSocket.send(packet);
			
			//Remove request from the the list of pending requests
			requestList.remove(requestList.get(0));
						
		}
	}
			

	
	/** needs another look
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private ArrayList<RedefinedStatus> request_statuses() throws IOException, ClassNotFoundException{
		
		//99 is the message that indicates that the eSub must send Scheduler list of statuses
		byte[] msg = {9};
		DatagramPacket status = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(),sysctrl.getPort("ElevatorStatusPort"));
		
		ArrayList<RedefinedStatus> eStatusList = new ArrayList<RedefinedStatus>();
		
		//send signal to elevator subsystem to send Scheduler status obj of each elevator
		statusSocket.send(status);
		
		//receive the statuses from ElevatorSubsystem
		for(int i = 0; i < numberOfElevators; i++) {
			statusSocket.receive(status);
			//add status to the list to be returned
			eStatusList.add((RedefinedStatus)sysctrl.convertFromBytes(status.getData()));
		}
		
		return eStatusList;
	}
	
	

	
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	//////////////////MAIN AND RUN METHOD///////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
   /*************************************************************
    * 
    * SCHEDULER RUN() METHOD
	*
	**************************************************************/
	public void run() {

		/*
		 * 
		 * ReceiverThread thread receives incoming elevator request
		 *
		 * Waits for new request,
		 * converts the request into a Person Object,
		 * adds person to requestList
		 *   
		 ************************************/
		Thread ReceiverThread = new Thread() {
			public void run() {
				
				System.out.println("\nScheduler: waiting for request...\n");
				
				while(true) {
					try {
						//Thread that receives request and places them into requestList
						receive_requests();
					} 
					catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
						
				}
				
			}
		};//end of Receiver Thread

		
		/*
		 * RequestAssigner thread assigns 
		 * the first request in the list of request 
		 * in Scheduler to an Elevator
		 * 
		 *************************************/
		Thread RequestAssigner = new Thread() {
			
			public void run() {
				
				while(true) {
					
					try {
						assign_request_to_elevator();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				
				}
			}
		};//end of requestAssigner thread
		

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
		
		
		//START the Scheduler's threads
		ReceiverThread.start();
		RequestAssigner.start();
		statusUpdater.start();
		
	}

}

