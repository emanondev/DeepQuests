package emanondev.quests.reward;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.YmlLoadable;

public interface MissionRewardType extends RewardType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public MissionReward getInstance(MemorySection section, YmlLoadable parent);
}
