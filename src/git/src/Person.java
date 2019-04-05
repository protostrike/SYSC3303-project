import java.sql.Time;
import java.util.Calendar;

/**
 * Person Class represents a person attempting to use the elevator in the Simulation
 */
public class Person  implements java.io.Serializable{


	// Class Variables
	
	public Calendar getTimes() {
		return times;
	}

	public void setTimes(Calendar times) {
		this.times = times;
	}
	
	
	Calendar times;
	String time;
	int originFloor, destFloor,errType,errFloor;
	boolean up;

	/**
	 * Default constructor creates Person object
	 */
	public Person() {
	}

	/**
	 * 
	 * Non-default cnstructor creates Person 
	 * 
	 * @param time - the time request is made
	 * @param originFloor - source floor of the request
	 * @param destFloor - destination of person
	 * @param up - does this person want to go up or not?
	 */
	public Person(String time, int originFloor, int destFloor, boolean up,int errType,int errFloor) {
		this.time = time;
		this.originFloor = originFloor;
		this.destFloor = destFloor;
		this.up = up;
		this.errType=errType;
		this.errFloor=errFloor;
	}

	//////////////	ACCESSOR AND MUTATOR METHODS	/////////////////
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