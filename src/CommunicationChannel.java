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
		try {
			//System.out.println("To miner " + message.getData());
			minersMessages.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
			//System.out.println("From miner " + msg.getData());
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			//System.out.println("To wizard " + message.getData());
			wizardsMessages.put(message);
			if (message.getParentRoom() == -1 && (message.getData().compareTo(Wizard.EXIT) == 0
					|| message.getData().compareTo(Wizard.END) == 0)) {
				lock.unlock();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lock.getHoldCount() == 2) {
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
			//System.out.println("From wizard " + msg.getData());
			if (msg.getParentRoom() == -1 && (msg.getData().compareTo(Wizard.EXIT) == 0
					|| msg.getData().compareTo(Wizard.END) == 0)) {
				lock2.unlock();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lock2.getHoldCount() == 2) {
			lock2.unlock();
			lock2.unlock();
		}
		return msg;
	}
}
