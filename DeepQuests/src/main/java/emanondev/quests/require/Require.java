package emanondev.quests.require;

import org.bukkit.entity.Player;

import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.QCWithCooldown;

public interface Require extends Applyable<QCWithCooldown> {
	
	/**
	 * 
	 * @return the object that has this applied
	 */
	public QCWithCooldown getParent();
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
	public Gui createEditorGui(Player player, Gui missionEditor);
}
