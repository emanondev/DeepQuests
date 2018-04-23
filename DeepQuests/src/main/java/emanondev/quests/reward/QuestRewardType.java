package emanondev.quests.reward;

public abstract class QuestRewardType extends AbstractRewardType {

	private final Class<? extends QuestReward> clazz;
	
	public QuestRewardType(String key,Class<? extends QuestReward> clazz) {
		super(key);
		if(clazz==null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Class<? extends QuestReward> getRewardClass(){
		return clazz;
	}

}
