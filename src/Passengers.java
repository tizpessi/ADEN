//OWNED BY TIZIANO PESSI
//peer reviewed by Jeffrey and Souma

// TODO: Auto-generated Javadoc
public class Passengers {
	private static int ID=0;
	// this will be initialized in the constructor so that it is unique for each
	// set of Passengers - and then increment the static ID
	private int id;	
	// These will come from the csv file, and should be initialized in the 
	// constructor.
	private int time;
	private int number;
	private int fromFloor;
	private int toFloor;
	private boolean polite;
	private int waitTime;
	// These fields will be initialized during run time - boardTime is when the group
	// starts getting on the elevator, timeArrived is when the elevator starts offloading
	// at the desired floor
	private int boardTime;
	private int timeArrived;
	
	
	// TODO: Write the constructor for this class
		//       Remember to appropriately adjust toFloor and fromFloor 
		//       from American to European numbering...
		
		// TODO: Write the getters and setters for this method
	
	/** //peer reviewed by Jeffrey and Souma
	 * Instantiates a new passengers.
	 *
	 * @param time the time
	 * @param number the number
	 * @param fromFloor the from floor
	 * @param toFloor the to floor
	 * @param polite the polite
	 * @param waitTime the wait time
	 */
	public Passengers(int time, int number, int fromFloor, int toFloor, boolean polite, int waitTime) {
		id = ID;
		ID++;
		
		this.time = time;
		this.number = number;
		this.fromFloor = fromFloor-1; //from american numbering to coding numbering
		this.toFloor = toFloor-1;
		this.polite = polite;
		this.waitTime = waitTime;
		
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * resets static id
	 *
	 * 
	 */
	static void resetStaticID() {
		ID = 0;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the time.
	 *
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the number.
	 *
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the from floor.
	 *
	 * @return the from floor
	 */
	public int getFromFloor() {
		return fromFloor;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the to floor.
	 *
	 * @return the to floor
	 */
	public int getToFloor() {
		return toFloor;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Checks if is polite.
	 *
	 * @return true, if is polite
	 */
	public boolean isPolite() {
		return polite;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Set polite
	 *
	 * @param polite
	 */
	public void setPolite(boolean polite) {
		this.polite = polite;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the wait time.
	 *
	 * @return the wait time
	 */
	public int getWaitTime() {
		return waitTime;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the board time.
	 *
	 * @return the board time
	 */
	public int getBoardTime() {
		return boardTime;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Sets the board time.
	 *
	 * @param boardTime the new board time
	 */
	public void setBoardTime(int boardTime) {
		this.boardTime = boardTime;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Gets the time arrived.
	 *
	 * @return the time arrived
	 */
	public int getTimeArrived() {
		return timeArrived;
	}
	
	/** //peer reviewed by Jeffrey and Souma
	 * Sets the time arrived.
	 *
	 * @param timeArrived the new time arrived
	 */
	public void setTimeArrived(int timeArrived) {
		this.timeArrived = timeArrived;
	}
	
	
	
	
}
