package emanondev.quests.reward;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.QuestComponent;

public abstract interface QuestRewardType extends RewardType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public QuestReward getInstance(ConfigSection section, QuestComponent parent);
}
