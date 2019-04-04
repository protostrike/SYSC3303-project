/**
 * Motor class represents the motor of the elevator car
 * 
 * Each Elevator will contain a Motor object
 */
public class Motor{

    //Attributes of Motor

    private boolean isOn;

    private int speed;

    private boolean directionUp;

    /**
     * Default constructor for Motor object
     */
    public Motor(){
        this.isOn = false;
        this.speed = 0;
        //this.directionUp;
    }

    /** Methods used to operate Motor **/
    public void turnOn(){
        this.isOn = true;
    }

    public void turnOff(){
        this.isOn = false;
    }

    public void accelerate(){
        this.speed = 1;
        //sleep(2000) //2 seconds
        this.speed = 2;
    }

    public void coast(){
        this.speed = 3;
    }

    public void deccelerate(){
        this.speed = 2;
        //sleep(2000) //2 seconds
        this.speed = 1;
    }
    
    public boolean getDirection(){
        return this.directionUp;
    }
    
    public void setDirection(boolean up){
        this.directionUp = up;
    }
    
    public boolean isOn() {
    	return this.isOn;
    }

    
    /************************************/




}