package emanondev.quests.reward;

public abstract interface MissionRewardType {
	
	public String getNameID(); 
	public MissionReward getRewardInstance(String info);
}
