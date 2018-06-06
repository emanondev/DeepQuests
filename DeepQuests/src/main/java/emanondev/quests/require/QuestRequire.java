package emanondev.quests.require;

import org.bukkit.entity.Player;

import emanondev.quests.gui.CustomGui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public interface QuestRequire {
	public boolean isAllowed(QuestPlayer p);
	public String getDescription();
	public YmlLoadableWithCooldown getParent();
	public QuestRequireType getRequireType();
	public void openEditorGui(Player p);
	public void openEditorGui(Player p,CustomGui previusHolder);
	public String getNameID();
}
