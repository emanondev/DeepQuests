package emanondev.quests.newgui.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

public interface Gui extends InventoryHolder {

	/**
	 * 
	 * @return the player target of the gui <br>
	 * (which is the player used for PlaceHolderAPI) <br>
	 * <br>
	 * might be null
	 */
	public Player getTargetPlayer();
	/**
	 * This method is called when a player click the gui
	 * 
	 * @param clicker - who clicked
	 * @param slot - raw slot clicked
	 * @param click - clicktype
	 */
	public void onSlotClick(Player clicker,int slot,ClickType click);
	/**
	 * Called when an update is requested
	 * @return true if any relevant changes has been made
	 */
	public boolean updateInventory();
	/*
	/**
	 * Reload the whole inventory replacing all buttons
	 *//*
	public void reloadInventory();*/
	/**
	 * 
	 * @return the previus used gui<br>
	 * might be null
	 */
	public Gui getPreviusGui();
	/**
	 * 
	 * @return the inventory size
	 * 
	 * shortcut for getInventory().getSize()
	 */
	public default int getInventorySize() {
		return getInventory().getSize();
	}
}