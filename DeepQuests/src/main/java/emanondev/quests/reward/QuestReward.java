package emanondev.quests.reward;

import emanondev.quests.quest.Quest;

public interface QuestReward extends Reward {
	
	/**
	 * 
	 * @return the object that has this applied
	 */
	public Quest getParent();
	/**
	 * 
	 * @return the Type
	 */
	public QuestRewardType getType();
}
