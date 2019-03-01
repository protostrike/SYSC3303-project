# SYSC3303-project
Project dedicated to course SYSC 3303

** Please use EGit plugin in Eclipse to clone/commit to project

** Please create a NEW branch for your works, in cases something wrong happen in Master

Iteration #1: Adding subsystems and establish connections

----------------------------------------------------------------------

Testing: Using three different ports on ONE PC for testing (22,23,24)

Qingchen(Alex): Test cases, UML, documentation

Mohamad: Scheduler,FloorSubsystem, ElevatorSubsystem, Floor

Aryan: Scheduler,FloorSubsystem, ElevatorSubsystem, Elevatortatus, Person 

Chengyang: 

Reginald: State Machine Diagrams, code refactoring (making code more modular)


Current design:

Floor: 
	add time feature to control times of request

Scheduler: 
	Get request from floor
	Get update of elevator statuses
	Read statuses from elevators and determine which elevator it should send request to
Elevator:
	Put new request in a pending list(person needs to pick up)
	Add person in car list from pending list, on certain floor(person in elevator car)
	Elevator operate by itself by sending person up and down
	Send status to scheduler every time status has an update
