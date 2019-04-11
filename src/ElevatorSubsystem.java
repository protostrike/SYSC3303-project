/***
 * The ElevatorSubsystem is a class controlled by the scheduler in order to
 * manage interactions with the elevator cars and operate the
 * motor and to open and close the doors. 
 * 
 * Each elevator has its own elevator subsystem
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;



/**
 * 
 * 
 *
 */
public class ElevatorSubsystem {

	//used from communication with SCHEDULER
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	int elNumber,errFloor=-1,errType=-1;
	long start,duration;

	//Elevator Status instance for THIS elevator's status
	ElevatorStatus status = new ElevatorStatus();

	//Ready-to-pick-up list
	//LinkedList<Person> pickUpList = new LinkedList<Person>(); 

	byte[] statusByte;
	byte lampButton;


	static Sysctrl sysctrl = new Sysctrl();

	public ElevatorSubsystem(int port)
	{
		elNumber =port - sysctrl.baseElPort;
		
		try {
			sendReceiveSocket = new DatagramSocket(port);
			
			
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String args[])
	{
	
		
		for (int i=1;i<=sysctrl.numElevators;i++) {
			Thread el;
			ElevatorSubsystem c = new ElevatorSubsystem(sysctrl.getPort("Elevator"+i));
			ElevatorHandler elevatorHandler = new ElevatorHandler(c);
			el = new Thread(elevatorHandler, "New request");
			el.setPriority(10-i);
			el.start();

		}
		
	}


	/**
	 * process() is used to process requests and set up DatagramPackets
	 */
	
	/**********************New Changes Below**********************/
	//////////////////////////////////////////////////////////////
	
	///////////////REGINALD: ADDING THE METHODS TO OPERATE ELEVATOR/////////////////
	
	/**
	 * Turns motor off
	 */
	private void stopElevator() {
		
		System.out.println("Elevator" +elNumber+ ": "+ "stopped at "+status.currentFloor);
		status.motorOn = false;		
	}
	
	private void elevatorStuck() {
		System.out.println("Elevator"+elNumber+": stuck between floor "+status.currentFloor+" and "+(status.up?status.currentFloor+1:status.currentFloor-1));
		status.motorOn=false;
		synchronized(status.pickUpList) {
			status.pickUpList.clear();
			//status.requests.clear();
			}
		status.fault=1;
	}
	private void getFault (Person p) {
		if (p.errType!=0) {
			errFloor=p.errFloor;
			errType=p.errType;
		}
	}
	
	
	private void doorStuck() {
		System.out.println("Elevator"+elNumber+": door stuck on floor "+status.currentFloor);
		synchronized(status.pickUpList) {
			status.pickUpList.clear();
			//status.requests.clear();
			}
		status.fault=1;

	}
	
	private void chooseFault(int n) {
		
		if (n==1) {
			elevatorStuck();
		}
		if (n==2) {
			doorStuck();
		}
	}
	private void dropOffPerson() {
	synchronized (status.pickUpList) {
		Iterator<Integer> iter = status.pickUpList.iterator();
		while (iter.hasNext()) {
		    int p = iter.next();
		    if (p == status.currentFloor) {
		        iter.remove();
		        if (status.requests.containsKey(p)) {
		        	status.requests.remove(p);
		        }
		    }
		}		
	}

		    stopElevator();
			start = System.currentTimeMillis();
			openDoor();
		    //System.out.println("Elevator" +elNumber+": "+" Person dropped off");
			closeDoor();
			status.inUse = false;
			duration = System.currentTimeMillis()-start;
			if (duration>3000) {
				sysctrl.printLog("FAULT DETECTED");
			}
		}
		
	/**
	 * Moves the elevator UPWARDS by ONE floor
	 */
	private void moveUp() {
		
		status.motorOn = true;
		status.currentFloor++;
		System.out.println("Elevator" +elNumber+": "+ " Going up: floor "+status.currentFloor);		
		
	}
	
	/**
	 * Moves the elevator DOWNWARDS by ONE floor
	 */
	private void moveDown() {
		status.motorOn = true;
		status.currentFloor--;
		System.out.println("Elevator" +elNumber+": "+ " Going down: floor "+status.currentFloor);
	}
	

	/**
	 * OPENS the elevator doors
	 */
	private void openDoor() {
		status.setDoorOpen(true);
		System.out.print("Elevator"+ elNumber+": "+"Opening door...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("Door opened");
	}
	
	/**
	 * CLOSES the elevator doors
	 */
	private void closeDoor() {
		status.setDoorOpen(false);
		System.out.print("\nElevator"+ elNumber+": "+"Closing door...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Door closed\n");
	}
	
	/**
	 * ADDS Persons on floor going SAME DIRECTION as elevator 
	 */
	/*
		
	/**
	 * turnLampOn is a method that
	 * 
	 * @param destination
	 */
	private void turnLampOn(int destination) {
		System.out.println("Elevator"+elNumber+": Lamp "+destination+": turned On"); 
	}
	private void turnLampOff(int destination) {
		System.out.println("Elevator"+elNumber+": Lamp "+destination+": turned Off"); 
	}

	
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * waitForRequest is a method that waits for the SCHEDULER to send pickup assignments 
	 * to the ELEVATOR subsystem cars
	 */
	
	// Run this Thread
	public void run () {
	
		Thread getRequestThread = new Thread() {
			
			public void run() {
				while (true) {
				
					waitForRequest();
					
				}
			}
					
		};
		
		Thread elevatorOp = new Thread() {
			
			public void run () {
				while (true) {
					operateElevator();
				}
			}
		};
		elevatorOp.start();
		getRequestThread.start();
	}
	
	
	private  void waitForRequest()  {
		//Prepare Datagram packet
		

		byte[] data = new byte[500];
		receivePacket = new DatagramPacket(data, data.length);
		
			
			try {
				//Receive request from SCHEDULER
				sendReceiveSocket.receive(receivePacket);
				//add new request to pickUpList
				int len = receivePacket.getLength();
				data = Arrays.copyOfRange(data, 0, len);				
			
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
	
			synchronized (status.pickUpList) {
				addRequestToList(data);
				
				status.pickUpList.notifyAll();
			}			
	}
		
	

	/**
	 *  addRequestToList is a method that converts a Person's request (in form of byte array)
	 *  into a Person object and adds it to the ELEVATOR'S pickUpList
	 *  
	 * @param data - DatagramPacket data
	 */
	private void addRequestToList(byte[] data) {
		
		try {
			//convert request data into Person
		
			Person person = (Person)sysctrl.convertFromBytes(data);
		
			//add Person to list of people waiting to get picked up
			if (status.requests.get(person.originFloor)==null)
				status.requests.put(person.originFloor,new ArrayList<Integer>());
			status.requests.get(person.originFloor).add(person.destFloor);
			status.pickUpList.add(person.originFloor);
			
			getFault(person);
			turnLampOn(person.originFloor);
			System.out.println("Elevator"+elNumber+": request Added");
	
			
		}
		
		
		
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		}
	
		
	
	

	/**
	 * operateElevator is a method that will be run indefinitely 
	 * by the Elevator THREADS
	 * 
	 * @throws InterruptedException
	 */
	private   void operateElevator() {
		
	synchronized (status.pickUpList) {
		
		
			while (status.pickUpList.isEmpty()) {
				try {
					System.out.println("Elevator" +elNumber+": "+"Waiting...");
					status.pickUpList.wait();
					
				
			
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	try {
		//sleeps to allow any more requests to come in before moving the elevator
		Thread.sleep(100);
		
	} catch (InterruptedException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
			start = System.currentTimeMillis();
			moveElevator();
			duration = System.currentTimeMillis()-start;
			if (duration>5000) {								// IF ELEVATOR TAKES MORE THAN 5 SECONDS TO MOVE AGAIN, THERE IS A FAULT
				sysctrl.printLog("FAULT DETECTED");
			}
		}
	
	/**
	 * moveElevator is a method that will dictate where the elevator will move 
	 */
	
	
	private  void moveElevator() {
	
		
		if (status.currentFloor==errFloor) {	// check for error
			chooseFault(errType);
		}
		
		else {
		int dest = -1;
		status.setMotorOn(true);
		status.inUse = true;
		dest = getNearestPickup();	// get closest floor to elevator
		if (status.currentFloor>dest)	// if dest is under elevator go down
			status.up = false;		
		if (status.currentFloor < dest)	// if dest is above elevator,go up
			status.up= true;
		if (status.currentFloor ==dest) {					// when elevator reaches floor, turn lamp off, remove from destination list
				if (status.requests.containsKey(dest)) {
					for (int p:status.requests.get(dest)) { // if there are requests coming from this floor, add them to destination list
					status.pickUpList.add(p);
					turnLampOn(p);
					}
				}
				dropOffPerson();
				status.inUse = false;
				turnLampOff(dest);
				
		}

		
			// ELEVATOR will move UPWARDS
				if(status.up && status.motorOn) {
					moveUp();
		
				}
				// ELEVATOR will move DOWNWARDS
				else if (!status.up && status.motorOn)
					moveDown();
				
				if (status.currentFloor==errFloor) {	// check for error after elevator has moved
					chooseFault(errType);
				}
		}
			
		
		try {
			Thread.sleep(2000);	//2s between floors
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//At the end of the invocation, 
		// report the status of the elevator subsystem to the SCHEDULER 
		updateStatusToScheduler();
	
	}
	
	/**
	 * decideNearestRequest finds the nearest destination in the same direction as status.up
	 * and sets the request as the FRONT of the pickUpList
	 */
	
	public int getNearestPickup() {				// gets closest destination according to elevators current floor
		synchronized (status.pickUpList) {
			int smallestDistanceUp=1000,floorUp=-1;
			int smallestDistanceDown=1000,floorDown=-1;
			
				for (int i : status.pickUpList) {
					int distance = Math.abs(status.currentFloor - i);
						if (distance<smallestDistanceUp && i>=status.currentFloor ) {
							smallestDistanceUp = distance;
							floorUp = i;
						}
						if (distance<smallestDistanceDown && i<=status.currentFloor ) {
							smallestDistanceDown = distance;
							floorDown = i;
						}

			}
		
				if (status.up) {
				//	if elevator is going up, return closest floor that is higher than current floor of elevator
						return floorUp; // returns -1, if there are no higher floors, so elevator will go down and find closest floors
						
				}
				
				else  {
				//	if elevator is going down, return closest floor that is under current floor of elevator
				
					return floorDown;
				}
		
		
		}
	}

	
	/**
	 * Updates the status of elevator to scheduler
	 */
	private void updateStatusToScheduler() {
// after elevator moves, sends an updated status to scheduler
		byte[] data = new byte[100];
		try {
			data = sysctrl.convertToBytes(status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), sysctrl.getPort("ElevatorStatusPort"));
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/**
 * 
 *	Handles the Elevator
 */
class ElevatorHandler implements Runnable
{
	private ElevatorSubsystem elevatorSubsystem;
	public ElevatorHandler(ElevatorSubsystem elevatorSubsystem) {
		this.elevatorSubsystem = elevatorSubsystem; 
	}

	@Override
	//Run the ElevatorHandler thread
	public void run() {			
		elevatorSubsystem.run();

		
	}
}



