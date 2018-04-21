package emanondev.quests.reward;

public class AbstractReward {

	private final String key;
	private final String displayName;
	public AbstractReward(String key,String displayName) {
		if (key == null)
			throw new NullPointerException();
		if (key.isEmpty() || key.contains(" "))
			throw new IllegalArgumentException("invalid task name");
		this.key = key.toUpperCase();

		if (displayName==null)
			this.displayName = key.toLowerCase().replace("_", " ");
		else
			this.displayName = displayName;
	}
	
	public final String getNameID() {
		return key;
	}
	public String getDisplayName() {
		return displayName;
	}
	

}
