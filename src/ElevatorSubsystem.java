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
public class ElevatorSubsystem implements Runnable{

	//used from communication with SCHEDULER (sharing status info of elevators)
	DatagramSocket statusSocket;
	
	//Number of elevators in the system
	private static int numberOfElevators;
	
	//List of Elevators in the system
	ArrayList<Elevator> elevators;

	//SYSTEM CONTROL CLASS WHICH ALLOWS TO CALL PORTS
	static Sysctrl sysctrl = new Sysctrl();
	

	
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
	
	
	
	/**
	 * Sends packet to Scheduler containing all of the statuses of the Elevators
	 *  to port "SchedulerStatusPort"
	 * @throws IOException 
	 * 
	 */
	private void send_statuses_to_scheduler() throws IOException {
				
		DatagramPacket packet;
		byte[] msg;
		
		ArrayList<RedefinedStatus> list = new ArrayList<RedefinedStatus>();
		
		for(Elevator e : elevators) {
			list.add(e.getStatus());
		}
			
		//convert the status in form of byte[]
		msg = sysctrl.combineStatusList(list);
			
		//prepare packet
		packet = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(),sysctrl.getPort("SchedulerStatusPort"));
			
		//send to Scheduler
		statusSocket.send(packet);
		
	}
	
	
	/*/////////////////// MAIN /////////////////////*/
	public static void main(String args[]){	
	}


	
	public void run() {

		for(Elevator e: elevators) {
			Thread t = new Thread(e,"Elevator"+e.getElevatorID());
		}
		
		
		/*
		 * StatusUpdater thread
		 * 
		 ************************************/
		Thread StatusSender = new Thread() {
			
			public void run() {
				
				while(true) {
					
					try {
						send_statuses_to_scheduler();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		};//end of statusUpdater thread
		
		
		
		/* 
		 * This thread updates the gui of each elevator in the elevator subsystem
		 * 
		 */
		Thread GUIupdater = new Thread() {
			public void run() {
				
				for(Elevator e : elevators) {
					
				}
				
				
				while(true) {
					
				}
			}
		};
		
		
		//START the Elevator Subsystem's threads
		//ReceiverThread.start();
		StatusSender.start();
		
	}

		
}



