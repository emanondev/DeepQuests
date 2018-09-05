package emanondev.quests.reward;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.YmlLoadable;

public abstract interface QuestRewardType extends RewardType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public QuestReward getInstance(ConfigSection section, YmlLoadable parent);
}
