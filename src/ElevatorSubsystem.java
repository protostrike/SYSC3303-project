// SimpleEchoClient.java
// This class is the client side for a simple echo server based on
// UDP/IP. The client sends a character string to the echo server, then waits 
// for the server to send it back to the client.
// Last edited January 9th, 2016

import java.io.*;
import java.net.*;

public class ElevatorSubsystem {

   DatagramPacket sendPacket, receivePacket;
   DatagramSocket sendReceiveSocket;
   int currentFloor = 1;
   boolean motorOn = false, up=false, doorOpen= false;
   int openCloseDoorTime = 3;
   int elevatorSpeed = 5;
   byte[] statusByte;
   byte lampButton;


   public ElevatorSubsystem()
   {
      try {
    	  sendReceiveSocket = new DatagramSocket(5005);
      } catch (SocketException se) {   // Can't create the socket.
         se.printStackTrace();
         System.exit(1);
      }
   }
   
   private byte[] convertToBytes(Object object) throws IOException {
	    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
	         ObjectOutput out = new ObjectOutputStream(bos)) {
	        out.writeObject(object);
	        return bos.toByteArray();
	    } 
	}
   
     
   public synchronized void process()
   {
	   String requestType = "";
	   ElevatorStatus status;
	  // byte[] msg= " ".getBytes();
	   byte data[] = new byte[1000];
	   receivePacket = new DatagramPacket(data, data.length);
	//   try {
		  // sendReceiveSocket.setSoTimeout(2);
		   try {
			   sendReceiveSocket.receive(receivePacket);
		   } catch (IOException e) {
			// TODO Auto-generated catch block
			   return;
		
		   }
	
		
	
		 
			  switch(data[0])
			  {
			  	case (byte)0:
					  status = new ElevatorStatus(currentFloor, motorOn, up);
				  	  requestType = "status requested";		     
				  	  try {
				  		  data = convertToBytes(status);
				  	  } catch (IOException e1) {
				  		  e1.printStackTrace();
				  	  }
				  	  break;
				case (byte)1:
					moveUp();
					break;
				case (byte)2:
					moveDown();
					break;
				case (byte)3:
					start();
					break;
				case (byte)4:
					stop();
					break;
				case (byte)5:
					openDoor();
					break;
				case (byte)6:
					closeDoor();
					break;
				case (byte)7:
					lampButton = data[1];
					turnLampOn();
					break;
				case (byte)8:
					turnLampOff();
					break;
			  }//switch
	
		  
		  
		  if(requestType.equalsIgnoreCase("status requested") )				// if status is asked for..
		  {
			  sendPacket =new DatagramPacket(data, data.length,
			        receivePacket.getAddress(), 5002);
			
	
			    
			  // Send the datagram packet to the client via the send socket. 
			  try {
			     sendReceiveSocket.send(sendPacket);
			  } catch (IOException e) {
			     e.printStackTrace();
			     System.exit(1);
			  }
			
			
			  
			  // We're finished, so close the socket.
			  //sendReceiveSocket.close();
			  requestType="";
		  }
   }
 

   
   //elevator updates scheduler of its current floor whenever it moves or starts the engine
   
   
   
   
  
   public void moveUp()
   {
	   
	   try {
		Thread.sleep(elevatorSpeed * 1000);
	   } catch (InterruptedException e) {
		e.printStackTrace();
	   }
	   this.up= true;
	   currentFloor++;
	   System.out.println("moving up to floor"+currentFloor);
	   
	   byte [] floorArival = {(byte)currentFloor};
	   try {
		sendPacket = new DatagramPacket(floorArival,floorArival.length,InetAddress.getLocalHost(),5001);
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	   
	   try {
		sendReceiveSocket.send(sendPacket);
	} catch (IOException e) {
		e.printStackTrace();
	}
	   
	   

   }
   public void moveDown()
   {
	  
	   try {
		Thread.sleep(elevatorSpeed * 1000);
	   } catch (InterruptedException e) {
		e.printStackTrace();
	   }
	   this.up= false;
	   currentFloor--;
	   System.out.println("moving down to floor"+ currentFloor);
	   byte [] floorArival = {(byte)currentFloor};
	   try {
		sendPacket = new DatagramPacket(floorArival,floorArival.length,InetAddress.getLocalHost(),5001);
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}                    									
	   
	   try {
		sendReceiveSocket.send(sendPacket);
	} catch (IOException e) {
		e.printStackTrace();
	}
	   
	   
   }
   public void start()
   {
	   System.out.println("Starting engine (at floor"+currentFloor+")");
	   this.motorOn= true;
	   
	   byte [] floorArival = {(byte)currentFloor};
	   try {
		sendPacket = new DatagramPacket(floorArival,floorArival.length,InetAddress.getLocalHost(),5001);
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	   
	   try {
		sendReceiveSocket.send(sendPacket);
	} catch (IOException e) {
		e.printStackTrace();
	}
   }
   public void stop()
   {
	   System.out.println("Stopping engine at floor"+currentFloor);
	   this.motorOn= false;
	   openDoor();
	   closeDoor();
	   
	   
	
	  
   }
   public void closeDoor()
   {
	   System.out.println("closing door");
	   try {
		Thread.sleep(openCloseDoorTime * 1000);
	   } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	   this.doorOpen= false;
	   System.out.println("door closed");
   }
   public void openDoor()
   {
	   System.out.println("Opening door");
	   try {
		Thread.sleep(openCloseDoorTime * 1000);
	   } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	   this.doorOpen= true;
	   System.out.println("door opened");
   }
   
   public void turnLampOn(){
	   System.out.println("Lamp "+lampButton+" is on");
   }
   public void turnLampOff(){
	   System.out.println("Lamp "+ lampButton+" is off");
   }
   
   
   
   public static void main(String args[])
   {
   	  Thread elevatorThread;
      ElevatorSubsystem c = new ElevatorSubsystem();
      ElevatorHandler elevatorHandler = new ElevatorHandler(c);
      elevatorThread = new Thread(elevatorHandler, "New request");
      elevatorThread.start();
   }
   
}

class ElevatorHandler implements Runnable
{
	private ElevatorSubsystem elevatorSubsystem;
	public ElevatorHandler(ElevatorSubsystem elevatorSubsystem) {
		this.elevatorSubsystem = elevatorSubsystem; 
	}
	
	@Override
	public void run() {
		while(true) {
			elevatorSubsystem.process();
			
		}
	}
}



