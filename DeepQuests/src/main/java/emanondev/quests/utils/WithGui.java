package emanondev.quests.utils;

import org.bukkit.entity.Player;

import emanondev.quests.gui.CustomGuiHolder;

public interface WithGui {
	public void openEditorGui(Player p) ;
	public void openEditorGui(Player p,CustomGuiHolder previusHolder);
	public String getDisplayName();
	

}
