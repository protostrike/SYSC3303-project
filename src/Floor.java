import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * The Floor class is used to represent floors when 
 * requesting and interacting with the Elevator Simulation
 */
public class Floor {

	/*
     * 
     * 
     * Status information of a Floor
     * 
     * 
     * 
     *************************************************************/
    
	//Number of the Floor
	private int floorNumber;

	//List of Persons on floor that are waiting for an Elevator
	private ArrayList<Person> requests;
	
	//private DatagramSocket floorSocket;
	
	//private 


	/*
     * 
     * 
     * Physical components of the Floor
     * 
     * 
     **************************************************************/
    
	
	private FloorCallPanel floorPanel;

	


	/**
	 * Non-default Constructor creates and initializes floor object
	 * 
	 * @param n - floor number
	 */
	public Floor(int floorNumber) {

		this.floorNumber = floorNumber;
		this.requests = new ArrayList<Person>();


	}

	

	///////////////ACCESSOR//////////////////
	public ArrayList<Person> getRequests(){
		return this.requests;
	}

	public int getFloorNumber(){
		return this.floorNumber;
	}
	/////////////////////////////////////////
	
	/*
	 * prepare datagramPacket that is sent to the Scheduler
	 * 
	 */
	public DatagramPacket callElevator(Person p) throws IOException {
		
		//Update the panel (appropriate lamp turns on)	
		this.floorPanel.makeRequest(p.isUp());
		
		//initialize to null (preventing error)
		Sysctrl helper = null;
		byte[] requestMessage = helper.convertToBytes(p);
		
		//initialize to null (preventing error)
		DatagramPacket request = null;
		
		//prepare request packet
		try {
			request = new DatagramPacket(requestMessage, requestMessage.length, InetAddress.getLocalHost(),helper.getPort("floorSendPort"));
			
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		return request;
		
	}
	
	
	/*
	 * adds a person to the list of pending requeston the Floor
	 */
	public void addRequest(Person p){
		requests.add(p);
	}




	/*
	 * toString() represents floor status as string
	 */
	public String toString() {
		return "floor#: "+ floorNumber + '\n';
	}

}