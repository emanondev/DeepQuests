package emanondev.quests.require;

public abstract class RequireType extends AbstractRequireType {
	private final Class<? extends Require> clazz;

	public RequireType(String key,Class<? extends Require> clazz) {
		super(key);
		if (clazz == null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Class<? extends Require> getRequireClass() {
		return clazz;
	}

}
