import java.util.*;

/**
 * The Floor class is used to represent floors when 
 * requesting and interacting with the Elevator Simulation
 */
public class floor {

	//Class variables

	ArrayList<Person> requests;
	String upOrDownPressed ;
	String upOrDownLamp;
	String direction; //of elevator
	int floorNum;
	Thread s1;


	/**
	 * Non-default Constructor creates and initializes floor object
	 * 
	 * @param n - floor number
	 */
	public floor(int n) {
		floorNum = n;
		requests = new ArrayList<Person>();
		upOrDownPressed = null;
		upOrDownLamp = null;
		direction = null;

	}

	/**
	 * setButtonPressed sets the status of the Floor's call buttons 
	 * 
	 * @param x - the sets the button of the requested floor
	 */
	public void setButtonPressed(String x) {
		this.upOrDownPressed = this.upOrDownLamp = x;
	}

	/**
	 * toString() represents floor status as string
	 */
	public String toString() {
		return "floor#: "+ floorNum+ " Button pressed " + upOrDownPressed + '\n';
	}

}