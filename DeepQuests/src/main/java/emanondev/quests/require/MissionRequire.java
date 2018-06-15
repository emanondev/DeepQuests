package emanondev.quests.require;

import org.bukkit.entity.Player;

import emanondev.quests.gui.CustomGui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public interface MissionRequire {
	public boolean isAllowed(QuestPlayer p);
	public String getDescription();
	public YmlLoadableWithCooldown getParent();
	public MissionRequireType getRequireType();
	public void openEditorGui(Player p);
	public void openEditorGui(Player p,CustomGui previusHolder);
	public String getNameID();
	public String getInfo();
}
