
public class Person  implements java.io.Serializable{
	
	String time;
	int originFloor, destFloor;
	boolean up;
	public Person()
	{
		
	}
	
	public Person(String time, int originFloor, int destFloor, boolean up) {
		this.time = time;
		this.originFloor = originFloor;
		this.destFloor = destFloor;
		this.up = up;
	}
	public String toString()
	{
		return time + " Origin: "+ originFloor + " Dest: " + destFloor + " Going: " + (up?"Going Up":"Going Down");
		
	}
	
	

}
