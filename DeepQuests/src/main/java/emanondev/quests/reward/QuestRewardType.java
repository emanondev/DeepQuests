package emanondev.quests.reward;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.YmlLoadable;

public abstract interface QuestRewardType extends RewardType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public QuestReward getInstance(MemorySection section, YmlLoadable parent);
}
