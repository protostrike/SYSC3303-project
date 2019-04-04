import java.io.Serializable;
import java.util.*;

public class RedefinedStatus implements Serializable{
    
    //Attributes : An Elevator will need to transmit the following info

	//Elevator ID
	private int elevatorID;
	
	// Floor where the Elevator is currently on
    private int currentFloor;

    // The list of floors Elevator will stop at
    private ArrayList<Floor> pickUpList,dropOffList;

    // The direction Elevator is set to
    private boolean goingUp;
    
    // whether the Elevator door is open or not
    //(This is not really need for scheduling)
    private boolean doorOpen;

    /**
     * Constructor for elevator status
     */
    public RedefinedStatus(int elevatorID, int currentFloor, boolean goingUp, boolean doorOpen ){

    	
    	this.elevatorID =  elevatorID;
        this.currentFloor = currentFloor;
        this.goingUp = goingUp;
        this.doorOpen = doorOpen;

    }

    /////////////// ACCESSOR METHODS /////////////////
	
	/**
	 * 
	 * isDoorOpen() will allow the elevatorSubystem 
	 * to communicate when the doors are OPEN or CLOSED
	 * 
	 * 
	 * @return - true when doors are open, false other wise
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

	

	

	/**
	 * isUp() returns the direction of travel
	 * 
	 * @return - whether the direction is set to UP or not
	 */

	public boolean isUp() {
		
		return goingUp;
		
	}
	


    /***
	 * The toString() method is overriden to concatenate 
	 * the different elements of the ElevatorStatus into 
	 * one String
	 * 
	 */
	@Override
	public String toString() {
		return "ElevatorStatus [currentFloor = " + this.currentFloor + ", up = " + this.isUp() +"]";
	}


}