
import java.io.Serializable;

public class ElevatorStatus implements Serializable{

	   int currentFloor;
	   boolean motorOn = false, up, inUse, doorOpen=false;
	   
	   public boolean isDoorOpen() {
		return doorOpen;
	}

	public void setDoorOpen(boolean doorOpen) {
		this.doorOpen = doorOpen;
	}

	public ElevatorStatus(int currentFloor, boolean motorOn, boolean up) {
		   super();
		   this.currentFloor = currentFloor;
		   this.motorOn = motorOn;
		   this.up = up;
		   this.inUse= motorOn;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public boolean isMotorOn() {
		return motorOn;
	}

	public void setMotorOn(boolean motorOn) {
		this.motorOn = motorOn;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public ElevatorStatus() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "ElevatorStatus [currentFloor=" + currentFloor + ", motorOn=" + motorOn + ", up=" + up + ", inUse="
				+ inUse + "]";
	}


}
