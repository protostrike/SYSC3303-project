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
	s1 = new Thread(sensor);
	s1.start();
	
}


public void setButtonPressed(String x) {
	upOrDownPressed = upOrDownLamp = x;
}

public String toString() {
	return "floor#: "+ floorNum;
}




}