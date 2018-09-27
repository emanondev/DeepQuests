package emanondev.quests.reward;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.ApplyableType;
import emanondev.quests.utils.QuestComponent;

public interface RewardType extends ApplyableType<QuestComponent> {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	public Reward getInstance(ConfigSection section, QuestComponent parent);
}
