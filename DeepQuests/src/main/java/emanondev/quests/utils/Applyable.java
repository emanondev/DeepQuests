package emanondev.quests.utils;

import emanondev.quests.quest.QuestManager;

public interface Applyable<T extends QuestComponent> extends QuestComponent {
	/**
	 * @return util for display purpose
	 * should descript what this object does
	 */
	public String getDescription();
	/**
	 * 
	 * @return the object that has this applied
	 */
	public T getParent();
	/**
	 * 
	 * @return the Type
	 */
	public ApplyableType<T> getType();
	/**
	 * 
	 * @return the Type Key
	 */
	public String getKey();
	/**
	 * unique key for this object on his parent
	 * @return
	 */
	public String getID();
	/**
	 * @return util for display purpose
	 * should descript what this object does
	 */
	public String getInfo();

	@Override
	public default QuestManager getQuestManager() {
		return getParent().getQuestManager();
	}
}