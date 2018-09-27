package emanondev.quests.reward;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.QuestComponent;

public interface MissionRewardType extends RewardType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public MissionReward getInstance(ConfigSection section, QuestComponent parent);
}
