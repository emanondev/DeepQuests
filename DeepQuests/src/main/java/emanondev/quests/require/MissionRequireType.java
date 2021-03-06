package emanondev.quests.require;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.QCWithCooldown;

public interface MissionRequireType extends RequireType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	@Override
	public MissionRequire getInstance(ConfigSection section, QCWithCooldown parent);
}
