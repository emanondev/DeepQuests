package emanondev.quests.require;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.ApplyableType;
import emanondev.quests.utils.QCWithCooldown;

public interface RequireType extends ApplyableType<QCWithCooldown> {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	public Require getInstance(ConfigSection section, QCWithCooldown parent);
}
