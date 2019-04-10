import javax.swing.*;
import java.util.*;
import java.awt.*;




public class gui extends JFrame {
	Sysctrl sysctrl = new Sysctrl();
int spacing = 5;
int el, fl;
Map<Integer,Integer> colored = new HashMap<Integer,Integer>();
Map<Integer,ArrayList<String>> requests = new HashMap<Integer,ArrayList<String>>();
	public gui() {
		
		
		this.setTitle("Scheduler");
		this.setSize(50+sysctrl.numElevators*50 +90*(sysctrl.numElevators) ,50+sysctrl.numFloors * 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		
		
		
		Grid grid = new Grid();
		this.setContentPane(grid);

	
	
		
	}
	
	public class Grid extends JPanel {
		
		
		public void paintComponent(Graphics g) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0,0,50+sysctrl.numElevators*50,50+sysctrl.numFloors * 50);
			for (int i=0; i<sysctrl.numElevators;i++) {
				g.setColor(Color.DARK_GRAY);
				g.drawLine(50+sysctrl.numElevators*50+ 90*(i+1), 0, 50+sysctrl.numElevators*50+ 90*(i+1), 50+sysctrl.numFloors * 50);
				for (int j=0;j<sysctrl.numFloors;j++) {
					g.setColor(Color.gray);
					for (int z:colored.keySet()) {
						if (z-1==i && (sysctrl.numFloors-1)-(colored.get(z)-1)==j)
							g.setColor(Color.yellow);
					}
					
					g.fillRect(spacing+i*50, spacing+j*50, 50-2*spacing, 50-2*spacing);
				}
				
			}
			
			
			for (int i=0;i<sysctrl.numElevators;i++) {
			
			JLabel l= new JLabel("Elevator"+(i+1));
		    Dimension size = l.getPreferredSize();
			l.setBounds(50+sysctrl.numElevators*50+ 90*(i),10,size.width,size.height);
			this.add(l);
			
			
			if (requests.containsKey(i)) {
				for (int j=0;j<requests.get(i).size();j++) {
					JLabel a = new JLabel(requests.get(i).get(j));
					Dimension size2 = a.getPreferredSize();
					a.setBounds(50+sysctrl.numElevators*50+ 90*(i-1),20+ 20*(j+1),size2.width,size2.height);
					this.add(a);
				}
			}
			
		}
		}
		
		
				
		
	}
	
	
	public void updateGrid(int a, int b) {
		if (colored.containsKey(a))
			colored.remove(a);
		
		colored.put(a, b);
	}
	
	public void updateRequests(int a, String b) {
		if (requests.get(a)==null) {
			requests.put(a, new ArrayList<String>());
		}
		
		requests.get(a).add(b);
		System.out.println(requests);
		
	}
	
	
	
	
	
	
}
	
