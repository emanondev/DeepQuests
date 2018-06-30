package emanondev.quests.require;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.YmlLoadableWithCooldown;

public interface QuestRequireType extends RequireType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the QuestRequire object
	 */
	@Override
	public QuestRequire getInstance(MemorySection section, YmlLoadableWithCooldown parent);
}
