import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * The Scheduler is responsible for routing each elevator 
 * to requested floors. 
 * 
 * Scheduler must be prepared to handle 
 * possible faults and failures in the system
 */
public class Scheduler {

	// Class variables

	DatagramPacket  floorPacket, elevatorPacket;
	DatagramSocket floorSocket, elevatorSocket, elevatorSocket2;

	LinkedList<Person> personList = new LinkedList<Person>();

	byte[] statusByte = {(byte)0};
	byte[] moveUpCommandByte = {(byte)1};
	byte[] moveDownCommandByte = {(byte)2};
	byte[] StartEngineCommandByte = {(byte)3};
	byte[] StopEngineCommandByte = {(byte)4};
	byte[] openDoorCommandByte = {(byte)5};
	byte[] closeDoorCommandByte = {(byte)6};
	byte[] turnLampOnCommandByte = {(byte)7,0};
	byte[] turnLampOffCommandByte = {(byte)8};

	Calendar cal;

	Sysctrl sysctrl = new Sysctrl();

	int currentFloor;

	LinkedList<Integer> destinationList = new LinkedList<Integer>();

	/*
	 * Constructor
	 */
	public Scheduler()
	{
		try {
			elevatorSocket = new DatagramSocket(sysctrl.getPort("SchedulerReceiveElevatorPort"));	// socket for receiving floor arrival updates
			elevatorSocket2 = new DatagramSocket(sysctrl.getPort("ElevatorStatusPort"));   // socket for receiving elevator status
			floorSocket = new DatagramSocket(sysctrl.getPort("floorSendPort")); // socket for receiving floor request

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
	}

	public static void main( String args[] )
	{
		Thread personThread;
		Scheduler scheduler = new Scheduler();
		PersonHandler personHandler = new PersonHandler(scheduler);
		personThread = new Thread(personHandler, "New request");
		personThread.start();
	}

	// Handle person arrival at elevator door
	public synchronized void personArrivale()
	{
		/*byte data[] = new byte[1000];
		floorPacket = new DatagramPacket(data, data.length);

		try {
			floorSocket.setSoTimeout(2);
			try {
				floorSocket.receive(floorPacket);
			} catch (IOException e) {
				// Some kind of IO Exception
				return;
			}
		} catch (SocketException e2) {
			//2 ms has passed and there is no new Person, therefore WAIT!
			try {
				sysctrl.printLog("waiting..");
				wait();
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		sysctrl.printLog("Request For Elevator received:");

		int len = floorPacket.getLength();
		Person person = new Person();

		try {
			person = (Person) sysctrl.convertFromBytes(data);	// convert data to person object (the request)
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		sysctrl.printLog(person);
		personList.add(person);


		try {
			elevatorPacket = new DatagramPacket(statusByte, statusByte.length,
					InetAddress.getLocalHost(), sysctrl.getPort("ElevatorSendReceivePort"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		sysctrl.printLog("Server: Sending packet:");
		sysctrl.printLog("To host: " + elevatorPacket.getAddress());
		sysctrl.printLog("Destination host port: " + elevatorPacket.getPort());
		len = elevatorPacket.getLength();
		sysctrl.printLog("Length: " + len);
		sysctrl.printLog("Containing: " + new String(elevatorPacket.getData(),0,len));

		// Send the datagram packet to the elevator via the send socket. 
		try {
			elevatorSocket.send(elevatorPacket);			//ask for elevator status from elevator
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}


		sysctrl.printLog("Server: status asked from Elevator");
		sysctrl.printLog("Server: Waiting for status of elevator.\n");

		data = new byte[100];
		elevatorPacket = new DatagramPacket(data,data.length);
		try {        

			System.out.println("Elevator is busy..."); // so we know we're waiting
			elevatorSocket2.receive(elevatorPacket); //receives status of elevator
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Status received:");
		len = elevatorPacket.getLength();

		// Form a String from the byte array.
		data = Arrays.copyOfRange(data, 0, len);

		try {
			//prepare floorpacket to send to floor
			floorPacket = new DatagramPacket(elevatorPacket.getData(),elevatorPacket.getLength(),InetAddress.getLocalHost(),sysctrl.getPort("floorReceivePort"));
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		sysctrl.printLog("Sending to floor...");            // send status back to floor
		try {
			floorSocket.send(floorPacket);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ElevatorStatus status = new ElevatorStatus();

		try {
			status = (ElevatorStatus) sysctrl.convertFromBytes(data);  //create status object from received status from elevator
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sysctrl.printLog(status);

		if (! status.isInUse())									// if elevator is not in use
		{
			this.currentFloor = status.getCurrentFloor();         //get currentFloor of elevator
			this.destinationList.add(person.getDestFloor());		//add to list of destinations


			sysctrl.printLog("elevator is on the way");
			sysctrl.printLog( "Sending packet To the elevator:");
		}


		sysctrl.printLog("\nPicking person up...\n");
		
		sendElevatorCommand(StartEngineCommandByte);

		turnLampOnCommandByte[1] = (byte)personList.getFirst().destFloor;  //what lamp to turn on

		//if elevator is on the same floor as requested initial floor
		if(currentFloor == personList.getFirst().originFloor) 
		{

			sendElevatorCommand(openDoorCommandByte);
			sendElevatorCommand(closeDoorCommandByte);

			sendElevatorCommand(this.turnLampOnCommandByte);
			// go straight to destination floor
			floorArrival(personList.getFirst().getDestFloor());	

		}

		else   {
			//elevator goes to requested floor (where request is coming from)
			floorArrival(personList.getFirst().getOriginFloor());

			sendElevatorCommand(StartEngineCommandByte);
			sendElevatorCommand(this.turnLampOnCommandByte);
			//elevator goes to destination floor
			floorArrival(personList.getFirst().getDestFloor());
		}
		sendElevatorCommand(turnLampOffCommandByte);

		//remove request from queue
		personList.removeFirst();
		sysctrl.printLog("\nPerson Dropped off\n");
		destinationList.removeFirst();
		*/


		byte[] data = new byte[1000];
		floorPacket = new DatagramPacket(data, data.length);
		
		//Wait for person request
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
				sysctrl.printLog("waiting..");
				wait();
				return;
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
		//Add person to request list
		Person person = addPersonToRequestList();
		//Request elevator for person
		requestElevator();
		//Wait until receiving elevator status
		data = waitForElevatorStatus();
		//Send elevator to person's floor
		sendElevatorToPerson(data, person);
		//Transport person to destination floor
		sendPersonToFloor();

	}


	// function that moves elevator, n is floor where elevator is sent to
	private void floorArrival(int n)		
	{
		byte data[] = new byte[1];	// current floor of elevator

		while (true) {
			elevatorPacket = new DatagramPacket(data,data.length);

			try {
				elevatorSocket.receive(elevatorPacket);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if ((int)data[0] == n) {   // if elevator reaches destination (n)
				sendElevatorCommand(StopEngineCommandByte);
				break;
			}
			else  if ((int)data[0]>n) {   // if elevator floor is greater than destination ... move down
				sendElevatorCommand(moveDownCommandByte);
			}
			else if  ((int)data[0]<n)  { // if elevator floor is less than destination ... move up
				sendElevatorCommand(moveUpCommandByte);
			}

			try {
				elevatorSocket.send(elevatorPacket);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Person addPersonToRequestList() {
		sysctrl.printLog("Request For Elevator received:");
		Person person = new Person();

		try {
			person = (Person) sysctrl.convertFromBytes(floorPacket.getData());	// convert data to person object (the request)
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		sysctrl.printLog(person);
		personList.add(person);

		return person;
	}

	private void requestElevator() {
		try {
			elevatorPacket = new DatagramPacket(statusByte, statusByte.length,
					InetAddress.getLocalHost(), sysctrl.getPort("ElevatorSendReceivePort"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		sysctrl.printLog( "Server: Sending packet:");
		sysctrl.printLog("To host: " + elevatorPacket.getAddress());
		sysctrl.printLog("Destination host port: " + elevatorPacket.getPort());
		int len = elevatorPacket.getLength();
		sysctrl.printLog("Length: " + len);
		System.out.print("Containing: ");
		sysctrl.printLog(new String(elevatorPacket.getData(),0,len));

		// Send the datagram packet to the elevator via the send socket. 
		try {
			elevatorSocket.send(elevatorPacket);			//ask for elevator status from elevator
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}


		sysctrl.printLog("Server: status asked from Elevator");
		sysctrl.printLog("Server: Waiting for status of elevator.\n");

	}

	private byte[] waitForElevatorStatus() {
		byte[] data = new byte[100];
		elevatorPacket = new DatagramPacket(data,data.length);
		try {        
			sysctrl.printLog("Elevator is busy...");
			elevatorSocket2.receive(elevatorPacket); //receives status of elevator
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			sysctrl.printLog("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		// Process the received datagram.
		sysctrl.printLog("Status received:");
		int len = elevatorPacket.getLength();
		// Form a String from the byte array.
		data = Arrays.copyOfRange(data, 0, len);

		return data;
	}

	private void sendElevatorToPerson(byte[] data, Person person) {
		try {
			//prepare floorpacket to send to floor
			floorPacket = new DatagramPacket(elevatorPacket.getData(),elevatorPacket.getLength(),InetAddress.getLocalHost(),sysctrl.getPort("floorReceivePort"));
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		sysctrl.printLog("Sending to floor...");            // send status back to floor
		try {
			floorSocket.send(floorPacket);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ElevatorStatus status = new ElevatorStatus();

		try {
			status = (ElevatorStatus) sysctrl.convertFromBytes(data);  //create status object from received status from elevator
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sysctrl.printLog(status);

		if (! status.isInUse())									// if elevator is not in use
		{
			this.currentFloor = status.getCurrentFloor();         //get currentFloor of elevator
			this.destinationList.add(person.getDestFloor());		//add to list of destinations


			sysctrl.printLog("elevator is on the way");
			sysctrl.printLog( "Sending packet To the elevator:");
		}
	}

	private void sendPersonToFloor() {
		sysctrl.printLog("\nPicking person up...\n");
		sendElevatorCommand(StartEngineCommandByte);

		turnLampOnCommandByte[1] = (byte)personList.getFirst().destFloor;  //what lamp to turn on

		//if elevator is on the same floor as requested initial floor
		if(currentFloor == personList.getFirst().originFloor) 
		{

			sendElevatorCommand(openDoorCommandByte);
			sendElevatorCommand(closeDoorCommandByte);

			sendElevatorCommand(this.turnLampOnCommandByte);
			// go straight to destination floor
			floorArrival(personList.getFirst().getDestFloor());	

		}

		else   {
			//elevator goes to requested floor (where request is coming from)
			floorArrival(personList.getFirst().getOriginFloor());

			sendElevatorCommand(StartEngineCommandByte);
			sendElevatorCommand(this.turnLampOnCommandByte);
			//elevator goes to destination floor
			floorArrival(personList.getFirst().getDestFloor());
		}
		sendElevatorCommand(turnLampOffCommandByte);

		//remove request from queue
		personList.removeFirst();
		sysctrl.printLog("\nPerson Dropped off\n");
		destinationList.removeFirst();
	}
	
	private void sendElevatorCommand(byte[] command) {
		try {
			elevatorPacket = new DatagramPacket(command,command.length,InetAddress.getLocalHost(),sysctrl.getPort("ElevatorSendReceivePort"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//Send command to elevator
		try {
			elevatorSocket.send(elevatorPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
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