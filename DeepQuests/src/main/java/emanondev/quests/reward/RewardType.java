package emanondev.quests.reward;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.ApplyableType;
import emanondev.quests.utils.YmlLoadable;

public interface RewardType extends ApplyableType<YmlLoadable> {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	public Reward getInstance(ConfigSection section, YmlLoadable parent);
}
