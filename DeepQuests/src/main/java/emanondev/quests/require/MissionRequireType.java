package emanondev.quests.require;

public abstract class MissionRequireType extends AbstractRequireType {
	private final Class<? extends MissionRequire> clazz;

	public MissionRequireType(String key,Class<? extends MissionRequire> clazz) {
		super(key);
		if (clazz == null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Class<? extends MissionRequire> getRequireClass() {
		return clazz;
	}

}
