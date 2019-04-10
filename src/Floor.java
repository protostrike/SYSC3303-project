import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * The Floor class is used to represent floors when 
 * requesting and interacting with the Elevator Simulation
 */
public class Floor implements Runnable {

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
	
	
	//Utility class used to convert objects to and from bytes
	Sysctrl sysctrl = new Sysctrl();

	//socket to send and receive from Scheduler
	private DatagramSocket sendReceiveSocket;

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
	 * @throws SocketException 
	 */
	public Floor(int floorNumber) throws SocketException {
		
		//Floor number
		this.floorNumber = floorNumber;
		//list of request pending on this floor
		this.requests = new ArrayList<Person>();
		
		//Establish socket port corresponding to the Floor#
		sendReceiveSocket = new DatagramSocket( sysctrl.getPort("FloorBasePort") + floorNumber);
	}

	

	// Accessor Methods
	public ArrayList<Person> getRequests(){
		return this.requests;
	}

	public int getFloorNumber(){
		return this.floorNumber;
	}
	
	public FloorCallPanel getFloorPanel() {
		return this.floorPanel;
	}

	
	
	
	/**
	 * This method takes a Person obj as argument and returns DatagramPacket
	 * containing the request info of the person
	 * 
	 * 
	 * @param p - Person who is waiting on floor for elevator
	 * 
	 * @return - DatagramPacket containing request info of the Person
	 * 
	 * @throws IOException
	 */
	public DatagramPacket prepareRequest(Person p) throws IOException {
		
		//Update the panel (appropriate lamp turns on)	
		//this.floorPanel.makeRequest(p.isUp());

		//convert Person object into byte[]
		byte[] requestMessage = sysctrl.convertPersonToBytes(p);
		
		//initialize to null (preventing error)
		DatagramPacket request = null;
				
		//prepare request packet to be sent to Scheduler
		try {
			request = new DatagramPacket(requestMessage, requestMessage.length, InetAddress.getLocalHost(),sysctrl.getPort("Scheduler<--Floors"));
			
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		return request;
		
	}
	
	public void sendRequestToScheduler(Person p) throws IOException {
		
		sendReceiveSocket.send(prepareRequest(p));
		
		//DEBUGGING
		System.out.println("Floor #" + this.floorNumber + ": sent request to Scheduler \n");
		
		
	}
	
	/*
	 * adds a person to the list of pending request on the Floor
	 */
	public void addRequest(Person p){
		requests.add(p);
	}


	/*
	 * toString() represents floor status as string
	 */
	public String toString() {
		return "Floor #"+ floorNumber + '\n';
	}
	
	
	
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	//////////////////MAIN AND RUN METHOD///////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
	/** 					RUN METHOD
	 * run() makes use of Threads
	 *************************************************************/
	@Override
	public void run() {
		
		Thread RequestReceiver = new Thread() {
			
		};
		
		
		
		Thread	Updater = new Thread() {
			public void run() {
				
				while(true) {
					//update changes made to gui
				}
			}
		};
		
		
		
	}
		

}