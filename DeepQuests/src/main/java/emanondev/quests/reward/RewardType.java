package emanondev.quests.reward;

public interface RewardType extends MissionRewardType,QuestRewardType {
	public String getNameID();
	public Reward getRewardInstance(String info);

}
