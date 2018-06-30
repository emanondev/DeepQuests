package emanondev.quests.reward;

import emanondev.quests.mission.Mission;

public interface MissionReward extends Reward {
	
	/**
	 * 
	 * @return the object that has this applied
	 */
	public Mission getParent();
	/**
	 * 
	 * @return the Type
	 */
	public MissionRewardType getType();

}
