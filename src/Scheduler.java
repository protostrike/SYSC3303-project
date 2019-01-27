// SimpleEchoServer.java
// This class is the server side of a simple echo server based on
// UDP/IP. The server receives from a client a packet containing a character
// string, then echoes the string back to the client.
// Last edited January 9th, 2016

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;

public class Scheduler {

   DatagramPacket  floorPacket, elevatorPacket;
   DatagramSocket floorSocket, elevatorSocket;
   LinkedList<Person> personList = new LinkedList<Person>();
   byte[] statusByte = {(byte)0,(byte)1};
   
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

   public void receiveAndEcho()
   {
      // Construct a DatagramPacket for receiving packets up 
      // to 100 bytes long (the length of the byte array).

      byte data[] = new byte[1000];
      floorPacket = new DatagramPacket(data, data.length);
      System.out.println("Server: Waiting for Packet.\n");

      // Block until a datagram packet is received from receiveSocket.
      try {        
         System.out.println("Waiting..."); // so we know we're waiting
         floorSocket.receive(floorPacket);
      } catch (IOException e) {
         System.out.print("IO Exception: likely:");
         System.out.println("Receive Socket Timed Out.\n" + e);
         e.printStackTrace();
         System.exit(1);
      }

      // Process the received datagram.
      System.out.println("Server: Packet received:");
      System.out.println("From host: " + floorPacket.getAddress());
      System.out.println("Host port: " + floorPacket.getPort());
      int len = floorPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: " );


      Person person = new Person();
      
      try {
		person = (Person) convertFromBytes(data);
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
      
      System.out.println(person);
      personList.add(person);
 
      

      try {
		elevatorPacket = new DatagramPacket(statusByte, statusByte.length,
				  InetAddress.getLocalHost(), 5005);
	} catch (UnknownHostException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

      System.out.println( "Server: Sending packet:");
      System.out.println("To host: " + elevatorPacket.getAddress());
      System.out.println("Destination host port: " + elevatorPacket.getPort());
      len = elevatorPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      System.out.println(new String(elevatorPacket.getData(),0,len));
      // or (as we should be sending back the same thing)
      // System.out.println(received); 
        
      // Send the datagram packet to the elevator via the send socket. 
      try {
         elevatorSocket.send(elevatorPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Server: status asked from Elevator");
      System.out.println("Server: Waiting for status of elevator.\n");

      // Block until a datagram packet is received from receiveSocket.
      //data = null;
      elevatorPacket = new DatagramPacket(data, data.length);
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
      System.out.println("Server: Packet received:");
      System.out.println("From host: " + elevatorPacket.getAddress());
      System.out.println("Host port: " + elevatorPacket.getPort());
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
        
      

   }

   public static void main( String args[] )
   {
      Scheduler c = new Scheduler();
      c.receiveAndEcho();
   }
}

