package emanondev.quests.require;

public abstract class AbstractRequireType {
	private final String key;
	public AbstractRequireType(String key) {
		if (key == null)
			throw new NullPointerException();
		if (key.isEmpty() || key.contains(" ")|| key.contains(":"))
			throw new IllegalArgumentException("invalid require name '"+key+"'");
		this.key = key.toUpperCase();
	}
	
	public final String getKey() {
		return key;
	}

}
