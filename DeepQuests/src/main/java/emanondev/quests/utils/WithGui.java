package emanondev.quests.utils;

import org.bukkit.entity.Player;

import emanondev.quests.gui.CustomGui;

public interface WithGui {
	public void openEditorGui(Player p) ;
	public void openEditorGui(Player p,CustomGui previusHolder);
	public String getDisplayName();
}
