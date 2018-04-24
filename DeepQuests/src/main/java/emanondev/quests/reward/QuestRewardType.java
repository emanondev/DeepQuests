package emanondev.quests.reward;

public abstract interface QuestRewardType {
	public String getNameID();
	public QuestReward getRewardInstance(String info);
}
