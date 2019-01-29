import java.net.*;
import java.util.*;
public class floor {

	
	ArrayList<Person> requests;
	String upOrDownPressed ;
	String upOrDownLamp;
	String direction; //of elevator
	int floorNum;
	Thread arrivalSensor;
	
	  


public floor(int n) {
	floorNum = n;
	requests = new ArrayList<Person>();
    upOrDownPressed = null;
	upOrDownLamp = null;
	direction = null;
	arrivalSensor =new Thread();
	arrivalSensor.start();
	
}


public void setButtonPressed(String x) {
	upOrDownPressed = upOrDownLamp = x;
}

public String toString() {
	return "floor#: "+ floorNum;
}




}