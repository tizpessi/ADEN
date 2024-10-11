import java.util.ArrayList;


// peer reviewed by Tiziano and Jeffrey
/**
 * This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targetting each floor...
 * 
 * @author Souma Nagano
 */
public class Elevator {
	
	/** The Constant STOP. */
	// Elevator State Variables
	final static int STOP = 0;
	
	/** The Constant MVTOFLR. */
	final static int MVTOFLR = 1;
	
	/** The Constant OPENDR. */
	final static int OPENDR = 2;
	
	/** The Constant OFFLD. */
	final static int OFFLD = 3;
	
	/** The Constant BOARD. */
	final static int BOARD = 4;
	
	/** The Constant CLOSEDR. */
	final static int CLOSEDR = 5;
	
	/** The Constant MV1FLR. */
	final static int MV1FLR = 6;

	/** The capacity. */
	// Default configuration parameters - these will be read from a file....
	private int capacity = 15;
	
	/** The ticks per floor. */
	private int ticksPerFloor = 5;
	
	/** The ticks door open close. */
	private int ticksDoorOpenClose = 2;  
	
	/** The pass per tick. */
	private int passPerTick = 3;
	
	/** The num floors. */
	private int numFloors;
	
	//State Variables
	/** The curr state. */
	// track the elevator state
	private int currState = STOP;
	
	/** The prev state. */
	private int prevState = STOP;
	
	/** The prev floor. */
	// track what floor you are on, and where you came from
	private int prevFloor = 0;
	
	/** The curr floor. */
	private int currFloor = 0;
	
	/** The direction. */
	// direction 1 = up, -1 = down
	private int direction = 1;
	
	/** The door state. */
	// used to track where the the door is in OPENDR and CLOSEDR states 
	private int doorState = 0;
	
	/** The floor state. */
	private int floorState = 0;
	
	/** The num passengers. */
	// number of passengers on the elevator
	private int numPassengers = 0;
	// when exiting the STOP ==> MVTOFLR, the floor to moveTo and the direction to go in once you
	/** The move to floor. */
	// get there...
	private int moveToFloor = -1;
	
	/** The move to floor dir. */
	private int moveToFloorDir = 1;
	
	/** The passengers. */
	ArrayList<Passengers>[] passengers;
	
	/** The nums passengers. */
	int [] numsPassengers;

	/**
	 * Instantiates a new elevator.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param numFloors the num floors
	 */
	// You need to update this constructor to configure the elevator and set any additional state as necessary.
	@SuppressWarnings("unchecked")
	public Elevator(int numFloors) {
		this.numFloors = numFloors;
		passengers = new ArrayList[numFloors];
		numsPassengers = new int[numFloors];
		for (int i = 0; i < numFloors; i++) {
			passengers[i] = new ArrayList<Passengers>();
			numsPassengers[i] = 0;
		}
	}
	
	/**
	 * Update curr state.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param newState the new state
	 */
	public void updateCurrState(int newState) {
		prevState = currState;
		currState = newState;
		if (prevState != MVTOFLR && prevState != MV1FLR) prevFloor = currFloor;
	}
	
	/**
	 * Checks if is at floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if is at floor
	 */
	public boolean isAtFloor() {
		return floorState == 0;
	}
	
	/**
	 * Move elevator.
	 * peer reviewed by Tiziano and Jeffrey
	 */
	public void moveElevator() {
		if (!isDoorClosed()) throw new IllegalStateException("cannot move elevator if the doors are not closed");
		if (currFloor == numFloors - 1 && direction > 0) throw new IllegalStateException("already at the highest floor");
		if (currFloor == 0 && direction < 0) throw new IllegalStateException("already at the lowest floor");
		if (currState != MVTOFLR && currState != MV1FLR) throw new IllegalStateException("not in move state");
		
		prevFloor = currFloor;
		
		floorState += direction;
		if (floorState % ticksPerFloor == 0) {
			currFloor += floorState / ticksPerFloor;
			floorState = 0;
		}
		
		if (currFloor == moveToFloor) {
			direction = moveToFloorDir;
		}
	}
	
	/**
	 * Checks if is door open.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if is door open
	 */
	public boolean isDoorOpen() {
		return doorState == ticksDoorOpenClose;
	}
	
	/**
	 * Checks if is door closed.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if is door closed
	 */
	public boolean isDoorClosed() {
		return doorState == 0;
	}
	
	/**
	 * Open door.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if successful
	 */
	public boolean openDoor() {
		if (isDoorOpen()) throw new IllegalStateException("door already open");
		if (!isAtFloor()) throw new IllegalStateException("not at a floor");
		if (currState != OPENDR) throw new IllegalStateException("not in open door state");
		doorState++;
		return isDoorOpen();
	}
	
	/**
	 * Close door.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if successful
	 */
	public boolean closeDoor() {
		if (isDoorClosed()) throw new IllegalStateException("door already closed");
		if (currState != CLOSEDR) throw new IllegalStateException("not in close door state");
		doorState--;
		return isDoorClosed();
	}
	
	/**
	 * Insert passenger.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param passengers the passengers
	 * @return true, if successful
	 */
	public boolean insertPassenger(Passengers passengers) {
		if (passengers == null) throw new IllegalArgumentException("passenger must not be null");
		if (currState != BOARD) throw new IllegalStateException("not in board state");
		if (numPassengers + passengers.getNumber() > capacity) return false;
		this.passengers[passengers.getToFloor()].add(passengers);
		numPassengers += passengers.getNumber();
		numsPassengers[passengers.getToFloor()] += passengers.getNumber();
		return true;
	}
	
	/**
	 * Removes the passengers.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the passengers[]
	 */
	public Passengers[] removePassengers() {
		if (currState != OFFLD) throw new IllegalStateException("not in offload state");
		Passengers[] rtn = new Passengers[passengers[currFloor].size()];
		rtn = passengers[currFloor].toArray(rtn);
		passengers[currFloor].clear();
		numPassengers -= numsPassengers[currFloor];
		numsPassengers[currFloor] = 0;
		return rtn;
	}
	
	/**
	 * Checks if is lift empty.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if is lift empty
	 */
	public boolean isLiftEmpty() {
		return numPassengers == 0;
	}
	
	/**
	 * Checks for passengers get off.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return true, if successful
	 */
	public boolean hasPassengersGetOff() {
		return !passengers[currFloor].isEmpty();
	}
	
	/**
	 * Sets the move to floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param floor the new move to floor
	 */
	public void setMoveToFloor(int floor) {
		moveToFloor = floor;
	}
	
	/**
	 * Sets the move to floor dir.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param direction the new move to floor dir
	 */
	public void setMoveToFloorDir(int direction) {
		if (!(direction == 1 || direction == -1)) throw new IllegalArgumentException("direction must be either 1 or -1");
		moveToFloorDir = direction;
	}
	
	/**
	 * Sets the capacity.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param capacity the new capacity
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	/**
	 * Sets the floor ticks.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param floorTicks the new floor ticks
	 */
	public void setFloorTicks(int floorTicks) {
		this.ticksPerFloor = floorTicks;
	}
	
	/**
	 * Sets the door ticks.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param doorTicks the new door ticks
	 */
	public void setDoorTicks(int doorTicks) {
		this.ticksDoorOpenClose = doorTicks;
	}
	
	/**
	 * Sets the tick passengers.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param tickPassengers the new tick passengers
	 */
	public void setTickPassengers(int tickPassengers) {
		this.passPerTick = tickPassengers;
	}
	
	/**
	 * Sets the direction.
	 * peer reviewed by Tiziano and Jeffrey
	 * @param direction the new direction
	 */
	public void setDirection(int direction) {
		if (!(direction == 1 || direction == -1)) throw new IllegalArgumentException("direction must be either 1 or -1");
		this.direction = direction;
	}
	
	/**
	 * Gets the capacity.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Gets the ticks per floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the ticks per floor
	 */
	public int getTicksPerFloor() {
		return ticksPerFloor;
	}
	
	/**
	 * Gets the ticks door open close.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the ticks door open close
	 */
	public int getTicksDoorOpenClose() {
		return ticksDoorOpenClose;
	}
	
	/**
	 * Gets the pass per tick.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the pass per tick
	 */
	public int getPassPerTick() {
		return passPerTick;
	}
	
	/**
	 * Gets the prev state.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the prev state
	 */
	public int getPrevState() {
		return prevState;
	}
	
	/**
	 * Gets the curr state.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the curr state
	 */
	public int getCurrState() {
		return currState;
	}
	
	/**
	 * Gets the prev floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the prev floor
	 */
	public int getPrevFloor() {
		return prevFloor;
	}
	
	/**
	 * Gets the curr floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the curr floor
	 */
	public int getCurrFloor() {
		return currFloor;
	}
	
	/**
	 * Gets the move to floor.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the move to floor
	 */
	public int getMoveToFloor() {
		return moveToFloor;
	}
	
	/**
	 * Gets the move to floor dir.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the move to floor dir
	 */
	public int getMoveToFloorDir() {
		return moveToFloorDir;
	}
	
	/**
	 * Gets the direction.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * Gets the num passengers.
	 * peer reviewed by Tiziano and Jeffrey
	 * @return the num passengers
	 */
	public int getNumPassengers() {
		return numPassengers;
	}
}
