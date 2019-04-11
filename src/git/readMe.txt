SYSC 3303 Group 3
Iteration 5
Work documentation
----------------------------------------------------


Main Files
----------------------------------
ElevatorSubsystem.java
Scheduler.java
FloorSubsystem.java
ElevatorStatus.java - holds information about an elevator
Sysctrl.java 	    - contains key functions, as well as port numbers
Person.java 	    - Resembles a request made by floorSubsystem
GenerateTestData.java    - Generates "data.txt" which holds requests

 



Test setup instructions
----------------------------------------------------
Our test purpose is running three systems on the same PC on different
ports
Floor subsystem will read requests in data.txt upon running
So floor subsystem has to be started after elevator and scheduler
2. Run Scheduler.java
3. Run ElevatorSubsystem.java
4. Run FloorSubsystem.java


FAULT DETECTION 
first paramater in input file is error type (1 or 2)
second is floor which fault occurs


Number of elevators and floors is configurable through Sysctrl variables: numFloor and numElevators

