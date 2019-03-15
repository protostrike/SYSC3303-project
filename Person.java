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
	int originFloor, destFloor;
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
	public Person(String time, int originFloor, int destFloor, boolean up) {
		this.time = time;
		this.originFloor = originFloor;
		this.destFloor = destFloor;
		this.up = up;
	//	times = new Calendar((int)Math.ceil(Integer.parseInt(time.substring(0, 2))), (int)Math.ceil(Integer.parseInt(
		//		time.substring(3, 5))),(int)Math.ceil( Double.parseDouble(time.substring(6, 10))));
	//	times= Calendar.getInstance();
		//times.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time.substring(0,2)));
		//times.set(Calendar.MINUTE,(int)Math.ceil(Integer.parseInt(time.substring(3,5))));
		//times.set(Calendar.SECOND,(int)Math.ceil(Double.parseDouble(time.substring(6,10))));
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