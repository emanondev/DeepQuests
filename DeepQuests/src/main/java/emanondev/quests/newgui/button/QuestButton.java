package emanondev.quests.newgui.button;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.events.PlayerClickQuestGuiEvent;
import emanondev.quests.newgui.gui.MissionsMenu;
import emanondev.quests.newgui.gui.QuestsMenu;
import emanondev.quests.quest.Quest;

public class QuestButton extends QCButton<Quest> {

	public QuestButton(QuestsMenu parent,Quest quest) {
		super(parent,quest);
	}
	
	public QuestsMenu getParent() {
		return (QuestsMenu) super.getParent();
	}
	
	@Override
	public ItemStack getItem() {
		return getParent().getQuestPlayer().getGuiItem(getQuestComponent());
	}

	@Override
	public boolean update() {
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		PlayerClickQuestGuiEvent event = new PlayerClickQuestGuiEvent(clicker,click,this);
		if (click!=ClickType.LEFT)
			event.setCancelled(true);
		switch (getParent().getQuestPlayer().getDisplayState(getQuestComponent())) {
		case LOCKED:
			event.setCancelled(true);
		default:
		}
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		if (click==ClickType.LEFT)
			clicker.openInventory(new MissionsMenu(getTargetPlayer(),getParent(),getQuestComponent()).getInventory());
	
	}

}
