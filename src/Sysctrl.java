import java.net.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * The Sysctrl (system control) class is mainly meant to be instantiated in a static manner 
 * to perform conversions from object to bytes and vice versa
 * 
 * as well as printLog() calls when debugging the system
 * 
 * @author reginaldpradel
 *
 */
public class Sysctrl {
	
	
	/*
	 *	The following is a port map of the Elevator system
	 *
	 * 
	 */
	@SuppressWarnings("serial")
	HashMap<String, Integer> portsMap = new HashMap<String, Integer>(){{
		
		
		/* Elevator */
		//Number of Elevators in the system
		put("#_OF_ELEVATORS", 4);
		//Elevator Subsystem port
		put("ElevatorSubsystem", 3333);
		//port for receiving status check requests
		put("ElevatorStatusPort",6969);
		//Elevator #x = base + x
		put("ElevatorBasePort",5500);	
		
		
		/* Scheduler */
		//receiving from floor subsystem
		put("Scheduler<--Floors", 6666);
		//receiving from elevator subsystem
		put("Scheduler<--Elevators", 7777);
		//port for receiving status objects from Elevator system
		put("SchedulerStatusPort",9696);

		
		/*Floor */
		//Number of Floors in the system
		put("#_OF_FLOORS", 22);
		
		put("FloorSubsystem", 2222);
		
		//Floor #x = base + x
		put("FloorBasePort",8800);
			
	}};
	
	
	/**
	 * Returns this Person object to a byte array
	 * 
	 * @return - byte[]
	 */
	public byte[] convertPersonToBytes(Person p) {
		return p.toString().getBytes();
	}
	
	/**				
	 * Converts byte array into Person Object
	 * 
	 * 
	 * @param b
	 * @return
	 */
	public Person convertBytesToPerson(byte[] b) {		
		
		boolean up;
		
		String msgParts[] = new String(b).split(" ");
			
		//time
		String time = msgParts[0];
		
		//origin floor
		int origin = Integer.parseInt(msgParts[1]);
		
		//direction
		if(msgParts[2].equals("up")) {up = true;}
		else {up = false;}
		
		//destination
		int dest = Integer.parseInt(msgParts[3]);
		
		//fault type
		int faultType = Integer.parseInt(msgParts[4]);
		
		//fault location
		int i = Integer.parseInt(msgParts[5].trim());
		
		int faultLocation = i;//Integer.parseInt(msgParts[5]);
		
		//System.out.println("Reciving: "+Arrays.toString(msgParts));
		
		
		return new Person(time,origin,dest,up,faultType,faultLocation);	
	}
	
	/**
	 * convertStatusToBytes() converts an input RedefinedStatus 
	 * into a byte array
	 * 
	 * @return - byte[] - byte representation of status
	 */
	public byte[] convertStatusToBytes(RedefinedStatus status) {
		return status.toString().getBytes(); 
	}
		
	public RedefinedStatus convertBytesToStatus(byte[] b) {
		
		String statusParts[] = new String(b).split(" ");
		
		
		int elevatorID = Integer.parseInt(statusParts[0].trim()); 
		int currentFloor = Integer.parseInt(statusParts[1].trim());
		
		boolean goingUp, doorOpen, busy, offline; 

		
		if( statusParts[2].trim().equals("up") ) {goingUp = true;}
		else {goingUp=false;}
		
		if( statusParts[3].trim().equals("open") ) {doorOpen = true;}
		else {doorOpen=false;}
		
		if(statusParts[4].trim().equals("busy")) {busy = true;}
		else {busy = false;}
		
		if(statusParts[5].trim().equals("offline")) {offline = true;}
		else {offline = false;}
		
		return new RedefinedStatus(elevatorID, currentFloor, goingUp, doorOpen, busy, offline);
	}
	
	/**
	 * combineStatusList() conbines a list of RedefinedStatus objects 
	 * into one byte array
	 * 
	 * @param list - list of RedefinedStatus objects
	 * @return byte[] - byte representation of RedefinedStatus object list
	 */
	public byte[] combineStatusList(List<RedefinedStatus> list) {
		String s = new String();
		
		for(RedefinedStatus status : list ) {
			s+=status.toString() + "$";
		}
		
		return s.getBytes();
	}
	
	/**
	 * decdeList() converts what should be a byte representation of a list of statuses 
	 * and return it as an ArrayList
	 * 
	 * @param b - byte representation of RedefinedStatus object list
	 * @return - ArrayList of RedefinedStatus objects
	 */
	public ArrayList<RedefinedStatus> decodeList(byte[] b){
		
		ArrayList<RedefinedStatus> list = new ArrayList<RedefinedStatus>();
		
		String s = new String(b);
		
		String[] statusStrings = s.split("$");
		
		//Make a status for each status string
		for(String str : statusStrings) {
			String[] statusParts = str.split(" ");
				
			//The following parses the data needed to make a status object
			int elevatorID = Integer.parseInt(statusParts[0].trim()); 
			
			int currentFloor = Integer.parseInt(statusParts[1].trim());
			
			boolean goingUp, doorOpen, busy, offline; 

			if( statusParts[2].trim().equals("up") ) {goingUp = true;}
			else {goingUp=false;}
			
			if( statusParts[3].trim().equals("open") ) {doorOpen = true;}
			else {doorOpen=false;}
			
			if(statusParts[4].trim().equals("busy")) {busy = true;}
			else {busy = false;}
			
			if(statusParts[5].trim().equals("offline")) {offline = true;}
			else {offline = false;}
			
			//finally we add our status element
			list.add(new RedefinedStatus(elevatorID, currentFloor, goingUp, doorOpen, busy,offline));
		}
		
		return list;
	}
	

	
	/**
	 * get port number by certain string input
	 * @param request - string indicating which port you want
	 * @return the port number 
	 */
	public int getPort(String request) {
		return portsMap.get(request);
	}
	

	/**
	 * getNumberOfElevators() returns the number of elevators in the system
	 * 
	 * @return - int - number of elevators
	 */
	public int getNumberOfElevators() {
		return portsMap.get("#_OF_ELEVATORS");
	}
	/**
	 * getNumberOfFloors() returns the number of floors in the system
	 * 
	 * @return - int - number of floors in system
	 */
	public int getNumberOfFloors() {
		return portsMap.get("#_OF_FLOORS");
	}
	

	public void printLog(String s) {
		Date date = new Date();
		long time = date.getTime();
		String currentTime = new Timestamp(time).toString().split(" ")[1];
		System.out.println("[" + currentTime + "] " + s);
	}
	
	public void printLog(Object o) {
		Date date = new Date();
		long time = date.getTime();
		String currentTime = new Timestamp(time).toString().split(" ")[1];
		System.out.println("[" + currentTime + "] " + o);
	}
}
