package emanondev.quests.require;

public abstract class QuestRequireType extends AbstractRequireType {
	private final Class<? extends QuestRequire> clazz;

	public QuestRequireType(String key,Class<? extends QuestRequire> clazz) {
		super(key);
		if (clazz == null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Class<? extends QuestRequire> getRequireClass() {
		return clazz;
	}
}
