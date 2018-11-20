import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that implements the channel used by wizards and miners to communicate.
 */
public class CommunicationChannel {
	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	private ArrayBlockingQueue<Message> minersMessages = new ArrayBlockingQueue<>(1000);
	private ArrayBlockingQueue<Message> wizardsMessages = new ArrayBlockingQueue<>(1000);
	private ReentrantLock lock = new ReentrantLock();
	private ReentrantLock lock2 = new ReentrantLock();
	public static Object someLock = new Object();

	public CommunicationChannel() {
	}

	/**
	 * Puts a message on the miner channel (i.e., where miners write to and wizards
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageMinerChannel(Message message) {
		//lock2.lock();
		try {
			System.out.println("To miner " + message.getData());
			minersMessages.put(message);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//lock2.lock();
		}
	}

	/**
	 * Gets a message from the miner channel (i.e., where miners write to and
	 * wizards read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageMinerChannel() {
		Message msg = null;
		try {
			msg = minersMessages.take();
			System.out.println("From miner " + msg.getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * Puts a message on the wizard channel (i.e., where wizards write to and miners
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	// ReEntrantLock
	public void putMessageWizardChannel(Message message) {
		lock.lock();
		try {
			System.out.println("To wizard " + message.getData());
			wizardsMessages.put(message);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets a message from the wizard channel (i.e., where wizards write to and
	 * miners read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageWizardChannel() {
		Message msg = null;
		try {
			msg = wizardsMessages.take();
			System.out.println("From wizard " + msg.getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
}
