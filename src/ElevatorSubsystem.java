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
	   try {
		   sendReceiveSocket.setSoTimeout(2);
		   try {
			   sendReceiveSocket.receive(receivePacket);
		   } catch (IOException e) {
			// TODO Auto-generated catch block
			   return;
		
		   }
	   } catch (SocketException e2) {
		  try {
			wait();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
	   }

		
		  // Process the received datagram.
		  System.out.println("Packet received from Scheduler:");
		  int len = receivePacket.getLength();
		
		  
		
		  // Form a String from the byte array.
	
		  //If the scheduler wants to check the status
		  if(len == 1)
		  {	 
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
					turnLampOn();
					break;
				case (byte)8:
					turnLampOff();
					break;
			  }//switch
		  }//if
		  
		  
		  if(requestType.equalsIgnoreCase("status requested") )
		  {
			  sendPacket =new DatagramPacket(data, data.length,
			        receivePacket.getAddress(), 5001);
			  
			
			  System.out.println( "Sending packet to scheduler:");
			  len = sendPacket.getLength();
			  System.out.println("Length: " + len);
			  System.out.print("Containing: ");
	
			    
			  // Send the datagram packet to the client via the send socket. 
			  try {
			     sendReceiveSocket.send(sendPacket);
			  } catch (IOException e) {
			     e.printStackTrace();
			     System.exit(1);
			  }
			
			  System.out.println("Server: packet sent");
			  
			  // We're finished, so close the socket.
			  //sendReceiveSocket.close();
			  requestType="";
		  }
   }
 

  
   public void moveUp()
   {
	   
	   currentFloor++;
	   try {
		Thread.sleep(elevatorSpeed * 1000);
	   } catch (InterruptedException e) {
		e.printStackTrace();
	   }
	   this.up= true;
	   System.out.println("Moving up to floor"+ currentFloor);
	   
	   
   

   }
   public void moveDown()
   {
	   
	   currentFloor--;
	   try {
		Thread.sleep(elevatorSpeed * 1000);
	   } catch (InterruptedException e) {
		e.printStackTrace();
	   }
	   this.up= false;
	   System.out.println("Moving down to floor"+ currentFloor);
	   
	   
	  
	   
	   
   }
   public void start()
   {
	   System.out.println("Starting engin at floor "+currentFloor);
	   this.motorOn= true;
	 
	   
   }
   public void stop()
   {
	   System.out.println("Stopping engine at floor "+currentFloor);
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
   
   public void turnLampOn(){}
   public void turnLampOff(){}
   
   
   
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



