import java.net.*;
import java.util.*;
public class floor {

	
	ArrayList<Person> requests;
	String upOrDownPressed ;
	String upOrDownLamp;
	String direction; //of elevator
	int floorNum;
	arrivalSensor sensor;
	Thread s1;
	
	  


public floor(int n) {
	floorNum = n;
	requests = new ArrayList<Person>();
    upOrDownPressed = null;
	upOrDownLamp = null;
	direction = null;
	new Thread(sensor).start();
	
	
}


public void setButtonPressed(String x) {
	this.upOrDownPressed = this.upOrDownLamp = x;
}

public String toString() {
	return "floor#: "+ floorNum+ " Button pressed " + upOrDownPressed + '\n';
}




}