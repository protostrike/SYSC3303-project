/**
 * Door class represents the elevator door. 
 * Each Elevator contain a Door object
 * 
 * @author Reginald Pradel
 * 
 */
 public class Door{

    private boolean isOpen;

    /**
     * Constuctor for Door
     */
    public Door(boolean isOpen){
        
        this.isOpen = isOpen;
    }

    /**
     * Accessor method returns true if door is open
     * returns false otherwise
     */
    public boolean isOpen(){
        return this.isOpen;
    }

    /**
     * If the door must be opened, param open = true
     * else door must be closed
     */
    public void operateDoor(boolean open){
        if(open){
            //sleep( timeToMoveDoorOPEN/Close )
            this.isOpen = true;
            System.out.println("Door is now open");

        }
        else{
            //sleep( timeToMoveDoorOPEN/Close )
            this.isOpen = false;
            System.out.println("Door is now closed");
        }
    }



 }