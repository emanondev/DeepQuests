package emanondev.quests.reward;

abstract class AbstractRewardType {
	private final String key;
	public AbstractRewardType(String key) {
		if (key == null)
			throw new NullPointerException();
		if (key.isEmpty() || key.contains(" ")|| key.contains(":"))
			throw new IllegalArgumentException("invalid task name");
		this.key = key.toUpperCase();
	}
	
	public final String getNameID() {
		return key;
	}
}
