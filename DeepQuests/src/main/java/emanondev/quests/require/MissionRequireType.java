package emanondev.quests.require;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.YmlLoadableWithCooldown;

public interface MissionRequireType extends RequireType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public MissionRequire getInstance(MemorySection section, YmlLoadableWithCooldown parent);
}
