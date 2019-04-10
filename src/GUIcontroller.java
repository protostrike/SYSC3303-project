import javax.swing.*;

public class GUIcontroller extends JFrame{

	/**
	 * default
	 */
	private static final long serialVersionUID = 1L;
	
	static Sysctrl sysctrl;
	
	
	ElevatorGUI ePanel = new ElevatorGUI(sysctrl.getNumberOfFloors());
	

	public GUIcontroller(ElevatorSubsystem es, Scheduler s, FloorSubsystem f) {
		
	}
	
	
	
	
}
