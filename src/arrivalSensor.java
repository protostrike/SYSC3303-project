import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class arrivalSensor implements Runnable {
	DatagramPacket  sendPacket;
	DatagramSocket sendSocket;
	byte[] StopEngineCommandByte = {(byte)4};


	public synchronized void sensor() {
		while (true){

		try {
			wait();												// waits for elevator to arrive then notifies scheduler
			try {
				sendSocket = new DatagramSocket();
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			try {
				sendPacket = new DatagramPacket(StopEngineCommandByte,1,InetAddress.getLocalHost(),23);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}

	public synchronized void elevatorArrival() {
			System.out.println("elevator has arrived");
			notifyAll();
		
	}








public void run() {
	this.sensor();
	
}


}