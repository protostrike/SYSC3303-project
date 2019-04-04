/***
 * Elevator class represents the elevator car object 
 * in the Elevator system simulation
 * 
 * ElevatorSubsystem will hold an ArrayList of Elevators
 * 
 * @author Reginald Pradel
 * 
 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Elevator class represent the elevator car object.
 * 
 * The Elevator Subsystem will contain a list of Elevators
 * 
 */
public class Elevator implements Runnable{

    //Attribute of an Elevator Car

    
    
    

    /*
     * 
     * 
     * Status information of an Elevator
     * 
     * 
     * 
     *************************************************************/
    
	//Identification of elevator car ; 
	// - will be used to generate port number
	// - will be used to label Elevator Thread
    private static int elevatorID;
	
	//Floor where the Elevator is located
    private int currentFloor;
    
    // Represents direction of travel
    private boolean goingUp;
    
    // Status object that will be converted into bytes and sent to Scheduler
    private RedefinedStatus status;
    
    //Passengers on board of elevator
    private ArrayList<Person> passengers;
    
    // Each elevator will have list of floors to stop at
    private ArrayList<Floor> pickUpList,dropOffList;
    
    
    private DatagramSocket receiveSocket,sendSocket;
    private int portNumber;
    
    // Utility class ; handles conversions object <--> bytes <--> string
    static Sysctrl systemControl;

    
    /*
     * 
     * 
     * Physical components of the Elevator
     * 
     * 
     **************************************************************/
    
        
    //Door of the elevator
    private Door elevatorDoor;
    
    //Motor of the elevator Car
    private Motor motor;
    
    //Elevator Car Panel
    private ElevatorCarPanel carPanel;

    
    

    /**
     * Constructor used to create an object of type Elevator
     */
    public Elevator(int elevatorID, int startingFloor) {
        
        this.elevatorID = elevatorID;

        this.currentFloor = startingFloor;

        this.elevatorDoor = new Door(false);
        
        
        this.status = new RedefinedStatus(elevatorID, startingFloor, true, false);
        


        //Utitly class for converting into and from bytes
        this.systemControl = new Sysctrl();
    }
    
    
   


    /** 
     * 
     * 
     * 
     * Methods used to operate Elevator car 
     * 
     * 
     ********************************************/
    
    /*
     * Used to move the elevator by one floor
     * up or down depending on the input isUp
     * 
     * @param isUp - true if we want to move up, false otherwise
     * 
     */
    public void move(boolean isUp){
        
        motor.setDirection(isUp);
        
        motor.turnOn();
        motor.accelerate();

        
        while(!isStopRequired()){
            
        	motor.coast();
            
            //if the current direction is up
            if(goingUp){     
                currentFloor++;
            }
            else{            
                currentFloor--;
            }
            
        }

        if(isStopRequired()){
            motor.deccelerate();
            motor.turnOff();
        }
    }
    
    
    /*
     * Allow passengers to enter Elevator
     * 
     * Tell floor to send This Elevator the list of people waiting on that floor
     * If the person is wants to go the same direction the motor is set to
     * ADD it to the dropOffList and remove it from the pickUpList
     * 
     */
    public void pickUpPassengers(){

        //We find the floor from pickupList
    	//(We initialize just to avoid NULL error)
        Floor tempFloor = new Floor(0);
        
        //local variable for referencing
        int floorNumber;
        
        for(Floor f : pickUpList){
        	        	
            if(f.getFloorNumber() == this.currentFloor){
                tempFloor = f;
            }
        }

        
        //we add all those in the floor's request list 
        //that are going the same direction
        for( Person p : tempFloor.getRequests() ){
            if(p.isUp() == this.goingUp){
                pickUpList.remove(p.getOriginFloor());
                dropOffList.add(new Floor(p.getDestFloor()));
            }
                
        }
    }

    
    /*
     * Allow passengers to exit Elevator
     * 
     * Tell floor to send This Elevator the list of people waiting on that floor
     * If the person has arrived to their destination: Person exits elevator
     * remove it from the dropOffList
     * 
     */
    public void dropOffPassengers(){
       
        //We find the floor from dropOffList
        for(Floor f : dropOffList){
            if(f.getFloorNumber() == this.currentFloor){
                
                //remove the request from dropOffList
                dropOffList.remove(f);

            }
        }
    }
    
    
    
    /*
     * 
     * Checks current location of elevator and returns true 
     * if stop is required, false otherwise
     * 
     */
    public boolean isStopRequired(){

        int temp;
        boolean stop = false;

        //Check to see if stop is required
        
        for(Floor f : dropOffList){
            if(currentFloor == f.getFloorNumber()){
                //make stop here
                stop = true;
            }
        }
        
        for(Floor f : pickUpList){
            if(currentFloor == f.getFloorNumber()){
                //make stop here
                stop = true;

            }
        }

        return stop;
    
    }
    
    
    /*
     * 
     * 
     * Used to open the Elevator door
     * 
     * 
     */
    public void openDoor(){
        
        System.out.println("Elevator " + elevatorID + " doors now opening");
        elevatorDoor.operateDoor(true);
    }

    
    /*
     * 
     * 
     * used to close the Elevator door
     * 
     * 
     */
    public void closeDoor(){
        
        System.out.println("Elevator " + elevatorID + " doors now closing");
        elevatorDoor.operateDoor(false);
        
    }

    
    /***************************ACCESSOR METHODS*******************************/
    
    
    /*
     *  returns reference the Elevator's motor object
     * 
     * @return - Motor of the elevator
     */
    public Motor getMotor(){
        return this.motor;
    }
    
    /*
     * returns the Elevator's ID 
     * 
     * @return - int representing Elevator ID
     */
    public int getElevatorID() {
    	return this.elevatorID;
    }
    
    /*
     * returns the direction of the Elevator
     * 
     * @return - true if going up, false otherwise
     */
    public boolean isGoingUp() {
    	return motor.getDirection();
    }
    
    /*
     * reutrns the list of Floors that the Elevator will pick up Persons
     * 
     * @return - ArrayList<Floor>
     */
    public ArrayList<Floor> getPickUpList(){
    	return this.pickUpList;
    }
    
    /*
     * reutrns the list of Floors that the Elevator will drop off Persons
     * 
     * @return - ArrayList<Floor>
     */
    public ArrayList<Floor> getDropOffList(){
    	return this.dropOffList;
    }
       

    
    /**
     * RUN
     */
    public void run() {
    	
    }
}




