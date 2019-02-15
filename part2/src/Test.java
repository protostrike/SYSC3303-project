
public class Test {
	public static void main(String[] args) {
		FloorSubsystem f = new FloorSubsystem();
		floorHandler h = new floorHandler(f);
		
		Thread elevatorThread;
		ElevatorSubsystem c = new ElevatorSubsystem();
		ElevatorHandler elevatorHandler = new ElevatorHandler(c);
		elevatorThread = new Thread(elevatorHandler, "New request");
		
		Thread personThread;
		Scheduler scheduler = new Scheduler();
		PersonHandler personHandler = new PersonHandler(scheduler);
		personThread = new Thread(personHandler, "New request");
		
		
		elevatorThread.start();
		personThread.start();
		new Thread(h).start();
	}
}
