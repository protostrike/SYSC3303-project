import java.util.*;
import javax.swing.*;

/**
 * Each Floor will have a call panel
 */
public class FloorCallPanel{

    //Attributes of a Floor Call Panel

    private boolean upButton, downButton;

    private JPanel floorPanel;

    public FloorCallPanel(){
        this.upButton = false;
        this.downButton = false;
    }

    public void update(){
        //will be used to update the FloorCall panel view

    }

    /**
     * sets the panel button boolean to true if going up 
     * else sets the downButton to true
     * 
     * @param goingUp
     */
    public void makeRequest(boolean goingUp){
        //send request to scheduler
        if(goingUp)
            upButton = true;
        else
            downButton = true;
    }

    /**
     * This will be called from floor when elevator arrives 
     */
    public void updateButtons(Elevator e){
        
        if(e.getMotor().getDirection()){
            setUpButton(false);
        }
        else{
            setDownButton(false);
        }

    }

    public boolean isPressedUp(){
        return this.upButton;
    }
    public boolean isPressedDown(){
        return this.downButton;
    }

    public void setUpButton(boolean b){
        this.upButton = b;
    }
    public void setDownButton(boolean b){
        this.downButton = b;
    }


}