import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import myfileio.MyFileIO;

/**
 * The Class ElevatorSimController.
 *
 * @author Souma Nagano
 */
public class ElevatorSimController {
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */
	private Building building;
	
	/** The fio. */
	private MyFileIO fio;

	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num elevators. */
	private final int NUM_ELEVATORS;
	
	/** The num floors. */
	private int numFloors;
	
	/** The num elevators. */
	private int numElevators;
	
	/** The capacity. */
	private int capacity; // # of passengers
	
	/** The floor ticks. */
	private int floorTicks; // the number of ticks of time that it takes the elevator to transition between floors
	
	/** The door ticks. */
	private int doorTicks; // rate at which the doors open and close
	
	/** The tick passengers. */
	private int tickPassengers; // how many passengers can board/exit the elevator in one tick
	
	/** The testfile. */
	private String testfile;
	
	/** The logfile. */
	private String logfile;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/**
	 * Instantiates a new elevator sim controller. 
	 * Reads the configuration file to configure the building and
	 * the elevator characteristics and also select the test
	 * to run. Reads the passenger data for the test to run to
	 * initialize the passenger queue in building...
	 * peer reviewed by Tiziano and Jeffrey
	 * @param gui the gui
	 */
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		fio = new MyFileIO();
		// IMPORTANT: DO NOT CHANGE THE NEXT LINE!!!
		configSimulation("ElevatorSimConfig.csv");
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		logfile = testfile.replaceAll(".csv", ".log");
		building = new Building(NUM_FLOORS,NUM_ELEVATORS,logfile);
		initializePassengerData(testfile);
		
		// YOU still need to configure the elevators in the building here....
		building.configElevators(capacity, floorTicks, doorTicks, tickPassengers);
	}
	
	//TODO: Write methods to update the GUI display
	//      Needs to cover the Elevator state, Elevator passengers
	//      and queues for each floor, as well as the current time
	
	/**
	 * Config simulation. Reads the filename, and parses the
	 * parameters.
	 *
	 * @param filename the filename
	 */
	private void configSimulation(String filename) {
		File configFile = fio.getFileHandle(filename);
		try ( BufferedReader br = fio.openBufferedReader(configFile)) {
			String line;
			while ((line = br.readLine())!= null) {
				String[] values = line.split(",");
				if (values[0].equals("numFloors")) {
					numFloors = Integer.parseInt(values[1]);
				} else if (values[0].equals("numElevators")) {
					numElevators = Integer.parseInt(values[1]);
				} else if (values[0].equals("passCSV")) {
					testfile = values[1];
				} else if (values[0].equals("capacity")) {
					capacity = Integer.parseInt(values[1]);
				} else if (values[0].equals("floorTicks")) {
					floorTicks = Integer.parseInt(values[1]);
				} else if (values[0].equals("doorTicks")) {
					doorTicks = Integer.parseInt(values[1]);
				} else if (values[0].equals("tickPassengers")) {
					tickPassengers = Integer.parseInt(values[1]);
				}
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize passenger data. Reads the supplied filename,
	 * and for each passenger group, identifies the pertinent information
	 * and adds it to the passengers queue in Building...
	 *
	 * @param filename the filename
	 */
	private void initializePassengerData(String filename) {
		int time=0, numPass=0,fromFloor=0, toFloor=0;
		boolean polite = true;
		int wait = 1000;
		boolean firstLine = true;
		File inputData = fio.getFileHandle(filename);
		try (BufferedReader br = fio.openBufferedReader(inputData)) {
			String line;
			while ((line = br.readLine())!= null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] values = line.split(",");
				for (int i = 0; i < values.length; i++) {
					switch (i) {
						case 0 : time      = Integer.parseInt(values[i]); break;
						case 1 : numPass   = Integer.parseInt(values[i]); break;
						case 2 : fromFloor   = Integer.parseInt(values[i]); break;
						case 3 : toFloor  = Integer.parseInt(values[i]); break;
						case 4 : polite = "TRUE".equalsIgnoreCase(values[i]); break;
						case 5 : wait      = Integer.parseInt(values[i]); break;
					}
				}
				//  YOU need to write this code in Building.java
				building.addPassengersToQueue(time,numPass,fromFloor,toFloor,polite,wait);	
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}	
	
	/**
	 * Enable logging. A pass-through from the GUI to building
	 */
	public void enableLogging() {
		building.enableLogging();
	}
	
	// TODO: Write any other helper methods that you may need to access data from the building...
	
	
 	/**
	 * Step sim. See the comments below for the functionality you
	 * must implement......
	 * 
	 */
	public void stepSim() {
 		// DO NOT MOVE THIS - YOU MUST INCREMENT TIME FIRST!
		stepCnt++;
		
		// TODO: Write the rest of this method
		// If simulation is not completed (not all passengers have been processed
		// or elevator(s) are not all in STOP state), then
		// 		1) check for arrival of any new passengers
		// 		2) update the elevator
		// 		3) update the GUI 
		//  else 
		//    	1) update the GUI
		//		2) close the logs
		//		3) process the passenger results
		//		4) send endSimulation to the GUI to stop ticks.
		if (!building.simulationCompleted()) {
			building.checkPassengerQueue(stepCnt);
			building.updateElevator(stepCnt);
			if (gui != null) gui.updateGUI();
		} else {
			if (gui != null) gui.updateGUI();
			building.closeLogs(stepCnt);
			if (gui != null) gui.endSimulation();
		}
	}
	
	/**
	 * Gets the time.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the time
	 */
	public int getTime() {
		return stepCnt;
	}
	
	/**
	 * Gets the test name.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the test name
	 */
	public String getTestName() {
		return testfile;
	}
	
	/**
	 * Gets the up calls on floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param floor
	 * @param elevatorIndex
	 * @return
	 */
	public int getUpCallsOnFloor(int floor, int elevatorIndex) {
		return building.getUpCallsOnFloor(floor, elevatorIndex);
	}
	
	/**
	 * Gets the down calls on floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param floor
	 * @param elevatorIndex
	 * @return
	 */
	public int getDownCallsOnFloor(int floor, int elevatorIndex) {
		return building.getDownCallsOnFloor(floor, elevatorIndex);
	}
	
	/**
	 * gets direction of specified elevator
	 * peer reviewed by Tiziano and Jeffrey
	 * @param elevatorIndex - specified elevator
	 * @return
	 */
	public int getDirection(int elevatorIndex) {
		return building.getDirection(elevatorIndex);
	}
	
	/**
	 * Gets the simulation completion.
	 * 
	 * @return the simulation completion
	 */
	public boolean getSimulationCompletion() {
		return building.simulationCompleted();
	}
	
	/**
	 * Gets the num floors.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the num floors
	 */
	public int getNumFloors() {
		return NUM_FLOORS;
	}
	
	/**
	 * Gets the num elevators.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the num elevators
	 */
	public int getNumElevators() {
		return NUM_ELEVATORS;
	}
	
	/**
	 * Gets the number of passengers on the specified elevator.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param elevatorIndex the elevator index
	 * @return the num passengers
	 */
	public int getNumPassengers(int elevatorIndex) {
		return building.getNumPassengers(elevatorIndex);
	}
	
	/**
	 * Gets the elevator state.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param elevatorIndex the elevator index
	 * @return the state
	 */
	public int getState(int elevatorIndex) {
		return building.getState(elevatorIndex);
	}
	
	/**
	 * Gets the floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param elevatorIndex the elevator index
	 * @return the floor
	 */
	public int getFloor(int elevatorIndex) {
		return building.getFloor(elevatorIndex);
	}
	
	
	/**
	 * Gets the building for the JUnit test.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the building
	 */
	protected Building getBuilding() {
		return building;
	}
}
