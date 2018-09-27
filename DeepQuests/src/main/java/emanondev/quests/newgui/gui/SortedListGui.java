package emanondev.quests.newgui.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.newgui.button.Button;

public class SortedListGui<T extends Button & Comparable<? super T>> extends PagedChestGui {
	
	
	/**
	 * 
	 * @param title - title
	 * @param rows - amount of rows must be >=2
	 * @param player
	 * @param previusHolder - might be null
	 * <br>page = 1
	 */
	public SortedListGui(String title, int rows, Player player,Gui previusHolder) {
		this(title, rows, player,previusHolder,1);
	}
	/**
	 * 
	 * @param title - title
	 * @param rows - amount of rows must be >=2
	 * @param player
	 * @param previusHolder - mignt be null
	 * @param page
	 */
	public SortedListGui(String title, int rows, Player player,Gui previusHolder,int page) {
		super(title, rows, player,previusHolder,page);
	}

	private final ArrayList<T> buttons = new ArrayList<T>();
	/**
	 * Adds a new Button to the gui
	 * 
	 * @param button
	 */
	public void addButton(T button) {
		if (button == null)
			return;
		buttons.add(button);
		if (sort())
			updateInventory();
		else
			if (buttons.size()>getPageOffset() && buttons.size()<=getPageOffset()+getInventorySize()-9)
				getInventory().setItem(buttons.size()-1-getPageOffset(),buttons.get(buttons.size()-1).getItem());
		
	}

	/**
	 * Remove the specified Button from the gui
	 * 
	 * @param button
	 */
	public void removeButton(int slot) {
		if(slot<0 || slot >= buttons.size())
			return;
		buttons.remove(slot);
		if (slot<=getPageOffset()+getInventorySize()-9)
			reloadInventory();
	}
	
	/**
	 * remove all buttons
	 */
	public void clearButtons() {
		buttons.clear();
		for (int i =0; i<getInventorySize()-9;i++)
			getInventory().setItem(i,null);
	}
	
	@Override
	public void onSlotClick(Player clicker, int slot, ClickType click) {
		super.onSlotClick(clicker,slot,click);
		if (slot>=0 && slot < getInventorySize() -9 && slot+getPageOffset()<buttons.size())
			if (buttons.get(slot+getPageOffset())!=null)
				buttons.get(slot+getPageOffset()).onClick(clicker, click);
	}

	@Override
	public int getMaxSlot() {
		return buttons.size();
	}
	
	@Override
	public boolean updateInventory() {
		boolean result = super.updateInventory();
		for(int i = 0; i < getInventorySize()-9; i++)
			if (getPageOffset() +i<buttons.size())//&&buttons.get(getPageOffset() +i)!=null) 
				if (buttons.get(getPageOffset() +i).update()) {
					getInventory().setItem(i, buttons.get(getPageOffset() +i).getItem());
					result = true;
				}
		return result;
	}
	
	@Override
	public void reloadInventory() {
		super.reloadInventory();
		
		for (int i = 0; i < getInventorySize() - 9; i++)
			if ((getPageOffset() + i) < buttons.size())
				getInventory().setItem(i, buttons.get(getPageOffset() + i).getItem());
			else
				getInventory().setItem(i, null);
	}
	
	private boolean sort() {
		try {
			List<T> copy = new ArrayList<T>(buttons);
			Collections.sort(buttons);
			if (buttons.equals(copy))
				return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}