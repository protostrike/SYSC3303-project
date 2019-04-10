import java.io.IOException;
import java.util.Arrays;


/**
 * Simulation class is a test class that brings the 3 subsystems together to 
 * form an Elevator System Simulator
 * 
 * 
 * 
 * @author reginaldpradel
 *
 */
public class Simulation {

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
				
		//GUIcontroller gui;
		
		ElevatorSubsystem ES = new ElevatorSubsystem();
		Scheduler scheduler = new Scheduler();
		FloorSubsystem FS = new FloorSubsystem();
		
		//Threads
		Thread t_ES = new Thread(ES,"Elevator Subsystem");
		Thread t_sch = new Thread(scheduler,"Scheduler");
		Thread t_FS = new Thread(FS,"Floor Subsystem");
		
		//Start Elevator Subsystem
		t_ES.start();
		
		//Start Scheduler
		t_sch.start();
		
		//Start Floor Subsystem
		t_FS.start();
		
	}

}
