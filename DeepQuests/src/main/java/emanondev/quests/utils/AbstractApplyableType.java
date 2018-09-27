package emanondev.quests.utils;

public abstract class AbstractApplyableType<T extends QuestComponent> implements ApplyableType<T>{
	private final String key;
	public AbstractApplyableType(String key) {
		if (key == null)
			throw new NullPointerException();
		if (key.isEmpty() || key.contains(" ")|| key.contains(":"))
			throw new IllegalArgumentException("invalid reward name '"+key+"'");
		this.key = key.toUpperCase();
	}
	public final String getKey() {
		return key;
	}
}
