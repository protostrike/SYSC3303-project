import java.io.*;
import java.net.*;
import java.util.*;

public class FloorSubsystem {
	
	int numFloors =10;
	ArrayList<floor> floors;
	LinkedList <Person> requests;
	DatagramPacket sendPacket,receivePacket;
	DatagramSocket receiveSocket, sendSocket;
	String upOrDownPressed ;
	String upOrDownLamp;
	String direction; //of elevator
	Boolean arrivalSensor;
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

			floors.get(currentFloor-1).requests.add(p);
			requests.add(p);
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
			receiveSocket = new DatagramSocket(23);
		}catch (SocketException se) {   
	        se.printStackTrace();
	        System.exit(1);
	     }
		
		floors = new ArrayList<floor>();
		requests = new LinkedList<Person>();
		
		
		for (int i=0;i<numFloors;i++) {                     // initialize all floors
			floors.add(new floor(i+1));
		}
		System.out.println(floors);
		

		File f = new File("data.txt");
		getData(f);
		
	
		
		
	}
		
	public void start() {
		while (!requests.isEmpty()) {                    // sending the first request and so on...
				Person p = requests.remove();
		byte msg[]=null;
		  try {
				msg = this.convertToBytes(p);
		      } catch (IOException e) {
				e.printStackTrace();
		      }
		  
		  
		try {
		  sendPacket = new DatagramPacket(msg,msg.length,InetAddress.getLocalHost(), 24);
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

		     System.out.println("Packet sent.\n");
		     System.out.println("waiting for packet from Scheduler...");
		     
		     
		    
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
		  ElevatorStatus es = new ElevatorStatus();
		  
		  try {
			  es = (ElevatorStatus) convertFromBytes(Arrays.copyOfRange(data,0,len));
		  }catch(ClassNotFoundException e){
			  e.printStackTrace();
		  }
		  catch(IOException e2){
		e2.printStackTrace();
		  }
		     
		  System.out.println(es);
		  
		  if (es.up && es.motorOn)
			  direction = "up";
		  else if(!es.up && es.motorOn) {
			  direction ="down";
		  }
			  
		  
	
		
		 
			
			
			
		}
		sendSocket.close();
		receiveSocket.close();
		
	}
	
	 public static void main( String args[] )
	   {
	      FloorSubsystem f = new FloorSubsystem();
	      f.start();
	   }
	
	

}
