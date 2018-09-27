package emanondev.quests.require;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.utils.QCWithCooldown;

public interface QuestRequireType extends RequireType {
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the QuestRequire object
	 */
	@Override
	public QuestRequire getInstance(ConfigSection section, QCWithCooldown parent);
}
