package emanondev.quests.require;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public interface Require extends Applyable<YmlLoadableWithCooldown> {
	
	/**
	 * 
	 * @return the object that has this applied
	 */
	public YmlLoadableWithCooldown getParent();
	/**
	 * 
	 * @return the Type
	 */
	public RequireType getType();
	/**
	 * 
	 * @param qPlayer
	 * @return true if qPlayer satisfy this require
	 */
	public boolean isAllowed(QuestPlayer qPlayer);
}
