import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that implements the channel used by wizards and miners to communicate.
 */
public class CommunicationChannel {
	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	private ArrayBlockingQueue<Message> minersMessages;
	private ArrayBlockingQueue<Message> wizardsMessages;
	private ReentrantLock lock;
	private ReentrantLock lock2;
	private final static int numberOfLocks = 2;
	private final static int roomEnd = -1;
	private final static int maxSize = 1000;

	public CommunicationChannel() {
		minersMessages = new ArrayBlockingQueue<>(maxSize);
		wizardsMessages = new ArrayBlockingQueue<>(maxSize);
		lock = new ReentrantLock();
		lock2 = new ReentrantLock();
	}

	/**
	 * Puts a message on the miner channel (i.e., where miners write to and wizards
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageMinerChannel(Message message) {
		try {
			minersMessages.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		} catch (InterruptedException e) {
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
			//System.out.println("To wizard " + message.getData());
			wizardsMessages.put(message);
			if (message.getCurrentRoom() == roomEnd && (message.getData().compareTo(Wizard.EXIT) == 0
					|| message.getData().compareTo(Wizard.END) == 0)) {
				lock.unlock();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (lock.getHoldCount() == numberOfLocks) {
			lock.unlock();
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
		lock2.lock();
		Message msg = null;
		try {
			msg = wizardsMessages.take();
			if (msg.getCurrentRoom() == roomEnd && (msg.getData().compareTo(Wizard.EXIT) == 0
					|| msg.getData().compareTo(Wizard.END) == 0)) {
				lock2.unlock();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (lock2.getHoldCount() == numberOfLocks) {
			lock2.unlock();
			lock2.unlock();
		}
		return msg;
	}
}
