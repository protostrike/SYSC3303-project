import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;

public class Test {
	public static void main(String[] args) {

		generateDataFile(3);

		startSystem();
	}

	private static void generateDataFile(int n) {
		String path = Paths.get("").toAbsolutePath().toString() + "/" + "data.txt";
		File file = new File(path);
		try {
			if(file.createNewFile()) {
				System.out.println("File created at: " + path);
			}
			else {
				System.out.println("File already exist at: " + path);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Write random requests in file
		int i = 0;
		FileWriter fr;
		BufferedWriter br;
		try {
			fr = new FileWriter(file, false);
			br = new BufferedWriter(fr);

			System.out.println("Writing requests into file......");
			while(i < n) {
				try {
					String request = "";
					int originFloor = (int)Math.ceil(Math.random() * 9);
					int destinationFloor = (int)Math.ceil(Math.random() * 9);
					String direction = "";

					//Make sure we don't have two same floor #
					while(originFloor == destinationFloor)
						destinationFloor = (int)Math.ceil(Math.random() * 9);

					//Determine direction
					if(originFloor > destinationFloor)
						direction = "down";
					else
						direction = "up";

					//Write request in file
					request += timeToString() + " " + originFloor + " " + direction + " " + destinationFloor;
					System.out.println("writing request: " + request + "\n");
					br.write(request.toString());
					br.newLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			br.close();
			fr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private static String timeToString() {
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String timestamp = ts.toString().split(" ")[1];

		return timestamp;
	}

	private static void startSystem() {
		FloorSubsystem f = new FloorSubsystem();
		floorHandler h = new floorHandler(f);

		Thread elevatorThread;
		ElevatorSubsystem c = new ElevatorSubsystem();
		ElevatorHandler elevatorHandler = new ElevatorHandler(c);
		elevatorThread = new Thread(elevatorHandler, "New request");

		Thread personThread;
		Scheduler scheduler = new Scheduler();
		PersonHandler personHandler = new PersonHandler(scheduler);
		personThread = new Thread(personHandler, "New request");


		elevatorThread.start();
		personThread.start();
		new Thread(h).start();
	}
}
