package emanondev.quests.utils;

import java.util.List;

import org.bukkit.entity.Player;

import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.quest.QuestManager;

public interface QuestComponent extends Savable, WithGui,Comparable<QuestComponent> {
	
	/**
	 * 
	 */
	public default int compareTo(QuestComponent qc) {
		if (qc == null)
			return -getPriority();
		return qc.getPriority()-getPriority();
	}
	
	public int getPriority();
	public boolean setPriority(int priority);

	/**
	 * return the parent of this quest component <br>
	 * <br>
	 * Quest <- Mission <- Task <- Require/Reward <br>
	 * Quest <- Mission <- Require/Reward <br>
	 * Quest <- Require/Reward
	 */
	public Savable getParent();
	
	
	public QuestManager getQuestManager();

	
	/**
	 * return the display name of the quest component
	 */
	public String getDisplayName();
	
	/**
	 * return the id of the quest component
	 */
	public String getID();
	
	/**
	 * return a Gui to edit the QuestComponent
	 * 
	 * @param player - the targetPlayer
	 * @param previusGui - the previus gui for the back button
	 */
	public Gui createEditorGui(Player player,Gui previusGui);
	
	/**
	 * 
	 * @return QuestComponent has been modified since last save on disk memory?
	 */
	public boolean isDirty();
	
	/**
	 * 
	 * @param  should be updated on disk memory?
	 */
	public void setDirty(boolean value);
	
	/**
	 * the load of the quest component changed some of it's variables
	 */
	public void setDirtyLoad();
	
	/**
	 * the load of the quest component has changed some of it's variables?
	 */
	public boolean isLoadDirty();
	
	public abstract List<String> getInfo();
}
