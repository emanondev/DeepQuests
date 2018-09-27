package emanondev.quests.newgui.gui;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.newgui.button.Button;

public class MapGui extends ChestGui{
	
	public MapGui(String title, int rows, Player p, Gui previusHolder) {
		super(title, rows, p, previusHolder);
	}

	protected HashMap<Integer,Button> buttonsMap = new HashMap<Integer,Button>();
	
	public void putButton(int pos,Button button) {
		if (pos<0)
			throw new IllegalArgumentException();
		if (button!=null)
			buttonsMap.put(pos,button);
		else
			buttonsMap.remove(pos);
		
		if (button!=null)
			getInventory().setItem(pos,button.getItem());
		else
			getInventory().setItem(pos,null);
	}
	
	@Override
	public void onSlotClick(Player clicker, int slot, ClickType click) {
		if (slot>=0 && slot < getInventorySize())
			if (buttonsMap.get(slot)!=null)
				buttonsMap.get(slot).onClick(clicker, click);
				
	}
	
	@Override
	public boolean updateInventory() {
		boolean result = false;
		for(int i = 0; i < getInventorySize(); i++)
			if (buttonsMap.get(i)!=null) 
				if (buttonsMap.get(i).update()) {
					getInventory().setItem(i, buttonsMap.get(i).getItem());
					result = true;
				}
		return result;
	}

}