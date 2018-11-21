import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * Class for a miner.
 */
public class Miner extends Thread {
	/**
	 * Creates a {@code Miner} object.
	 * 
	 * @param hashCount
	 *            number of times that a miner repeats the hash operation when
	 *            solving a puzzle.
	 * @param solved
	 *            set containing the IDs of the solved rooms
	 * @param channel
	 *            communication channel between the miners and the wizards
	 */
	private int hashCount;
	private Set<Integer> solved;
	private CommunicationChannel channel;

	public Miner(Integer hashCount, Set<Integer> solved, CommunicationChannel channel) {
		this.hashCount = hashCount;
		this.solved = solved;
		this.channel = channel;
	}

	public int getHashCount() {
		return hashCount;
	}

	public Set<Integer> getSolved() {
		return solved;
	}

	public CommunicationChannel getChannel() {
		return channel;
	}

	public void setChannel(CommunicationChannel channel) {
		this.channel = channel;
	}

	public void setHashCount(int hashCount) {
		this.hashCount = hashCount;
	}

	public void setSolved(Set<Integer> solved) {
		this.solved = solved;
	}

	private static String encryptMultipleTimes(String input, Integer count) {
		String hashed = input;
		for (int i = 0; i < count; ++i) {
			hashed = encryptThisString(hashed);
		}

		return hashed;
	}

	private static String encryptThisString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// convert to string
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xff & messageDigest[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void run() {
		for(;;) {
			Message messageFromParent = channel.getMessageWizardChannel();
			if (messageFromParent.getData().compareTo(Wizard.END) == 0)
				continue;
			if (messageFromParent.getData().compareTo(Wizard.EXIT) == 0)
				break;
			Message msgFromCurrent = channel.getMessageWizardChannel();
			if (!solved.contains(msgFromCurrent.getCurrentRoom())) {
				solved.add(msgFromCurrent.getCurrentRoom());
				String hashedString = encryptMultipleTimes(msgFromCurrent.getData(), hashCount);
				Message msg = new Message(messageFromParent.getCurrentRoom(), msgFromCurrent.getCurrentRoom(), hashedString);
				channel.putMessageMinerChannel(msg);
			}
		}
	}
}
