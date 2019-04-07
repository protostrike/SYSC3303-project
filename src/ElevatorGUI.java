import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 * 
 * 
 * 
 * @author reginaldpradel
 *
 */
public class ElevatorGUI extends JFrame {

	private JLabel currentFloor;
	private ArrayList<JButton> buttons;
	
	private JLabel elevatorID;

	private JLabel speed;
	
	private JLabel direction;


	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * @param numberOfFloors
	 */
	public ElevatorGUI(int numberOfFloors) {
		
		//this should be set by Elevator
		this.currentFloor = new JLabel("1");
		
		//list of the buttons
		this.buttons = new ArrayList<JButton>();
		
		//adds floor buttons 
		for(int i = numberOfFloors;i > 0 ;i--) {
			buttons.add(new JButton(Integer.toString(i)));
			this.add( new JButton(Integer.toString(i)) );
		}
		
		//buttons for opening and closing doors
		this.add(new JButton("OPEN"));
		this.add(new JButton("CLOSE"));

		
		//grid layout 10 rows by 3 column
	    this.setLayout(new GridLayout(10,3));
		
	   
	    
		//make buttons visible
	    this.pack();
	    this.setVisible(true);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    this.add(currentFloor);
		
	}
	
	/**
	 * will call getCurrentFloor() in Elevator class
	 */
	public void updateFloorNumber(int floorNumber) {
		currentFloor.setText(Integer.toString(floorNumber));
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// create the GUI
		ElevatorGUI eButtonPad = new ElevatorGUI(22);
		
		
	}

}
