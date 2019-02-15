import java.io.Serializable;

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

	int currentFloor;
	boolean motorOn = false, up, inUse, doorOpen=false;


	/***
	 * Default Contructor of creates object of type ElevatorStatus 
	 */
	public ElevatorStatus() {

		//No assignments
	}


	/***
	 * 
	 * Non-default Contructor of creates object of type ElevatorStatus 
	 * 
	 * @param currentFloor - the desired floor
	 * @param motorOn - whether the motor is on or not
	 * @param up - whether the direction of travel is on or not
	 */
	public ElevatorStatus(int currentFloor, boolean motorOn, boolean up) {

		super();
		this.currentFloor = currentFloor;
		this.motorOn = motorOn;
		this.up = up;
		this.inUse= motorOn;

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
