/***
 * The ElevatorSubsystem is a class controlled by the scheduler in order to
 * manage interactions with the elevator cars and operate the
 * motor and to open and close the doors. 
 * 
 * Each elevator has its own elevator subsystem
 */

import java.io.*;
import java.net.*;

public class ElevatorSubsystem2 {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	int currentFloor = 1;
	boolean motorOn = false, up=false, doorOpen= false;
	int openCloseDoorTime = 3;
	int elevatorSpeed = 5;
	byte[] statusByte;
	byte lampButton;

	Sysctrl sysctrl = new Sysctrl();

	public ElevatorSubsystem2()
	{
		try {
			sendReceiveSocket = new DatagramSocket(sysctrl.getPort("ElevatorSendReceivePort"));
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String args[])
	{
		Thread elevatorThread;
		ElevatorSubsystem c = new ElevatorSubsystem();
		ElevatorHandler elevatorHandler = new ElevatorHandler(c);
		elevatorThread = new Thread(elevatorHandler, "New request");
		elevatorThread.start();
	}

	
	/**
	 * process() is used to process requests and set up DatagramPackets
	 */
	public synchronized void process()
	{	
		byte data[] = new byte[1000];
		receivePacket = new DatagramPacket(data, data.length);
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return;
		}

		//Operate Elevator
		OperateElevator(data);

	}
	/**
	 * start() is used to initialize elevator subsystem
	 */
	private void start()
	{
		sysctrl.printLog("Starting engine (at floor"+currentFloor+")");
		this.motorOn= true;

		sendElevatorStatus();
	}

	/**
	 * stop() is used to stop the elevator subsystem execution
	 */
	private void stop()
	{
		sysctrl.printLog("Stopping engine at floor"+currentFloor);
		this.motorOn= false;
		openOrCloseDoor("open");
		openOrCloseDoor("close");

	}

	/**
	 * Send elevator status to scheduler
	 */
	private void sendElevatorStatus() {
		byte [] floorArival = {(byte)currentFloor};
		try {
			sendPacket = new DatagramPacket(floorArival,floorArival.length,InetAddress.getLocalHost(),sysctrl.getPort("SchedulerReceiveElevatorPort"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}                    									

		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Operate elevator door with given input
	 * @param doorStatus - open or close request
	 */
	private void openOrCloseDoor(String doorStatusRequest) {
		Boolean doorOpen = null;

		if(doorStatusRequest.equals("open")) {
			sysctrl.printLog("Opening door");
			doorOpen = true;
		}
		else if(doorStatusRequest.equals("close")) {
			sysctrl.printLog("closing door");
			doorOpen = false;
		}
		//TODO: Error handling
		else {
			sysctrl.printLog("Door Error, force closing door");
			this.doorOpen = false;
			return;
		}

		try {
			Thread.sleep(openCloseDoorTime * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.doorOpen = doorOpen;
		if(doorOpen)
			sysctrl.printLog("door opened");
		else
			sysctrl.printLog("door closed");
	}

	/**
	 * Simply print lamp status (on or off)
	 * @param s - lamp status (on or off)
	 */
	private void turnLampOnOrOff(String s) {
		sysctrl.printLog("Lamp "+lampButton+" is " + s);
	}

	/**
	 * Move elevator up or down with corresponding input
	 * @param s - indicating up or down
	 */
	private void moveUpOrDown(String s) {
		try {
			Thread.sleep(elevatorSpeed * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(s.equals("up")) {
			this.up = true;
			currentFloor++;
		}
		else {
			this.up = false;
			currentFloor--;
		}

		sysctrl.printLog("moving " + s + " to floor"+currentFloor);
		
		byte [] floorArival = {(byte)currentFloor};
		try {
			sendPacket = new DatagramPacket(floorArival,floorArival.length,InetAddress.getLocalHost(),sysctrl.getPort("SchedulerReceiveElevatorPort"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Operate elevator with requested command
	 * @param data - the data received for elevator status, first byte contains status command 
	 * @return A string saying whether status was requested
	 */
	private void OperateElevator(byte[] data) {
		ElevatorStatus status;
		String requestType = "";
		switch(data[0])				// run operations depending on what byte it receives
		{
		case (byte)0:
			status = new ElevatorStatus(currentFloor, motorOn, up);
			requestType = "status requested";		     
			try {
				data = sysctrl.convertToBytes(status);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		case (byte)1:
			moveUpOrDown("up");
			break;
		case (byte)2:
			moveUpOrDown("down");
			break;
		case (byte)3:
			start();
			break;
		case (byte)4:
			stop();
			break;
		case (byte)5:
			openOrCloseDoor("open");
			break;
		case (byte)6:
			openOrCloseDoor("close");
			break;
		case (byte)7:
			lampButton = data[1];
			turnLampOnOrOff("on");
			break;
		case (byte)8:
			turnLampOnOrOff("off");
			break;
		}//switch

		sendStatus(requestType, data);
	}
	
	private void sendStatus(String statusRequest, byte[] data) {
		if(statusRequest.equalsIgnoreCase("status requested") )				
			// if status is asked for, send it
		{
			sendPacket =new DatagramPacket(data, data.length,
					receivePacket.getAddress(), sysctrl.getPort("ElevatorStatusPort"));
			try {
				sendReceiveSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}

class ElevatorHandler2 implements Runnable
{
	private ElevatorSubsystem2 elevatorSubsystem;
	public ElevatorHandler2(ElevatorSubsystem2 elevatorSubsystem) {
		this.elevatorSubsystem = elevatorSubsystem; 
	}

	@Override
	public void run() {
		while(true) {
			elevatorSubsystem.process();

		}
	}
}



