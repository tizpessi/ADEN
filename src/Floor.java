// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor.
 */
public class Floor {
	// queues to track the up requests and down requests on each floor
	private GenericQueue<Passengers> down;
	private GenericQueue<Passengers> up;
	
	// constructor
	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	// TODO: Write the helper methods for this class. 
	//       The building will need to be able to manipulate the
	//       up and down queues for each floor.... 
	//       This includes accessing all of the lower level queue
	//       methods as well as possibly accessing the contents of each
	//       queue
	/**
	 * adds a person to the floor and organizes them based on up or down
	 * @param person
	 * @param upQueue
	 */
	
	public void addPersonToFloor(Passengers person, boolean upQueue) {
		if (upQueue) {
			up.add(person);
		}
		else {
			down.add(person);
		}
	}
	/**
	 * checks the amount of up calls
	 * @return
	 */
	
	public int callsUpAmount() {
		return up.size();
	}
	
	/**
	 * checks the amount of down calls
	 * @return
	 */
	public int callsDownAmount() {
		return down.size();
	}
	
	/**
	 * checks if there's any calls up
	 * @return
	 */
	public boolean callsUp() {
		return (!up.isEmpty());
	}
	/** 
	 * Checks if there's any calls down
	 * @return
	 */
	public boolean callsDown() {
		return (!down.isEmpty());
	}
	/** 
	 * Checks first passenger of down queue
	 * @return
	 */
	public Passengers peekFirstPassengerMoveToDown() {
		return (down.peek());
	}
	/** 
	 * Checks first passenger of up queue
	 * @return
	 */
	public Passengers peekFirstPassengerMoveToUp() {
		return (up.peek());
	}
	/**
	 * removes a passenger from down queue
	 * @return
	 */
	public Passengers removeFirstPassengerMoveToDown() {
		return (down.poll());
	}
	/**
	 * removes a passenger from up queue
	 * @return
	 */
	public Passengers removeFirstPassengerMoveToUp() {
		return (up.poll());
	}
}
