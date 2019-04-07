import java.io.IOException;

public class Simulation {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {

		Thread ES = new ElevatorSubsystem();
		Thread scheduler = new Scheduler();
		Thread FS = new FloorSubsystem();
		
		ES.start();
		scheduler.start();
		
		//FS.sleep(300);
		//FS.start();
		
		
		
	}

}
