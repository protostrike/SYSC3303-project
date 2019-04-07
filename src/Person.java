import java.sql.Time;
import java.util.Calendar;
import java.io.*;

/**
 * Person Class represents a person attempting to use the elevator in the Simulation
 */
public class Person  implements Serializable{


	// Attributes of Person	
	Calendar times;
	
	String time;
	
	//Start, destination, fault type and floor where fault occurs
	int originFloor, destFloor, faultType, faultLocation;
	
	//Direction Person wants to go
	boolean up;

	
	

	/**
	 * 
	 * Constructor 1 creates Person 
	 * 
	 * @param time - the time request is made
	 * @param originFloor - source floor of the request
	 * @param destFloor - destination of person
	 * @param up - does this person want to go up or not?
	 */
	public Person(String time, int originFloor, int destFloor, boolean up) {
		
		this.time = time;
		this.originFloor = originFloor;
		this.destFloor = destFloor;
		this.up = up;
	}
	/**
	 * 
	 * Constructor creates object of type Person 
	 * 
	 * @param time - the time request is made
	 * @param originFloor - source floor of the request
	 * @param destFloor - destination of person
	 * @param up - does this person want to go up or not?
	 * 
	 * @param faultType - 0 if no fault
	 * @param faultLoc - location where an assigned fault occurs
	 */
	public Person(String time, int originFloor, int destFloor, boolean up, int faultType, int faultLoc) {
		
		//calling Person() within this constructor didn't work(smelly code)
		this.time = time;
		this.originFloor = originFloor;
		this.destFloor = destFloor;
		this.up = up;
		
		//for handling faults
		this.faultType = faultType;
		this.faultLocation = faultLoc;
	}

	//////////////	ACCESSOR AND MUTATOR METHODS	/////////////////
	
	
	public Calendar getTimes() {
		return times;
	}

	public void setTimes(Calendar times) {
		this.times = times;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getOriginFloor() {
		return originFloor;
	}

	public void setOriginFloor(int originFloor) {
		this.originFloor = originFloor;
	}

	public int getDestFloor() {
		return destFloor;
	}

	public void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}
	/////////////////////////////////////
	

	/**
	 * toString() represents the person's data as a string
	 */
	public String toString()
	{
		return time + " Origin: "+ originFloor + " Dest: " + destFloor + " Going: " + (up?"Going Up":"Going Down");

	}



}