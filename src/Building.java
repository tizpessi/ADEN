//** OWNED BY TIZIANO PESSI
//peer reviewed by Jeffrey and Souma

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myfileio.MyFileIO;

// TODO: Auto-generated Javadoc 
//java -jar cmpElevator.jar

public class Building {
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	private FileHandler fh;
	private MyFileIO fio;
	private File passDataFile;
	
	private int passengersNotBoarded = 0;
	private int passengersNotOffloaded = 0;
	
	private boolean simulationEnded = false;
	
	private boolean capacityFlag = false;

	// ArrayLists to store those passengers that arrived or gave up...
	private ArrayList<Passengers> passSuccess = new ArrayList<Passengers>(1000);
	private ArrayList<Passengers> gaveUp = new ArrayList<Passengers>(1000);
	
	private GenericQueue<Passengers> passengerQueue = new GenericQueue<Passengers>(1000);
	
	
	// Elevator State Variables
	private final int NUM_FLOORS;
	private final int NUM_ELEVATORS;
	public Floor[] floors;
	private Elevator[] elevators;

	/** //peer reviewed by Jeffrey and Souma
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param numElevators the num elevators
	 * @param logfile the logfile
	 */
	public Building(int numFloors, int numElevators,String logfile) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		
		Passengers.resetStaticID();
		
		
		// Initialize the LOGGER - DO NOT CHANGE THIS!!!
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));
		
		//processPassengerData();
		
		// create the floors and the elevator(s)
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(500); 
		}
		elevators = new Elevator[NUM_ELEVATORS];
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Config elevators.
	 *
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param tickPassengers the tick passengers
	 */
	public void configElevators(int capacity, int floorTicks, int doorTicks, int tickPassengers) {
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(NUM_FLOORS);
			elevators[i].setCapacity(capacity);
			elevators[i].setFloorTicks(floorTicks);
			elevators[i].setDoorTicks(doorTicks);
			elevators[i].setTickPassengers(tickPassengers);
			
			logElevatorConfig(capacity, floorTicks, doorTicks, tickPassengers, elevators[i].getCurrState(), elevators[i].getCurrFloor());
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Simulation completed.
	 *
	 * @return true, if successful
	 */
	public boolean simulationCompleted() {
		return simulationEnded;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Adds the passengers to queue.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param fromFloor the from floor
	 * @param toFloor the to floor
	 * @param polite the polite
	 * @param wait the wait
	 */
	public void addPassengersToQueue(int time,int numPass,int fromFloor,int toFloor,boolean polite,int wait) {
		Passengers newPassengers = new Passengers(time,numPass,fromFloor,toFloor,polite,wait);
		passengerQueue.add(newPassengers);
	}
	
	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/** 
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	// YOU WILL NEED TO CODE ANY MISSING METHODS IN THE APPROPRIATE CLASSES...
	public void updateElevator(int time) {
		for (Elevator lift: elevators) {
			//insertNextPassenger(time);
			
			if (elevatorStateChanged(lift,time))
				logElevatorStateChanged(time,lift.getPrevState(),lift.getCurrState(),lift.getPrevFloor(),lift.getCurrFloor());

			switch (lift.getCurrState()) {
				case Elevator.STOP: lift.updateCurrState(currStateStop(time,lift)); break;
				case Elevator.MVTOFLR: lift.updateCurrState(currStateMvToFlr(time,lift)); break;
				case Elevator.OPENDR: lift.updateCurrState(currStateOpenDr(time,lift)); break;
				case Elevator.OFFLD: lift.updateCurrState(currStateOffLd(time,lift)); break;
				case Elevator.BOARD: lift.updateCurrState(currStateBoard(time,lift)); break;
				case Elevator.CLOSEDR: lift.updateCurrState(currStateCloseDr(time,lift)); break;
				case Elevator.MV1FLR: lift.updateCurrState(currStateMv1Flr(time,lift)); break;
			}
		}
	}
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the state of the elevator.
	 *
	 * @param elevatorIndex the elevator index
	 * @return the state
	 */
	public int getState(int elevatorIndex) {
		Elevator elevator = elevators[elevatorIndex];
		return elevator.getCurrState();
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the number of passengers of the elevator.
	 *
	 * @param elevatorIndex the elevator index
	 * @return the state
	 */
	public int getNumPassengers(int elevatorIndex) {
		Elevator elevator = elevators[elevatorIndex];
		return elevator.getNumPassengers();
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Return the number of calls up on a floor
	 *
	 * @param floor the floor
	 * @param elevatorIndex the elevator index
	 * @return the number of up calls
	 */
	public int getUpCallsOnFloor(int floor, int elevatorIndex) {
		return floors[floor].callsUpAmount();
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Return the number of calls down on a floor
	 *
	 * @param floor the floor
	 * @param elevatorIndex the elevator index
	 * @return the number of down calls
	 */
	public int getDownCallsOnFloor(int floor, int elevatorIndex) {
		return floors[floor].callsDownAmount();
	}

	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the floor the specific elevator is at.
	 *
	 * @param elevatorIndex the elevator index
	 * @return the floor
	 */
	public int getFloor(int elevatorIndex) {
		Elevator elevator = elevators[elevatorIndex];
		return elevator.getCurrFloor();
	}
	
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Check passenger queue for a new call, adds passenger to floor.
	 *
	 * @param time the time
	 */
	public void checkPassengerQueue(int time) {
		
		while (passengerQueue.peek() != null && passengerQueue.peek().getTime() == time) { //I like how this is a loop and not recursion - jeffrey
			Passengers firstPassenger = passengerQueue.peek();
			
			boolean goingUp = firstPassenger.getToFloor() > firstPassenger.getFromFloor();
			int floor = firstPassenger.getFromFloor();

			floors[floor].addPersonToFloor(firstPassenger, goingUp);
			
			int dir = 1;
			if (goingUp == false)
				dir = -1;
			
			logCalls(time, firstPassenger.getNumber(), floor,dir,firstPassenger.getId());
			passengerQueue.remove();
			
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Elevator state changed.
	 *
	 * @param lift the lift
	 * @param time the time
	 * @return true, if successful
	 */
	private boolean elevatorStateChanged(Elevator lift, int time) {
		
		if (!(lift.getPrevState()==lift.getCurrState())) {
			return true;
		}
		
		return false;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state stop.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateStop(int time,Elevator lift) {
		
		if (passengerQueue.isEmpty() && totalUpCalls(-1) == 0 && totalDownCalls(-1) == 0) {
			simulationEnded = true;
			logEndSimulation(time);
			
			processPassengerData();
		}
		
		int currFloor = lift.getCurrFloor();
		Passengers passengerWaitingFloor = stopStatePrioritization(currFloor);
		if (passengerWaitingFloor != null) {
			if (passengerWaitingFloor.getFromFloor() == currFloor) {
				lift.setDirection((passengerWaitingFloor.getToFloor()>=currFloor)?1:-1);
				return Elevator.OPENDR;
			} else {
				lift.setMoveToFloor(passengerWaitingFloor.getFromFloor());
				
				lift.setMoveToFloorDir((passengerWaitingFloor.getToFloor()>=passengerWaitingFloor.getFromFloor())?1:-1);

				lift.setDirection((passengerWaitingFloor.getFromFloor()>=currFloor)?1:-1);
				
				
				return Elevator.MVTOFLR;
			}
		}
		return Elevator.STOP;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Stop state prioritization logic.
	 *
	 * @param currFloor the curr floor
	 * @return the passengers to move to.
	 */
	private Passengers stopStatePrioritization(int currFloor) {
		boolean callsUpCurr = floors[currFloor].callsUp();
		boolean callsDownCurr = floors[currFloor].callsDown();
		int totalUpCalls = totalUpCalls(-1);
		int totalDownCalls = totalDownCalls(-1);
		if (callsUpCurr || callsDownCurr) {
			if (!callsUpCurr) {
				return floors[currFloor].peekFirstPassengerMoveToDown();
			} else if (!callsDownCurr) {
				return floors[currFloor].peekFirstPassengerMoveToUp();
			}
			if (totalUpCalls(currFloor) >= totalDownCalls(currFloor)) {
				return floors[currFloor].peekFirstPassengerMoveToUp();
			} 
			return floors[currFloor].peekFirstPassengerMoveToDown();
		} else {
			if (totalUpCalls > totalDownCalls) {
				return floors[lowestUpCallFloor()].peekFirstPassengerMoveToUp();
			} else if (totalUpCalls < totalDownCalls) {
				return floors[highestDownCallFloor()].peekFirstPassengerMoveToDown();
			} else if (totalUpCalls == totalDownCalls && totalUpCalls != 0) {
				int closestUp = lowestUpCallFloor();
				int closestDown = highestDownCallFloor();
				if ((closestDown-currFloor) < (currFloor-closestUp)) {
					return floors[closestDown].peekFirstPassengerMoveToDown();
				}
				return floors[closestUp].peekFirstPassengerMoveToUp();
			}
			return null;
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Change lift direction.
	 *
	 * @param lift the lift
	 */
	private void changeLiftDirection(Elevator lift) {
		lift.setDirection(lift.getDirection()*-1);
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Lowest floor with up call.
	 *
	 * @return the int
	 */
	private int lowestUpCallFloor() {
		for (int i = 0; i < floors.length; i++) {
			if (floors[i].callsUp()) {
				return i;
			}
		}
		return -1;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Highest floor with down call.
	 *
	 * @return the int
	 */
	private int highestDownCallFloor() {
		for (int i = floors.length-1; i >= 0; i--) {
			if (floors[i].callsDown()) {
				return i;
			}
		}
		return -1;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Total down calls.
	 *
	 * @param floor the floor
	 * @return the int
	 */
	private int totalDownCalls(int floor) {
		int count = 0;
		if (floor != -1) {
			for (int i = floor; i >= 0; i--) {
				if (floors[i].callsDown()) {
					count++;
				}
			}
		} else {
			for (int i = 0; i < floors.length; i++) {
				if (floors[i].callsDown()) {
					count++;
				}
			}
		}
		return count;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Total up calls.
	 *
	 * @param floor the floor
	 * @return the int
	 */
	private int totalUpCalls(int floor) {
		int count = 0;
		if (floor != -1) {
			for (int i = floor; i < floors.length; i++) {
				if (floors[i].callsUp()) {
					count++;
				}
			}
		} else {
			for (int i = 0; i < floors.length; i++) {
				if (floors[i].callsUp()) {
					count++;
				}
			}
		}
		return count;
	}
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state move to flr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateMvToFlr(int time,Elevator lift) {
		lift.moveElevator();
		if (!lift.isAtFloor()) {
			return Elevator.MVTOFLR;
		} else {
			if (lift.getMoveToFloor() == lift.getCurrFloor()) {
				lift.setMoveToFloor(-1);
				return Elevator.OPENDR;
			} else {
				return Elevator.MVTOFLR;
			}
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state open door.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateOpenDr(int time,Elevator lift) {
		if (lift.openDoor() == true) {
			if (lift.hasPassengersGetOff()) {
				passengersNotOffloaded = 0;
				return Elevator.OFFLD;
			} else {
				passengersNotBoarded = 0;
				capacityFlag = false;
				return Elevator.BOARD;
			}
		} else {
			return Elevator.OPENDR;
		}
		
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Passengers to board at a certain floor and direction.
	 *
	 * @param floor the floor
	 * @param dir the dir
	 * @return true, if successful
	 */
	private boolean passengersToBoardSameDir(int floor, int dir) {
		if (dir == 1) {
			return floors[floor].callsUp();
		} else  {
			return floors[floor].callsDown();
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Calls of any type in the current direction, below or above specified floor.
	 *
	 * @param currFloor the curr floor
	 * @param direction the direction
	 * @return true, if successful
	 */
	private boolean callsAnyTypeCurrDir(int currFloor, int direction) {
		if (direction == 1) {
			for (int i = currFloor+1; i < floors.length; i++ ) {
				if (floors[i].callsUp() || floors[i].callsDown()) {
					return true;
				}
			}
		} else {
			for (int i = currFloor-1; i >= 0; i-- ) {
				if (floors[i].callsUp() || floors[i].callsDown()) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state offload.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateOffLd(int time,Elevator lift) {
		int liftCurrFloor = lift.getCurrFloor();
		int liftDir = lift.getDirection();
		Passengers[] passGetOff = lift.removePassengers();
		if (passGetOff != null) {
			for (int i = 0; i < passGetOff.length; i++) {
				passSuccess.add(passGetOff[i]); 
				passengersNotOffloaded += passGetOff[i].getNumber();
				passGetOff[i].setTimeArrived(time);
				logArrival(time, passGetOff[i].getNumber(), liftCurrFloor,passGetOff[i].getId());
			}
		}
		passengersNotOffloaded -= lift.getPassPerTick();
		if (passengersNotOffloaded < 0) passengersNotOffloaded = 0;
		
		if (passengersNotOffloaded == 0) { //done offloading?
			if (passengersToBoardSameDir(liftCurrFloor,liftDir)) {
				passengersNotBoarded = 0;
				capacityFlag = false;
				return Elevator.BOARD;
			} else if (lift.isLiftEmpty() && !callsAnyTypeCurrDir(liftCurrFloor,liftDir)) {
				changeLiftDirection(lift);
				if (passengersToBoardSameDir(liftCurrFloor,-1*liftDir)) {
					passengersNotBoarded = 0;
					capacityFlag = false;
					return Elevator.BOARD;
				}
			}
			return Elevator.CLOSEDR;
		} else {
			return Elevator.OFFLD;
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Remove first passenger at curr floor and dir
	 *
	 * @param lift the lift
	 */
	private void removeFirstPsg(Elevator lift, int liftCurrFloor) {
		if (lift.getDirection()==1) {
			floors[liftCurrFloor].removeFirstPassengerMoveToUp();
		} else {
			floors[liftCurrFloor].removeFirstPassengerMoveToDown();
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Board passengers, deal with give ups and capacity skips
	 *
	 * @param lift the lift
	 * @param time the time
	 * @param liftCurrFloor the lift's curr floor
	 * @param liftDir the lift's curr direction
	 */
	private void boardLoopHelperMethod(Elevator lift, int time, int liftCurrFloor, int liftDir) {
		while (doneBoarding(lift,liftCurrFloor) == false && capacityFlag == false) {
			Passengers p;
			if (liftDir==1) {
				p = floors[liftCurrFloor].peekFirstPassengerMoveToUp();
			} else {
				p = floors[liftCurrFloor].peekFirstPassengerMoveToDown();
			}
			if (p.getTime() + p.getWaitTime() < time) {
				logGiveUp(time,p.getNumber(),liftCurrFloor, liftDir, p.getId());
				gaveUp.add(p);
				removeFirstPsg(lift, liftCurrFloor);
			} else {
				if (lift.insertPassenger(p) == false) {
					logSkip(time, p.getNumber(), liftCurrFloor, liftDir, p.getId());
					capacityFlag = true;
				} else {
					passengersNotBoarded += p.getNumber();
					p.setBoardTime(time);
					logBoard(time, p.getNumber(), liftCurrFloor, liftDir, p.getId());
					removeFirstPsg(lift, liftCurrFloor);
				}
			}
		}
	}
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state board.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateBoard(int time,Elevator lift) {
		int liftCurrFloor = lift.getCurrFloor();
		int liftDir = lift.getDirection();
		
		boardLoopHelperMethod(lift, time, liftCurrFloor, liftDir);
		
		if (capacityFlag == true) { //force passengers to polite if capacity full
			if (liftDir==1) {
				Passengers p = floors[liftCurrFloor].peekFirstPassengerMoveToUp();
				if (p != null)
					p.setPolite(true);
			} else {
				Passengers p = floors[liftCurrFloor].peekFirstPassengerMoveToDown();
				if (p != null)
					p.setPolite(true);
			}
		}
		passengersNotBoarded -= lift.getPassPerTick();
		if (passengersNotBoarded < 0) passengersNotBoarded = 0;
		
		if ((doneBoarding(lift,liftCurrFloor) && passengersNotBoarded == 0)) {
			return Elevator.CLOSEDR;
		} else {
			return Elevator.BOARD;
		}
	}
	
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Done boarding; no more passengers to board at current floor
	 *
	 * @param lift the lift
	 * @return true, if successful
	 */
	private boolean doneBoarding(Elevator lift, int liftCurrFloor) {
		if (capacityFlag == true)
			return true;
		if (lift.getDirection() == 1 ) {
			return !floors[liftCurrFloor].callsUp();
		} else {
			return !floors[liftCurrFloor].callsDown();
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state close dr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateCloseDr(int time,Elevator lift) {
		boolean closedDoor = lift.closeDoor();
		int liftCurrFloor = lift.getCurrFloor();
		int liftDir = lift.getDirection();
		Passengers p;
		if (liftDir==1) {
			p = floors[liftCurrFloor].peekFirstPassengerMoveToUp();
		} else {
			p = floors[liftCurrFloor].peekFirstPassengerMoveToDown();
		}
		if (p != null && p.isPolite() == false) {
			p.setPolite(true);
			return Elevator.OPENDR;
		}
		if (closedDoor == true) {
			if (!lift.isLiftEmpty()) {
				return Elevator.MV1FLR;
			} else {
				boolean callsSameDir = callsAnyTypeCurrDir(liftCurrFloor,liftDir);
				boolean callsOpp = callsAnyTypeCurrDir(liftCurrFloor,-1*liftDir);
				if (callsSameDir) {
					return Elevator.MV1FLR;
				} else if (callsOpp) {
					changeLiftDirection(lift);
					return Elevator.MV1FLR;
				}
				return Elevator.STOP;
			}
		} else {
			return Elevator.CLOSEDR;
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Curr state move 1 flr.
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	private int currStateMv1Flr(int time,Elevator lift) {
		lift.moveElevator();
		
		int liftCurrFloor = lift.getCurrFloor();
		int liftDir = lift.getDirection();
		
		if (!lift.isAtFloor()) {
			
			return Elevator.MV1FLR;
		} else {
			if (passengersAtFloorAndDir(liftCurrFloor,liftDir) || lift.hasPassengersGetOff()) {
				return Elevator.OPENDR;
			} else if (lift.isLiftEmpty() == true && !callsAnyTypeCurrDir(liftCurrFloor,liftDir)) {
				changeLiftDirection(lift);
				return Elevator.OPENDR;
			}
		}
		return Elevator.MV1FLR;
	}
	
	
	/** //peer reviewed by Jeffrey and Souma
	 * Passengers waiting at specified floor and dir.
	 *
	 * @param currFloor the curr floor
	 * @param dir the dir
	 * @return true, if successful
	 */
	private boolean passengersAtFloorAndDir(int currFloor,int dir) {
		if (dir == 1) {
			if (floors[currFloor].callsUp()) {
				return true;
			}
		} else {
			if (floors[currFloor].callsDown()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumber()+","+(p.getFromFloor()+1)+","+(p.getToFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumber()+","+(p.getFromFloor()+1)+","+(p.getToFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
		for (Elevator el:elevators)
			logElevatorConfig(el.getCapacity(),el.getTicksPerFloor(), el.getTicksDoorOpenClose(), el.getPassPerTick(), el.getCurrState(), el.getCurrFloor());
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Returns the direction of specified elevator.
	 */
	public int getDirection(int elevatorIndex) {
		return elevators[elevatorIndex].getDirection();
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		+"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		+" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}


