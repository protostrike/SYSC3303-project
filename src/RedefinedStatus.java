import java.io.Serializable;
import java.util.*;

/**
 * RedefinedStatus is a class based on the previously used ElevatorStatus with the same purpose:
 * to be transmitted to the Scheduler to be examined and compared to other status objects 
 * from the elevators in Elevator Subsystem.
 *
 * Each Elevator object will possess a RedefinedStatus attribute
 *
 * @author reginaldpradel
 *
 */
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

    //whether the elevator is currently servicing a request
    private boolean busy;
    
    // whether the elevator is experiencing a fault
    private boolean offline;
    
    /**
     * Constructor
     */
    public RedefinedStatus(int elevatorID, int currentFloor, boolean goingUp, boolean doorOpen, boolean busy, boolean offline){

    	
    	this.elevatorID =  elevatorID;
        this.currentFloor = currentFloor;
        this.goingUp = goingUp;
        this.doorOpen = doorOpen;
        this.busy = busy;
        this.offline = offline;

    }

    
    /////////////// ACCESSOR METHODS /////////////////
	

	/**
	 * getID() returns the elevatorID
	 * 
	 * @return
	 */
	public int getID() {
		return elevatorID;
	}
    
	/**
	 * 
	 * isDoorOpen() will allow the elevatorSubystem 
	 * to communicate when the doors are OPEN or CLOSED
	 * 
	 * 
	 * @return - true when doors are open, false other wise
	 */
	public boolean isDoorOpen() {
		return this.doorOpen;
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
		
	/**
	 * isBusy()  returns boolean indicating whether elevator 
	 * is busy with another request or not
	 * 
	 * @return
	 */
	public boolean isBusy() {
		return this.busy;
	}
	
	/**
	 * 
	 */
	public boolean isOffline() {
		return this.offline;
	}
	
	
	
    /////////////// MUTATOR METHODS /////////////////

	public void setBusy(boolean b) {
		this.busy = b;
	}
	
	
	
	
    /***
	 * The toString() method is overriden to concatenate 
	 * the different elements of the ElevatorStatus into 
	 * one String
	 * 
	 */
	@Override
	public String toString() {
		return this.currentFloor +" "+ (goingUp?"up":"down") +" "+ (doorOpen?"open":"closed")+" "+(offline?"offline":"online");
	}


}