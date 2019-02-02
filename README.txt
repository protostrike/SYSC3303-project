SYSC 3303 Group 3 
Iteration 1

Work documentation
----------------------------------------------------
Please see README.md for detailed works for group members

Test setup
----------------------------------------------------
Our test purpose is running three systems on the same PC on different ports
Floor subsystem will read requests in data.txt upon running
So floor subsystem has to be started after elevator and scheduler

1. Run GenerateTestData.java
2. Run Scheduler.java
3. Run ElevatorSubsystem.java
4. Run FloorSubsystem.java

After executing steps above, please wait for console outputs for each systems' activities.


Sample Output of three systems
-------------------------------------------------------

Elevator:
---------------------------
Starting engine (at floor1)
moving up to floor2
Stopping engine at floor2
Opening door
door opened
closing door
door closed
Starting engine (at floor2)
Lamp 4 is on
moving up to floor3
moving up to floor4
Stopping engine at floor4
Opening door
door opened
closing door
door closed
Lamp 4 is off
Starting engine (at floor4)
moving up to floor5
Stopping engine at floor5
Opening door
door opened
closing door
door closed
Starting engine (at floor5)
Lamp 2 is on
moving down to floor4
moving down to floor3
moving down to floor2
Stopping engine at floor2
Opening door
door opened
closing door
door closed
Lamp 2 is off



Floor:
-----------------------------
floor 2 pressed button up to floor 4
floor 5 pressed button down to floor 2
Floor: Sending packet to Scheduler...:
14:05:15.0 Origin: 2 Dest: 4 Going: Going Up
Floor 2 is pressed up
waiting...
Packet received

ElevatorStatus [currentFloor=1, motorOn=false, up=false, inUse=false]
Floor: Sending packet to Scheduler...:
14:05:15.0 Origin: 5 Dest: 2 Going: Going Down
Floor 5 is pressed Down
waiting...
Packet received

ElevatorStatus [currentFloor=4, motorOn=false, up=true, inUse=false]


Scheduler:
-----------------------------
Request For Elevator received:
14:05:15.0 Origin: 2 Dest: 4 Going: Going Up
Server: Sending packet:
To host: AlexMSI/192.168.1.11
Destination host port: 5005
Length: 1
Containing: 
Server: status asked from Elevator
Server: Waiting for status of elevator.

Elevator is busy...
Status received:
Sending to floor...
ElevatorStatus [currentFloor=1, motorOn=false, up=false, inUse=false]
elevator is on the way
Sending packet To the elevator:

Picking person up...


Person Dropped off

Request For Elevator received:
14:05:15.0 Origin: 5 Dest: 2 Going: Going Down
Server: Sending packet:
To host: AlexMSI/192.168.1.11
Destination host port: 5005
Length: 1
Containing: 
Server: status asked from Elevator
Server: Waiting for status of elevator.

Elevator is busy...
Status received:
Sending to floor...
ElevatorStatus [currentFloor=4, motorOn=false, up=true, inUse=false]
elevator is on the way
Sending packet To the elevator:

Picking person up...


Person Dropped off
