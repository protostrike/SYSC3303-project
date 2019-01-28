// SimpleEchoServer.java
// This class is the server side of a simple echo server based on
// UDP/IP. The server receives from a client a packet containing a character
// string, then echoes the string back to the client.
// Last edited January 9th, 2016

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;

public class Scheduler {

   DatagramPacket  floorPacket, elevatorPacket;
   DatagramSocket floorSocket, elevatorSocket;
   LinkedList<Person> personList = new LinkedList<Person>();
   byte[] statusByte = {(byte)0};
   byte[] moveUpCommandByte = {(byte)1};
   byte[] moveDownCommandByte = {(byte)2};
   byte[] StartEngineCommandByte = {(byte)3};
   byte[] StopEngineCommandByte = {(byte)4};
   byte[] openDoorCommandByte = {(byte)5};
   byte[] closeDoorCommandByte = {(byte)6};
   byte[] turnLampOnCommandByte = {(byte)7};
   byte[] turnLampOffCommandByte = {(byte)8};
   Calendar cal;
   int currentFloor;
   LinkedList<Integer> destinationList = new LinkedList<Integer>();

   
   private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
	    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	         ObjectInput in = new ObjectInputStream(bis)) {
	        return in.readObject();
	    } 
	}

   public Scheduler()
   {
      try {
         // Construct a datagram socket and bind it to any available 
         // port on the local host machine. This socket will be used to
         // send UDP Datagram packets.
         elevatorSocket = new DatagramSocket();


         // Construct a datagram socket and bind it to port 5000 
         // on the local host machine. This socket will be used to
         // receive UDP Datagram packets.
         floorSocket = new DatagramSocket(5000);
         
         // to test socket timeout (2 seconds)
         //receiveSocket.setSoTimeout(2000);
      } catch (SocketException se) {
         se.printStackTrace();
         System.exit(1);
      } 
   }


   public synchronized void personArrivale()
   {
	   byte data[] = new byte[1000];
	   floorPacket = new DatagramPacket(data, data.length);
	   //System.out.println("Server: Waiting for Packet again.\n");
       try {
       floorSocket.setSoTimeout(2);
	   try {
	   		floorSocket.receive(floorPacket);
	   } catch (IOException e) {
			// Some kind of IO Exception
		   return;
			//e.printStackTrace();
	   }
       } catch (SocketException e2) {
		//2 ms has passed and there is no new Person, therefore WAIT!
		try {
			wait();
			return;
		} catch (InterruptedException e) {
			
			//e.printStackTrace();
		}
		
       }
      
       // Process the received datagram.
       System.out.println("Request For Elevator received:");

       int len = floorPacket.getLength();
       Person person = new Person();
      
       try {
    	   person = (Person) convertFromBytes(data);
       } catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
      
      System.out.println(person);
      
      
      
      ////////////////////////////////////////////////////
      try {
  		elevatorPacket = new DatagramPacket(statusByte, statusByte.length,
  				  InetAddress.getLocalHost(), 5005);
  	} catch (UnknownHostException e1) {
  		e1.printStackTrace();
  	}
      System.out.println( "Server: Sending packet:");
      System.out.println("To host: " + elevatorPacket.getAddress());
      System.out.println("Destination host port: " + elevatorPacket.getPort());
      len = elevatorPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      System.out.println(new String(elevatorPacket.getData(),0,len));
        
      // Send the datagram packet to the elevator via the send socket. 
      try {
         elevatorSocket.send(elevatorPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Server: status asked from Elevator");
      System.out.println("Server: Waiting for status of elevator.\n");
		try {
			elevatorPacket = new DatagramPacket(data, data.length,
					  InetAddress.getLocalHost(), 5005);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}


      try {        
         System.out.println("Waiting..."); // so we know we're waiting
         elevatorSocket.receive(elevatorPacket);
      } catch (IOException e) {
         System.out.print("IO Exception: likely:");
         System.out.println("Receive Socket Timed Out.\n" + e);
         e.printStackTrace();
         System.exit(1);
      }

      // Process the received datagram.
      System.out.println("Status received:");
      len = elevatorPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: " );
      // Form a String from the byte array.
      data = Arrays.copyOfRange(data, 0, len);


      ElevatorStatus status = new ElevatorStatus();
      
      try {
    	  status = (ElevatorStatus) convertFromBytes(data);
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
      
      
      System.out.println(status);
      
      if (! status.isInUse())
      {
    	  this.currentFloor = status.getCurrentFloor();
    	  this.destinationList.add(person.getDestFloor());
    	  
    	  System.out.println("elevator is coming right away");
    	  
    	  System.out.println( "Sending packet To the elevator:");

    	  if(currentFloor == person.originFloor)
    	  {
        	  try {
				elevatorPacket = new DatagramPacket(this.openDoorCommandByte, openDoorCommandByte.length
						,InetAddress.getLocalHost(), 5005);
        	  } catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
        	  }
              try {
                 elevatorSocket.send(elevatorPacket);
              } catch (IOException e) {
                 e.printStackTrace();
                 System.exit(1);
              }
        	  try {
				elevatorPacket = new DatagramPacket(this.closeDoorCommandByte, closeDoorCommandByte.length
						  ,InetAddress.getLocalHost(), 5005);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	  
        	  try {
                 elevatorSocket.send(elevatorPacket);
              } catch (IOException e) {
                 e.printStackTrace();
                 System.exit(1);
              }
        	  try {
				elevatorPacket = new DatagramPacket(this.StartEngineCommandByte, StartEngineCommandByte.length
						  ,InetAddress.getLocalHost(), 5005);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
              try {
                 elevatorSocket.send(elevatorPacket);
              } catch (IOException e) {
                 e.printStackTrace();
                 System.exit(1);
              }
              if(person.getDestFloor() > currentFloor)
              {
            	  try {
					elevatorPacket = new DatagramPacket(this.moveUpCommandByte, moveUpCommandByte.length
							  ,InetAddress.getLocalHost(), 5005);
            	  } catch (UnknownHostException e1) {
					e1.printStackTrace();
            	  }
              }
              else
              {
            	  try {
					elevatorPacket = new DatagramPacket(this.moveDownCommandByte, moveDownCommandByte.length
							  ,InetAddress.getLocalHost(), 5005);
            	  } catch (UnknownHostException e1) {					
					e1.printStackTrace();
            	  }
              }
              try {
            	  elevatorSocket.send(elevatorPacket);
              } catch (IOException e) {
            	  e.printStackTrace();
              	System.exit(1);
              }

    	  }//if
    	  else //elevator is in use
    	  {
    		  personList.add(person);
    		  //do sth here
    	  }    

    	  
      }//if
	 
	      
   }

   
   public synchronized void floorArrivale()
   {
	   //Floor's sensor notifies the Scheduler that new floor has reached,
	   // so scheduler should a decision to stop the elevator here or not
   }

   public static void main( String args[] )
   {
	  Thread personThread;
      Scheduler scheduler = new Scheduler();
      PersonHandler personHandler = new PersonHandler(scheduler);
      personThread = new Thread(personHandler, "New request");
      personThread.start();
      
      //c.receiveAndEcho();
   }
}
class PersonHandler implements Runnable
{
	private Scheduler scheduler;
	public PersonHandler(Scheduler scheduler) {
		this.scheduler = scheduler; 
	}
	
	@Override
	public void run() {
		while(true)
			scheduler.personArrivale();
		
	}
	
}

