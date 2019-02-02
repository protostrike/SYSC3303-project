import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;

public class Scheduler {

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
	int currentFloor;
	LinkedList<Integer> destinationList = new LinkedList<Integer>();


	private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				ObjectInput in = new ObjectInputStream(bis)) {
			return in.readObject();
		} 
	}

	//Constructor
	public Scheduler()
	{
		try {
			elevatorSocket = new DatagramSocket(5001);	// socket for receiving floor arrival updates
			elevatorSocket2 = new DatagramSocket(5002);   // socket for receiving elevator status


			floorSocket = new DatagramSocket(5000); // socket for receiving floor request


		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
	}


	// Handle person arrival at elevator door
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
				System.out.println("waiting..");
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
			person = (Person) convertFromBytes(data);					// convert data to person object (the request)
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println(person);
		personList.add(person);


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
			elevatorSocket.send(elevatorPacket);			//ask for elevator status from elevator
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}


		System.out.println("Server: status asked from Elevator");
		System.out.println("Server: Waiting for status of elevator.\n");


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
			floorPacket = new DatagramPacket(elevatorPacket.getData(),elevatorPacket.getLength(),InetAddress.getLocalHost(),5010);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		System.out.println("Sending to floor...");            // send status back to floor
		try {
			floorSocket.send(floorPacket);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ElevatorStatus status = new ElevatorStatus();

		try {
			status = (ElevatorStatus) convertFromBytes(data);  //create status object from received status from elevator
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		System.out.println(status);

		if (! status.isInUse())									// if elevator is not in use
		{
			this.currentFloor = status.getCurrentFloor();         //get currentFloor of elevator
			this.destinationList.add(person.getDestFloor());		//add to list of destinations


			System.out.println("elevator is on the way");
			System.out.println( "Sending packet To the elevator:");

			System.out.println("\nPicking person up...\n");
			try {
				elevatorPacket = new DatagramPacket(this.StartEngineCommandByte, StartEngineCommandByte.length  //start elevator engine
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

			turnLampOnCommandByte[1] = (byte)personList.getFirst().destFloor;  //what lamp to turn on

			if(currentFloor == personList.getFirst().originFloor) //if elevator is on the same floor as requested initial floor
			{

				try {
					elevatorPacket = new DatagramPacket(this.openDoorCommandByte, openDoorCommandByte.length // open the door
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

				// close door
				try {
					elevatorPacket = new DatagramPacket(this.closeDoorCommandByte, closeDoorCommandByte.length // open the door
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
					elevatorPacket = new DatagramPacket(this.turnLampOnCommandByte, turnLampOnCommandByte.length //turn the lamp on(dest. floor)
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

				floorArrival(personList.getFirst().getDestFloor());	// go straight to destination floor

			}

			else   {
				floorArrival(personList.getFirst().getOriginFloor());//elevator goes to requested floor (where request is coming from)
				try {
					//starts engine after it gets there..
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


				try {
					elevatorPacket = new DatagramPacket(this.turnLampOnCommandByte, turnLampOnCommandByte.length //turn the lamp on
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

				floorArrival(personList.getFirst().getDestFloor());//elevator goes to destination floor
			}

			try {
				//turn lamp off after elevator gets to destination
				elevatorPacket = new DatagramPacket(this.turnLampOffCommandByte, turnLampOffCommandByte.length 
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

			//remove request from queue
			personList.removeFirst();
			System.out.println("\nPerson Dropped off\n");
			destinationList.removeFirst();
		}
	}


	public   void floorArrival(int n)		// function that moves elevator, n is floor where elevator is sent to
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
				try {
					elevatorPacket = new DatagramPacket(StopEngineCommandByte,StopEngineCommandByte.length,InetAddress.getLocalHost(),5005);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}

				try {													// elevator reached dest. stop
					elevatorSocket.send(elevatorPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			else  if ((int)data[0]>n) {   // if elevator floor is greater than destination ... move down

				try {
					elevatorPacket = new DatagramPacket(moveDownCommandByte,moveDownCommandByte.length,InetAddress.getLocalHost(),5005);

				} catch (UnknownHostException e) {
					e.printStackTrace();
				}  
			}
			else if  ((int)data[0]<n)  { // if elevator floor is less than destination ... move up

				try {
					elevatorPacket = new DatagramPacket(moveUpCommandByte,moveUpCommandByte.length,InetAddress.getLocalHost(),5005);

				} catch (UnknownHostException e) {
					e.printStackTrace();
				}  
			}


			try {
				elevatorSocket.send(elevatorPacket);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

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