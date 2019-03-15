Please see code below this line in both scheduler and elevator: 

"/**********************New Changes Below**********************/"



SCHEDULER:
-----------------------------------------
In scheduler, please see the run() method, which is the main process function.

I used three threads to wait request from floor, 

send request to elevator, 
and wait for update of elevator status in the same time.

I used a HashMap to store the elevator statuses.

ELEVATOR:
-----------------------------------------
- REMOVED currentFloor, and other boolean variables, 

+ ADDED an instance of elevatorStatus object to hold this elevator's status.
+ ADDED a carList in elevatorStatus to represent Person inside elevator car, 
+ ADDED pickUpList in Elevator system to maintain pending requests. 

+ ADDED methods called in operateElevator(): moveUp() moveDown() stopElevator() ect.


FLOOR:
-----------------------------------------
There are still some runtime errors in FloorSubsystem. Maybe because of extra requests in data.txt


WORK LEFT TO DO:
-----------------------------------------

* ADD the timer that goes off within a certain time frame.

* IF the time goes off before an elevator reaches a floor: 
	the system should ASSUME a fault: 
	(the elevator is stuck between floors) 
	(the arrival sensor at a floor has failed) 
	
* ADD sensor(s) that detects whether a door opens or not, or is stuck open

* ADD functionality to SCHEDULER that allows it to deal properly with the faults  
	(i.e. must be able to inject these faults into the system via the data.txt)

The following is what was in theconsole:

[12:45:27.717] ElevatorStatus [currentFloor=1, motorOn=false, up=false, inUse=false]
	at java.io.ObjectInputStream$PeekInputStream.readFully(ObjectInputStream.java:2638)
	at java.io.ObjectInputStream$BlockDataInputStream.readUTFBody(ObjectInputStream.java:3383)
	at java.io.ObjectInputStream$BlockDataInputStream.readUTF(ObjectInputStream.java:3183)
	at java.io.ObjectInputStream.readUTF(ObjectInputStream.java:1124)
	at java.io.ObjectStreamClass.readNonProxy(ObjectStreamClass.java:717)
	at java.io.ObjectInputStream.readClassDescriptor(ObjectInputStream.java:882)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:1815)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1713)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2000)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1535)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:422)
	at Sysctrl.convertFromBytes(Sysctrl.java:42)
	at FloorSubsystem.waitForElevatorStatus(FloorSubsystem.java:164)
	at FloorSubsystem.start(FloorSubsystem.java:78)
	at floorHandler.run(FloorSubsystem.java:210)
	at java.lang.Thread.run(Thread.java:748)