import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

// System control class
// All utility functions should go to this class
// And other class can use functions here
public class Sysctrl {
	@SuppressWarnings("serial")
	HashMap<String, Integer> portsMap = new HashMap<String, Integer>(){{
		put("floorSendPort", 5000);
		put("floorReceivePort", 5010);
		put("floorSendPort", 5000);
		put("floorReceivePort", 5010);
		put("ElevatorSendReceivePort", 5005);
		put("ElevatorStatusPort", 5002);
		put("SchedulerReceiveElevatorPort", 5001);
		put("2ndelevator",5006);
	}};

	/***
	 * convertFromBytes() takes a byte array input and return and and of object
	 * 
	 * @param bytes - data in byte array to be interpreted
	 * @return - object corresponding to the data
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				ObjectInput in = new ObjectInputStream(bis)) {
			return in.readObject();
		} 
	}

	/**
	 * converToBytes() converts input argument object's data 
	 * into a byte array and returns it
	 * 
	 * @param object - converts object's data into a byte array
	 * @return - byte array of corresponding object
	 * @throws IOException
	 */
	public byte[] convertToBytes(Object object) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(object);
			return bos.toByteArray();
		} 
	}
	/**
	 * get port number by certain string input
	 * @param request - string indicating which port you want
	 * @return the port number 
	 */
	public int getPort(String request) {
		return portsMap.get(request);
	}

	public void printLog(String s) {
		Date date = new Date();
		long time = date.getTime();
		String currentTime = new Timestamp(time).toString().split(" ")[1];
		System.out.println("[" + currentTime + "] " + s);
	}
	
	public void printLog(Object o) {
		Date date = new Date();
		long time = date.getTime();
		String currentTime = new Timestamp(time).toString().split(" ")[1];
		System.out.println("[" + currentTime + "] " + o);
	}
}
