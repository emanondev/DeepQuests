package emanondev.quests.require;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.ApplyableType;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public interface RequireType extends ApplyableType<YmlLoadableWithCooldown> {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	public Require getInstance(MemorySection section, YmlLoadableWithCooldown parent);
}
