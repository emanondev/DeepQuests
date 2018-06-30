package emanondev.quests.reward;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.ApplyableType;
import emanondev.quests.utils.YmlLoadable;

public interface RewardType extends ApplyableType<YmlLoadable> {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	public Reward getInstance(MemorySection section, YmlLoadable parent);
}
