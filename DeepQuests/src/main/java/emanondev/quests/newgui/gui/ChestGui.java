package emanondev.quests.newgui.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import emanondev.quests.utils.Utils;

public abstract class ChestGui implements Gui {
	//int rows;
	private final Gui previusHolder;
	
	public Gui getPreviusGui() {
		return previusHolder;
	}
	
	/**
	 * @return the amount of rows used in this chest gui
	 */
	public int getInventoryRows() {
		return getInventorySize()/9;
	}
	
	/**
	 * Create a chesttype gui
	 * 
	 * @param title - the raw title
	 * @param rows - amount of rows
	 * @param p - targetplayer <br> might be null
	 * @param previusHolder - previusly used gui <br> might be null
	 */
	public ChestGui(String title, int rows,Player p,Gui previusHolder) {
		if (rows < 1 || rows >9)
			throw new IllegalArgumentException("invalid rows size '"+rows+"'");
		//this.rows = rows;
		this.previusHolder = previusHolder;
		this.inv = Bukkit.createInventory(this, rows*9, Utils.fixString(title,player,true));
		this.player = p;
	}
	
	private Inventory inv;

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	private Player player;
	
	public Player getTargetPlayer() {
		return player;
	}
	
	/**
	 * Change the used inventory, remember to call reloadInventory() to reload buttons
	 * @param inv
	 */
	protected void setInventory(Inventory inv) {
		this.inv = inv;
	}

}