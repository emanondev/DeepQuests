package emanondev.quests.utils;

import org.bukkit.entity.Player;

import emanondev.quests.newgui.gui.Gui;

public interface WithGui {
	public String getDisplayName();
	public Gui createEditorGui(Player p,Gui previusHolder);
}
