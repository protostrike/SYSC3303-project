import java.awt.*;
import javax.swing.*;



public class FloorGUI extends JFrame {

	/**
	 * constructor
	 */
	public FloorGUI(int floorNumber) {
	    
		Container pane = getContentPane();
		
		// label to display current Floor # 
		JLabel l = new JLabel("  Floor "+Integer.toString(floorNumber));
		this.add(l);
		
		 //centers the buttons
	    this.setLayout( new GridBagLayout() );
		
		
		//grid layout 2 rows by 1 column
	    pane.setLayout(new GridLayout(3,1));
	    
	    //Add buttons
	    JButton up = new JButton("UP");
	    this.add(up, new GridBagConstraints());
	    
	    JButton down = new JButton("DOWN");
	    this.add(down, new GridBagConstraints());
	    
	    //make buttons visible
	    this.pack();
	    this.setVisible(true);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	    
	  }
	
	public static void main(String[] args) {
		
		//create GUI frame for Floor
	    FloorGUI callPanel = new FloorGUI(6);
	    
	    //Once object is created: 
	    
	    callPanel.pack();
	    //callPanel.setVisible(true);
	}

}
