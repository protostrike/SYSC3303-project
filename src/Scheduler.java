import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Scheduler is responsible for routing each elevator 
 * to requested floors. 
 * 
 * Scheduler must be prepared to handle 
 * possible faults and failures in the system
 * 
 ************************************************************/
public class Scheduler implements Runnable{

	// Class Attributes
	
	//Sockets
	private DatagramSocket floorSystemSocket, elevatorSystemSocket,statusSocket;

	//list of requests to be assighned
	private ArrayList<Person> requestList;
	
	//List of elevator statuses
	private ArrayList<RedefinedStatus> statusList;
	
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

		requestList = new ArrayList<Person>();
		statusList = new ArrayList<RedefinedStatus>();
		
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
	 *************************************************************************************/
	private void receive_requests() throws IOException, ClassNotFoundException {
		
		byte[] msg = new byte[26];
		
		DatagramPacket request = new DatagramPacket(msg,msg.length);
		
		// socket bound to port "Scheduler<--Floors"
		floorSystemSocket.receive(request);
		
		
		// local var Person used to add request to list
		Person p = (Person)sysctrl.convertBytesToPerson(request.getData());
		
		//DEBUGGING
		System.out.println("Scheduler: received request from Floor #"+p.getOriginFloor()+" going "+(p.isUp()?"up":"down") + "\n");
		
		//add request to list
		requestList.add(p);
	}	
	
	
	/**
	 * Receives the elevator statuses from the Elevator Subsystem
	 * 
	 * @throws IOException
	 *************************************************************************************/
	private void receive_elevator_statuses() throws IOException {
		byte[] msg = new byte[300];
		
		DatagramPacket statusPack = new DatagramPacket(msg,msg.length);
		
		// socket bound to port "StatusPort"
		statusSocket.receive(statusPack);
		
		
		
		//reconstruct the list of statuses
		statusList = sysctrl.decodeList(statusPack.getData());
		
		statusList.size();
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
		
		DatagramPacket packet;
		byte reply[] = new byte[30] ;
		
		ArrayList<RedefinedStatus> statuses = new ArrayList<RedefinedStatus>();
		
		int bestID = 0 ;
		int closest_distance = 1000;
		
			//checks list of Statuses
			for(RedefinedStatus status : statusList) {
				
				//Calculation of the distance between elevator and floor
				int distance = Math.abs(status.getCurrentFloor() - requestList.get(0).getOriginFloor());
				
				//this is the ideal condition
				if( distance < closest_distance && status.isUp()==requestList.get(0).isUp() && !status.isBusy()) {
					 closest_distance = distance;
					 bestID = status.getID();
				}
				//this is in case no elevators are going the same direction as request ATM but are still close
				else if(distance < closest_distance && status.isUp()==requestList.get(0).isUp()) {
					 closest_distance = distance;
					 bestID = status.getID();
				}
				
				//if other conditions dont meet, settle for this
				else if(distance < closest_distance) {
					 closest_distance = distance;
					 bestID = status.getID();
				}
				
			}
			
			//send request to best Elevator
			byte[] request = sysctrl.convertPersonToBytes(requestList.get(0));
			packet = new DatagramPacket(request,request.length,InetAddress.getLocalHost(),sysctrl.getPort("ElevatorBasePort"+bestID));
			elevatorSystemSocket.send(packet);
			
			//Remove request from the request list
			requestList.remove(requestList.get(0));
			//And reset the status List to its default state
			statusList = new ArrayList<RedefinedStatus>();
			
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
		 * RequestReceiverThread thread receives incoming elevator request
		 *   
		 ************************************/
		Thread RequestReceiverThread = new Thread() {
			public void run() {
				
				System.out.println("\nScheduler: waiting for request...\n");
				
				while(true) {
					
					while(requestList.size() < 4) {
						try {
							//Thread that receives request and places them into requestList
							receive_requests();
						} 
						catch (ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}
						
				}
				
			}
		};//end of Receiver Thread
		
		/*
		 * 
		 * StatusReceiverThread thread receives incoming elevator request
		 *
		 * Waits for new request,
		 * converts the request into a Person Object,
		 * adds person to requestList
		 *   
		 ************************************/
		Thread StatusReceiverThread = new Thread() {
			public void run() {
				
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
		};//end of Status Receiver Thread

		
		/*
		 * RequestAssigner thread assigns 
		 * the first request in the list of request 
		 * in Scheduler to an Elevator
		 * 
		 *************************************/
		Thread RequestAssigner = new Thread() {
			
			public void run() {
				
				while(true) {

					//while there are NO requests to be assigned
					while(requestList.isEmpty());
					
					System.out.println("Scheduler: assigning a request from Floor #"+requestList.get(0).getOriginFloor()+"\n");
										
					//assign an elevator to Request in requestList
					try {
						assign_request_to_elevator();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		};//end of requestAssigner thread
		
		
		//START the Scheduler's threads
		RequestAssigner.start();
		StatusReceiverThread.start();
		RequestReceiverThread.start();
		
	}

}

