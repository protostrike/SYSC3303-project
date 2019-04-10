import java.io.Serializable;

import java.util.*;

/**
 * 
 * Class Elevator Status implements the serialize serializable 
 * so that the status of the elevator cars can be represented 
 * as a sequence of bytes 
 * 
 * These bytes allow will transmission of infomation on:
 *  the location of the elevator car,
 *  the activity of the elevator car motor,
 *  the activity of the elevator car doors,
 *  and whether the elevator car is in use.
 * 
 * 
 * 
 **/

public class ElevatorStatus implements Serializable{

	//Class variables
	byte fault;
	int currentFloor;
	boolean motorOn = false, up, inUse, doorOpen=false;
	int openCloseDoorTime;
	int elevatorSpeed;
	//List<Person> carList = new ArrayList<Person>();
	//List<Integer> pickUp = new ArrayList<Integer>();
	//List<Integer> dropOff = new ArrayList<Integer>();
	LinkedList<Integer> pickUpList = new LinkedList<Integer>(); //elevators destintation list
	Map<Integer,ArrayList<Integer>> requests = new HashMap<Integer,ArrayList<Integer>>();	//map of requests <origin floor, dest floors>

	/***
	 * Default Constructor of creates object of type ElevatorStatus 
	 */
	public ElevatorStatus() {
		fault =0;
		currentFloor = 1;
		openCloseDoorTime = 3;
		elevatorSpeed = 5;
		up=true;
	}


	/***
	 * 
	 * Non-default Contructor of creates object of type ElevatorStatus 
	 * 
	 * @param currentFloor - the desired floor
	 * @param motorOn - whether the motor is on or not
	 * @param up - whether the direction of travel is on or not
	 */
	public ElevatorStatus(int currentFloor,  boolean up) {

		super();
		this.currentFloor = currentFloor;
		this.up = up;

	}


	/////////////// ACCESSOR METHODS /////////////////
	
	/**
	 * 
	 * isDoorOpen() will allow the elevatorSubystem 
	 * to communicate when the doors are OPEN or CLOSED
	 * 
	 * 
	 * @return
	 */
	public boolean isDoorOpen() {
		return doorOpen;
	}
	

	/**
	 * getCurrentFloor() returns the current floor number
	 * 
	 * @return - number of the floor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}

	

	/***
	 *  isInUse() return whether the elevator is occupied or not
	 * 
	 * @return - whether the elevator is occupied or not
	 */
	public boolean isInUse() {
		return inUse;
	}

	/**
	 * isMotorOn() returns the whether the status of the motor
	 * 
	 *  @return - whether the motor is on or not
	 */
	public boolean isMotorOn() {
		return motorOn;
	}
 
	/**
	 * setMotorOn() sets the status of the elevator car motor
	 * 
	 * @param motorOn -  the status of the elevator car motor
	 */
	public void setMotorOn(boolean motorOn) {
		this.motorOn = motorOn;
	}

	/**
	 * isUp() returns the direction of travel
	 * 
	 * @return - whether the direction is set to UP or not
	 */

	public boolean isUp() {
		return up;
	}
	

	/////////////////////////////////////////////////


	

	/////////////// MUTATOR METHODS /////////////////

	/**
	 * setOpenDoor() sets the status of the elevator doors
	 * 
	 * @param doorOpen - boolean of whether eleavator doors are open or not 
	 */
	public void setDoorOpen(boolean doorOpen) {
		this.doorOpen = doorOpen;
	}	
	
	/**
	 *  setCurrentFloor() sets the current floor
	 * 
 	 * @param currentFloor - the desired floor number
 	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}


	/**
	 * setUp() sets the desired direction of the elevator
	 * 
	 * @param up - whether the desired direction is up or down 
	 * 				( true = up | false = down)
	 */
	public void setUp(boolean up) {
		this.up = up;
	}

	/**
	 * setInUse() sets the occupancy of the elevator
 	 * 
 	 * @param inUse - whether elevator is in use or not
 	 */
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	
	public int getFarthestDestination() {
		if(up) {
			int count = 0;
			for(int  p : pickUpList) {
				if(p >= count)
					count = p;
			}
			return count;
			
		}	
	
		else {
			int count = 99;
			for(int p : pickUpList) {
				if(p <= count)
					count = p;
			}
			return count;
			
		}
		
		
	}
	
	//////////////////////////////////////////////////


	/***
	 * The toString() method is overriden to concatenate 
	 * the different elements of the ElevatorStatus into 
	 * one String
	 * 
	 */
	@Override
	public String toString() {
		return "ElevatorStatus [currentFloor=" + currentFloor + ", motorOn=" + motorOn + ", up=" + up + ", inUse="
				+ inUse + "]";
	}


}
