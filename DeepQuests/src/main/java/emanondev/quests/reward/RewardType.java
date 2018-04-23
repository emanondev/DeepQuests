package emanondev.quests.reward;

public abstract class RewardType extends AbstractRewardType{

	private final Class<? extends Reward> clazz;

	public RewardType(String key,Class<? extends Reward> clazz) {
		super(key);
		if (clazz == null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Class<? extends Reward> getRewardClass(){
		return clazz;
	}
	

}
