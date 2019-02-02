// GenerateTestData
// Generating test request strings to data.txt

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;

public class GenerateTestData {

	public String timeToString() {
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String timestamp = ts.toString().split(" ")[1];

		return timestamp;
	}
	public static void main(String args[]) {

		System.out.println("Test file for Iteration 1\n"
				+ "---------------------------\n"
				+ "Floor system reads requests from data.txt\n"
				+ "Then Floor system will send requests to Scheduler and Elevator\n\n");

		//First test string
		String first = "14:05:15.0 2 up 4";
		//Second test string
		String second = "14:05:15.0 5 down 2";

		//Create data.txt
		System.out.println("Create data file......\n"
				+ "Overwrite the file if existed");
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

		//Write persons in file
		System.out.println("Writing requests into file......");
		try {
			FileWriter fr = new FileWriter(file, false);
			BufferedWriter br = new BufferedWriter(fr);
			System.out.println("writing request: " + first + "\n");
			br.write(first.toString());
			br.newLine();
			System.out.println("writing request: " + second + "\n");
			br.newLine();
			br.write(second.toString());

			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Please run the three Java program in this order\n"
				+ "Scheduler -> ElevatorSubsystem -> FloorSubsystem\n"
				+ "Please check console outputs for three systems");
	}

}
