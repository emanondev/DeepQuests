package emanondev.quests.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.events.PlayerClickQuestGuiEvent;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.quest.Quest;

public class QuestButton extends CustomButton {
	private final Quest quest;
	
	public QuestButton(PlayerQuestsGui parent,Quest quest) {
		super(parent);
		if (quest==null)
			throw new NullPointerException();
		this.quest = quest;
	}
	
	@Override
	public ItemStack getItem() {
		return getParent().getQuestPlayer().getGuiItem(quest);
	}
	@Override
	public void onClick(Player clicker, ClickType click) {
		PlayerClickQuestGuiEvent event = new PlayerClickQuestGuiEvent(clicker,click,this);
		if (click!=ClickType.LEFT)
			event.setCancelled(true);
		switch (getParent().getQuestPlayer().getDisplayState(quest)) {
		case LOCKED:
			event.setCancelled(true);
		default:
		}
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		if (click==ClickType.LEFT)
			clicker.openInventory(Quests.getInstance().getGuiManager().getMissionsInventory(getParent().getPlayer(),quest,true,getParent().isForcedShow()));
	}
	public PlayerQuestsGui getParent() {
		return (PlayerQuestsGui) super.getParent();
	}
	public Quest getQuest() {
		return quest;
	}
}