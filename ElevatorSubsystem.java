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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	int elNumber;
	long start,duration;

	//Elevator Status instance for THIS elevator's status
	ElevatorStatus status = new ElevatorStatus();

	//Ready-to-pick-up list
	LinkedList<Person> pickUpList = new LinkedList<Person>(); 

	byte[] statusByte;
	byte lampButton;


	static Sysctrl sysctrl = new Sysctrl();

	public ElevatorSubsystem(int port)
	{
		elNumber = (port==sysctrl.getPort("Elevator1")?1:2);
		
		try {
			sendReceiveSocket = new DatagramSocket(port);
			
			
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String args[])
	{
		Thread elevatorThread,elevatorThread2;
		ElevatorSubsystem c = new ElevatorSubsystem(sysctrl.getPort("Elevator1"));
		ElevatorSubsystem c2 = new ElevatorSubsystem(sysctrl.getPort("Elevator2"));
		ElevatorHandler elevatorHandler = new ElevatorHandler(c);
		ElevatorHandler elevatorHandler2 = new ElevatorHandler(c2);
		elevatorThread = new Thread(elevatorHandler, "New request");
		elevatorThread2 = new Thread(elevatorHandler2, "New request");
		elevatorThread.start();
		elevatorThread2.start();
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
		
		System.out.println("Elevator" +elNumber+" "+ "is stopped at "+status.currentFloor);
		
		status.motorOn = false;		
	}
	
	private void dropOffPerson() {
		stopElevator();
		start = System.currentTimeMillis();
		openDoor();
		Iterator<Person> iter = status.carList.iterator();
		while (iter.hasNext()) {
		    Person p = iter.next();
		    if (p.destFloor == status.currentFloor)
		        iter.remove();
		}
		
		System.out.println("Elevator" +elNumber+": "+" Person dropped off");
			closeDoor();
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
	 * announceFloor is a method that announces the floor upon arriving
	 */
	private void announceFloor() {
		if(status.currentFloor == 1)
			System.out.println(status.currentFloor + "st FLOOR");
		else if(status.currentFloor == 2)
			System.out.println(status.currentFloor + "nd FLOOR");
		else if(status.currentFloor == 3) {
			System.out.println(status.currentFloor + "rd FLOOR");
		}
		else 
			System.out.println(status.currentFloor + "th FLOOR");
		
	}
	
	
	/**
	 * OPENS the elevator doors
	 */
	private void openDoor() {
		status.setDoorOpen(true);
		System.out.println("Elevato"+ elNumber+": "+"Opening door");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Elevator" +elNumber+": "+" Door opened");
	}
	
	/**
	 * CLOSES the elevator doors
	 */
	private void closeDoor() {
		status.setDoorOpen(false);
		System.out.println("Elevato"+ elNumber+": "+"Closing door");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Elevator" +elNumber+": "+" Door closed");
	}
	
	/**
	 * ADDS Persons on floor going SAME DIRECTION as elevator 
	 */
	private void pickUpPerson() {
		
		//iterate through all requests assigned to this elevator
		stopElevator();
		start = System.currentTimeMillis();
		openDoor();
		
		
		for(Person p : pickUpList) {
			
			if(p.originFloor == status.currentFloor) {
				//Person has entered the elevator
				status.carList.add(p);
				//Person is no longer waiting for elevator to arrive
				pickUpList.remove(p);
				System.out.println("Elevator" +elNumber+": "+" Person picked up");
			}
				
		}
		closeDoor();
		duration = System.currentTimeMillis()-start;
		if (duration>3000) {						// if elevator doors take more than 3 seconds to open, fault is detected
			sysctrl.printLog("FAULT DETECTED");
		}
		
	}
	
	/**
	 * turnLampOn is a method that
	 * 
	 * @param destination
	 */
	private void turnLampOn(int destination) {
		
	}

	
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * waitForRequest is a method that waits for the SCHEDULER to send pickup assignments 
	 * to the ELEVATOR subsystem cars
	 */
	
	
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
		
		getRequestThread.start();
		elevatorOp.start();
		
	}
	
	
	
	
	
	
	
	
	private  void waitForRequest() {
		
		//Prepare Datagram packet
		
		
		byte[] data = new byte[500];
		receivePacket = new DatagramPacket(data, data.length);
		
			
			try {
				//Receive request from SCHEDULER
				sendReceiveSocket.receive(receivePacket);
				//add new request to pickUpList
				int len = receivePacket.getLength();
				data = Arrays.copyOfRange(data, 0, len);
				addRequestToList(data);
			
			
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		
	}

	/**
	 *  addRequestToList is a method that converts a Person's request (in form of byte array)
	 *  into a Person object and adds it to the ELEVATOR'S pickUpList
	 *  
	 * @param data - DatagramPacket data
	 */
	private void addRequestToList(byte[] data) {
		synchronized (pickUpList) {
		try {
			//convert request data into Person
		
			Person person = (Person)sysctrl.convertFromBytes(data);
			//add Person to list of people waiting to get picked up
			pickUpList.add(person);
			pickUpList.notifyAll();
		
			
		} 
		
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		}
	}


	/**
	 * operateElevator is a method that will be run indefinitely 
	 * by the Elevator THREADS
	 * 
	 * @throws InterruptedException
	 */
	private  void operateElevator() {
		

		synchronized (pickUpList) {
			while (pickUpList.isEmpty() && status.carList.isEmpty()) {
				try {
					System.out.println("Elevator" +elNumber+": "+"Waiting...");
					pickUpList.wait();
					
						
			
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			start = System.currentTimeMillis();
			moveElevator();
			duration = System.currentTimeMillis()-start;
			if (duration>5000) {								// IF ELEVATOR TAKES MORE THAN 5 SECONDS TO MOVE AGAIN, THERE IS A FAULT
				sysctrl.printLog("FAULT DETECTED");
			}
		
			}
	
	}


	/**
	 * moveElevator is a method that will dictate where the elevator will move 
	 */
	
	
	private void moveElevator() {
		
		int dest = -1;
		if (!status.carList.isEmpty()) {
			status.setMotorOn(true);
		 dest = getNearestDest();
		if (status.currentFloor>dest)
			status.up = false;
		if (status.currentFloor < dest)
			status.up= true;
		if (status.currentFloor ==dest) {
				dropOffPerson();
			
		}
		}
		
		//IF: carList is empty
		else if(status.carList.isEmpty()) {
			
			//IF: motor is NOT on yet
			//if(!status.isMotorOn()) {
				
				//turn motor on
				status.setMotorOn(true);
				
				/**********************************************/
				
				/* SUGGESTION:Elevator should check 
				 * carList Person.destinationFloor 
				 * to decide when to stop ELEVATOR */
				
				//get the nearest distance to request 
				decideNearestRequest();
				
				/**********************************************/

				
				// IF: the request came from a floor ABOVE the current floor of the ELEVATOR
				if(pickUpList.getFirst().originFloor > status.currentFloor) {
					//set the ELEVATOR to go upwards
					status.up = true;
				}
				// IF: the request came from a floor BELOW the current floor of the ELEVATOR
				else if(pickUpList.getFirst().originFloor < status.currentFloor) 
					//set the ELEVATOR to go Downwards
					status.up = false;
				
				//ELSE: The request came from the same floor elevator is currently on
				else {
					
					pickUpPerson();
					
					
				}
		//	}
			
			/*
			else {
				// still have persons in elevator, keep current direction
				// check for person to pick up
				for(Person p : pickUpList) {
					//
					if(p.getOriginFloor() == status.getCurrentFloor()) {
						openDoor();
						pickUpPerson();
						closeDoor();
					}
				}
			}
			
		*/
		}	
				// ELEVATOR will move UPWARDS
				if(status.up && status.motorOn) {
					moveUp();
					start = System.currentTimeMillis();
				}
				// ELEVATOR will move DOWNWARDS
				else if (!status.up && status.motorOn)
					moveDown();
			
		
		try {
			Thread.sleep(2000);
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
	private void decideNearestRequest() {
		int count = 99;
		Person person = new Person();
		
		for(Person p : pickUpList) {
			//If distance is closer
			if(Math.abs(status.getCurrentFloor() - p.getOriginFloor()) <= count) {
				count = Math.abs(status.getCurrentFloor() - p.getOriginFloor());
				person = p;
			}
		}
		
		turnLampOn(person.destFloor);
		
		//Now that we have found the nearest request
		//Set is as the head of the list
		try {
		
			int index = pickUpList.indexOf(person);
			pickUpList.add(pickUpList.remove(index));
		} 
		catch (NullPointerException e) {
			sysctrl.printLog("Error: Cannot find person in request List");
		}
	}
	
	public int getNearestDest() {
		int count = 99;
		int result=0;
		
		for(Person p : status.carList) {
			//If distance is closer
			if(Math.abs(status.getCurrentFloor() - p.getDestFloor()) <= count) {
				count = Math.abs(status.getCurrentFloor() - p.getDestFloor());
				result = p.getDestFloor();
			}
		}
		return result;
		
	}
	
	/**
	 * 
	 */
	private void updateStatusToScheduler() {
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
 *
 */
class ElevatorHandler implements Runnable
{
	private ElevatorSubsystem elevatorSubsystem;
	public ElevatorHandler(ElevatorSubsystem elevatorSubsystem) {
		this.elevatorSubsystem = elevatorSubsystem; 
	}

	@Override
	public void run() {			
		elevatorSubsystem.run();

		
	}
}



