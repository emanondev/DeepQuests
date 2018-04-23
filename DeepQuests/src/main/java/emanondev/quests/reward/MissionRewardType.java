package emanondev.quests.reward;

public abstract class MissionRewardType extends AbstractRewardType {

	private final Class<? extends MissionReward> clazz;

	public MissionRewardType(String key,Class<? extends MissionReward> clazz) {
		super(key);
		if (clazz == null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Class<? extends MissionReward> getRewardClass(){
		return clazz;
	}

}
