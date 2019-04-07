import java.io.*;
import java.net.*;
import java.util.*;



/****************************************************************************
 * 
 * The ElevatorSubsystem is a class controlled by the scheduler in order to
 * manage interactions with the elevator cars and operate the
 * motor and to open and close the doors. 
 * 
 * Each elevator has its own elevator subsystem
 * 
 ****************************************************************************/
public class ElevatorSubsystem extends Thread{

	//used from communication with SCHEDULER (sharing status info of elevators)
	DatagramSocket statusSocket;
	
	//Number of elevators in the system
	private static int numberOfElevators;
	
	//List of Elevators in the system
	ArrayList<Elevator> elevators;


	//SYSTEM CONTROL CLASS WHICH ALLOWS TO CALL PORTS
	static Sysctrl sysctrl = new Sysctrl();
	
	private boolean statusRequested = false;

	
	
	
	/**
	 * Constructor for the Elevator Subsystem
	 * 
	 * initializes the list of elevators
	 * 
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ElevatorSubsystem() throws IOException, ClassNotFoundException {
		
		
		elevators = new ArrayList<Elevator>();
		
		//establish connection between ElevatorSubsystem and scheduler
		try {
			//used to receive from
			statusSocket = new DatagramSocket(sysctrl.getPort("ElevatorStatusPort"));

			
		} 
		catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
		
		//number of elevators in the system
		numberOfElevators = sysctrl.getNumberOfElevators();
		
		//initialize list of Elevators in subsystem
		for(int i = 1; i <= numberOfElevators; i++) {
			//NOTE: all elevators start at floor #1
			elevators.add(new Elevator(i,1));
		}
		
	}
	
	private void receive_status_request() throws IOException {
		
		//init packet (avoiding error message)
		DatagramPacket packet = new DatagramPacket(new byte[1],1) ;

		statusSocket.receive(packet);
		
		byte[] msg = packet.getData();
		
		if(msg[0]  == 9) {
			statusRequested = true;
		}
		
		
	}
	
	/**
	 * SENDS THE REDEFINED STATUS OBJ OF EACH ELEVATOR 
	 * TO SCHEDULER PORT
	 * @throws IOException 
	 * 
	 * 
	 * 
	 */
	private void send_statuses_to_scheduler() throws IOException {
		
		DatagramPacket packet;
		byte[] msg;
		
		for(Elevator e : elevators) {
			//convert the status in form of byte[]
			msg = sysctrl.convertToBytes(e.getStatus());
			
			//prepare packet
			packet = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(),sysctrl.getPort("SchedulerStatusPort"));
			
			//send to Scheduler
			statusSocket.send(packet);
		}
	}
	
	
	/*/////////////////// MAIN /////////////////////*/
	public static void main(String args[]){}


	@Override
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
				
				while(true) {
					
					//this thread is always waiting for the chance to 
					//set statusRequested = true
					try {
						receive_status_request();
					} catch (IOException e) {
						e.printStackTrace();
					}
						
				}
				
			}
		};//end of Receiver Thread
		

		/*
		 * StatusUpdater thread
		 * 
		 ************************************/
		Thread StatusSender = new Thread() {
			
			public void run() {
				
				while(true) {
					
					//wait for Scheduler to request statuses
					while(!statusRequested) {
					}
					
					//send statuses
					try {
						send_statuses_to_scheduler();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					statusRequested = false;
				}
			}
		};//end of statusUpdater thread
		
		
		Thread ElevatorManager = new Thread() {
			public void run() {
				
				for(Elevator e : elevators) {
					System.out.println("Elevator #"+e.getElevatorID()+": waiting...");
				}
				
				
				while(true) {
					
				}
			}
		};
		
		
		//START the Elevator Subsystem's threads
		ReceiverThread.start();
		StatusSender.start();
		ElevatorManager.start();
		
	}

	
		
		
}



