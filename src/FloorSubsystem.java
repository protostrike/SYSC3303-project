import java.io.*;
import java.net.*;
import java.util.*;

public class FloorSubsystem {
	
	int numFloors =10;								// number of floors in building
	ArrayList<floor> floors;
	LinkedList <Person> requests;
	DatagramPacket sendPacket,receivePacket;
	DatagramSocket receiveSocket, sendSocket;
	String upOrDownPressed ;
	String upOrDownLamp;
	String direction; //of elevator
	String time;
	Scanner x;
	int currentFloor;
	int desiredFloor;
	
	public void getData(File f) {                      //create request
		
		try {
		x = new Scanner(f);
		}catch(Exception e) {
			System.out.println("File doesnt exist");
		}
		
		while (x.hasNext()) {
			time = x.next();
			currentFloor = Integer.parseInt(x.next());
			upOrDownPressed = upOrDownLamp = x.next();
			desiredFloor = Integer.parseInt(x.next());
			
			Person p = new Person(time,currentFloor,desiredFloor,upOrDownPressed.trim().equals("up")?true:false);
			
			floors.get(currentFloor-1).setButtonPressed(upOrDownPressed);
		     System.out.println("floor "+currentFloor+" pressed button "+upOrDownPressed+" to floor "+desiredFloor);//updates buttons pressed per floor
			floors.get(currentFloor-1).requests.add(p);				      // adds request to floor requests		
			requests.add(p);                                              // add request to subsystem requests
		}
		
		
	}
	
	private byte[] convertToBytes(Object object) throws IOException {
	    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
	         ObjectOutput out = new ObjectOutputStream(bos)) {
	        out.writeObject(object);
	        return bos.toByteArray();
	    } 
	}
	
	private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
	    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	         ObjectInput in = new ObjectInputStream(bis)) {
	        return in.readObject();
	    } 
	}
	
	
	
	
	public FloorSubsystem()  {
		
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(5010);
		}catch (SocketException se) {   
	        se.printStackTrace();
	        System.exit(1);
	     }
		
		floors = new ArrayList<floor>();
		requests = new LinkedList<Person>();
		
		
		for (int i=0;i<numFloors;i++) {                     // initialize all floors
			floors.add(new floor(i+1));
		}
	
		

		File f = new File("data.txt");
		getData(f);
		
		
		
	}
		
	public synchronized void start() {
		            while (!requests.isEmpty())  {    // sending the first request and so on...
				Person p = requests.remove();
		byte msg[]=null;
		  try {
				msg = this.convertToBytes(p);
		      } catch (IOException e) {
				e.printStackTrace();
		      }
		  
		  
		try {
		  sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(), 5000);
		}catch (UnknownHostException e) {
	        e.printStackTrace();
	        System.exit(1);
	     }
		
		System.out.println("Floor: Sending packet to Scheduler...:");
		System.out.println(p.toString());
		
		  try {
		        sendSocket.send(sendPacket);
		     } catch (IOException e) {
		        e.printStackTrace();
		        System.exit(1);
		     }
		  	System.out.println("Floor "+ p.getOriginFloor()+" is pressed "+( p.up?"up":"Down"));
		  	System.out.println("waiting...");
		
		     
		     
		   
		    
		     byte data[] = new byte[500];
		     receivePacket = new DatagramPacket (data,data.length);
		     
		     try { 
		         receiveSocket.receive(receivePacket);
		      } catch(IOException e) {
		         e.printStackTrace();
		         System.exit(1);
		      }
		     System.out.println("Packet received\n");
		     
		     int len = receivePacket.getLength();
		     data = Arrays.copyOfRange(data,0,len);
		     
		  ElevatorStatus es = new ElevatorStatus();
		  
		  try {
			  es = (ElevatorStatus) convertFromBytes(data);
		  }catch(ClassNotFoundException e){
			  e.printStackTrace();
		  }
		  catch(IOException e2){
		e2.printStackTrace();
		  }
		     
		  direction = (es.up?"up":"Down");
		  for (int i=0;i<numFloors;i++) {                                        // update all floor arrow lamps
			  floors.get(i).direction=direction;
		  }
		  
		  System.out.println(es);
		
	
		
		 
			
		            }
			
		
		//sendSocket.close();
		//receiveSocket.close();
		
	}
	
	 public static void main( String args[] )
	   {
		 
	      FloorSubsystem f = new FloorSubsystem();
	      floorHandler h = new floorHandler(f);
	      new Thread(h).start();
	   }
	
	

}




class floorHandler implements Runnable 
{
	private FloorSubsystem floorSubsystem;
	public floorHandler(FloorSubsystem floorSubsystem) {
		this.floorSubsystem = floorSubsystem; 
	}
	
	@Override
	public void run() {
		while (true) {
			floorSubsystem.start();
		}
		
	}
}

	
	
	
	

