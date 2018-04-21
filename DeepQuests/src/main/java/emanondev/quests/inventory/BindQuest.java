package emanondev.quests.inventory;

import org.bukkit.inventory.ItemStack;

import emanondev.quests.quest.Quest;

public class BindQuest{
	private ItemStack item;
	private final Quest quest;
	public BindQuest(ItemStack item,Quest quest) {
		this.item = item;
		this.quest = quest;
	}
	public ItemStack getItem() {
		return item;
	}
	public Quest getQuest() {
		return quest;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
}