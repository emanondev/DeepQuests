package emanondev.quests.gui;

import java.util.LinkedHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class CustomLinkedGui<T extends CustomButton> extends CustomGui{

	private final LinkedHashMap<Integer,T> items = new LinkedHashMap<Integer,T>();
	public CustomLinkedGui(Player p, CustomGui previusHolder, int rows) {
		super(p, previusHolder, Math.max(2,rows));
	}
	public void reloadInventory() {
		for (int i = 0 ; i < size() ; i++)
			if (items.containsKey(i) && items.get(i)!=null) {
				getInventory().setItem(i,items.get(i).getItem());
			}
			else
				getInventory().setItem(i,null);
		super.reloadInventory();
	}
	public void addButton(int slot,T button) {
		items.put(slot,button);
	}
	public void removeButton(int slot) {
		items.remove(slot);
	}
	public void onSlotClick(Player clicker,int slot,ClickType click) {
		if (items.containsKey(slot) && items.get(slot)!=null) {
			items.get(slot).onClick(clicker,click);
			return;
		}
		super.onSlotClick(clicker,slot,click);
	}
	public void update() {
		for (T customItem : items.values())
			customItem.update();
		reloadInventory();
	}
	
}
