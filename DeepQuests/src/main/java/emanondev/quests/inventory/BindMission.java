package emanondev.quests.inventory;

import org.bukkit.inventory.ItemStack;

import emanondev.quests.mission.Mission;

public class BindMission{
	private ItemStack item;
	private final Mission mission;
	BindMission(ItemStack item,Mission mission) {
		this.item = item;
		this.mission = mission;
	}
	public ItemStack getItem() {
		return item;
	}
	public Mission getMission() {
		return mission;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
}
