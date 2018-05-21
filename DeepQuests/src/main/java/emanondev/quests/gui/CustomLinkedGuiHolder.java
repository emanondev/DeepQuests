package emanondev.quests.gui;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class CustomLinkedGuiHolder<T extends CustomGuiItem> extends CustomGuiHolder{

	protected final LinkedHashMap<Integer,T> items = new LinkedHashMap<Integer,T>();
	public CustomLinkedGuiHolder(Player p, CustomGuiHolder previusHolder, HashMap<Integer, T> items, int rows) {
		super(p, previusHolder, Math.max(2,rows));
		if (items!=null)
			this.items.putAll(items);
		for (int i = 0; i < size()-9 ; i++)
			if (items.containsKey(i))
				getInventory().setItem(i,items.get(i).getItem());
		getInventory().setItem(size()-this.fromEndBackButtonPosition(),getBackButton().getItem());
		getInventory().setItem(size()-this.fromEndCloseButtonPosition(),getCloseButton().getItem());
		
	}
	public void onSlotClick(Player clicker,int slot,ClickType click) {
		if (slot==size()-this.fromEndBackButtonPosition()) {
			getBackButton().onClick(clicker,click);
			return;
		}
		if (slot == size()-this.fromEndCloseButtonPosition()) {
			getCloseButton().onClick(clicker,click);
			return;
		}
		if (items.containsKey(slot)) {
			items.get(slot).onClick(clicker,click);
			return;
		}
	}
	public void update() {
		for (T customItem : items.values())
			customItem.update();
	}
	
}
