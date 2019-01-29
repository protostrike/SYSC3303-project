import java.io.Serializable;

public class ElevatorStatus implements Serializable{

	   int currentFloor;
	   boolean motorOn = false, up;
	   
	   public ElevatorStatus(int currentFloor, boolean motorOn, boolean up) {
		   super();
		   this.currentFloor = currentFloor;
		   this.motorOn = motorOn;
		   this.up = up;
	}

	public ElevatorStatus() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "ElevatorStatus [currentFloor=" + currentFloor + ", motorOn=" + motorOn + ", up=" + up + "]";
	}

}