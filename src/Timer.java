
public class Timer {

	//Attributes of the Timer
	
	//Reference to the Elevator
	private Elevator elevator;
	
	//Start time, time duration and stop time of timer
	private long start, time, stop;
	
	//
	

	/**
	 * 
	 * Timer constructor has only one 
	 * 
	 * @param e - elevator 
	 */
	public Timer(Elevator e) {
		this.elevator = e;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTime() {
		return time = stop - start;
	}
	
	public void reset() {
		time = 0; 
	}
	
}


