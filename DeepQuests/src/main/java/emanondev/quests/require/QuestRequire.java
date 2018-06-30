package emanondev.quests.require;

import emanondev.quests.quest.Quest;

public interface QuestRequire extends Require {

	/**
	 * 
	 * @return the object that has this applied
	 */
	@Override
	public Quest getParent();
	/**
	 * 
	 * @return the Type
	 */
	@Override
	public QuestRequireType getType();
}
