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
   boolean motorOn = false, up=false;
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

   public void sendAndReceive()
   {
      // Prepare a DatagramPacket and send it via sendReceiveSocket
      // to port 5000 on the destination host.
 
      String s = "Anyone there?";
      System.out.println("Client: sending a packet containing:\n" + s);

      // Java stores characters as 16-bit Unicode values, but 
      // DatagramPackets store their messages as byte arrays.
      // Convert the String into bytes according to the platform's 
      // default character encoding, storing the result into a new 
      // byte array.

      byte msg[] = s.getBytes();

      // Construct a datagram packet that is to be sent to a specified port 
      // on a specified host.
      // The arguments are:
      //  msg - the message contained in the packet (the byte array)
      //  msg.length - the length of the byte array
      //  InetAddress.getLocalHost() - the Internet address of the 
      //     destination host.
      //     In this example, we want the destination to be the same as
      //     the source (i.e., we want to run the client and server on the
      //     same computer). InetAddress.getLocalHost() returns the Internet
      //     address of the local host.
      //  5000 - the destination port number on the destination host.
     /*
      try {
         sendPacket = new DatagramPacket(msg, msg.length,
                                         InetAddress.getLocalHost(), 5000);
      } catch (UnknownHostException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Client: Sending packet:");
      System.out.println("To host: " + sendPacket.getAddress());
      System.out.println("Destination host port: " + sendPacket.getPort());
      int len = sendPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"

      // Send the datagram packet to the server via the send/receive socket. 

      try {
         sendReceiveSocket.send(sendPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Client: Packet sent.\n");
	*/
      // Construct a DatagramPacket for receiving packets up 
      // to 100 bytes long (the length of the byte array).

      byte data[] = new byte[1000];
      receivePacket = new DatagramPacket(data, data.length);

      try {
         // Block until a datagram is received via sendReceiveSocket.  
         sendReceiveSocket.receive(receivePacket);
      } catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      // Process the received datagram.
      System.out.println("Client: Packet received:");
      System.out.println("From host: " + receivePacket.getAddress());
      System.out.println("Host port: " + receivePacket.getPort());
      int len = receivePacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      
      

      // Form a String from the byte array.
      String received = new String(data,0,len);   
      System.out.println(received);

      //If the scheduler wants to check the status
      if(len == 2 && data[0]==(byte)0 && data[1]==(byte)1)
      {
    	  ElevatorStatus status = new ElevatorStatus(currentFloor, motorOn, up);
    	  
          try {
    		msg = this.convertToBytes(status);
          } catch (IOException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
          }

          sendPacket =new DatagramPacket(msg, msg.length,
		        receivePacket.getAddress(), receivePacket.getPort());

          System.out.println( "Server: Sending packet:");
          System.out.println("To host: " + sendPacket.getAddress());
          System.out.println("Destination host port: " + sendPacket.getPort());
          len = sendPacket.getLength();
          System.out.println("Length: " + len);
          System.out.print("Containing: ");
          System.out.println(status);
          // or (as we should be sending back the same thing)
          // System.out.println(received); 
            
          // Send the datagram packet to the client via the send socket. 
          try {
             sendReceiveSocket.send(sendPacket);
          } catch (IOException e) {
             e.printStackTrace();
             System.exit(1);
          }

          System.out.println("Server: packet sent");
      }
      // We're finished, so close the socket.
      sendReceiveSocket.close();
   }

   public static void main(String args[])
   {
      ElevatorSubsystem c = new ElevatorSubsystem();
      c.sendAndReceive();
   }
}
