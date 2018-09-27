package emanondev.quests.newgui.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import emanondev.quests.Quests;
import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.NextPageButton;
import emanondev.quests.newgui.button.PreviusPageButton;
import emanondev.quests.utils.Utils;

public abstract class PagedChestGui extends ChestGui implements PagedGui{
	public PagedChestGui(String title, int rows, Player player, Gui previusHolder,int page) {
		super(Utils.fixString(title,player,true,GuiConfig.PAGE_HOLDER,String.valueOf(Math.max(page, 1))), rows, player,previusHolder);
		if (rows==1)
			throw new IllegalArgumentException();
		this.page = Math.max(page, 1);
		//inventories.put(page,getInventory());
		this.rawTitle=title;
		for (int i = 0 ; i < 9; i++)
			getInventory().setItem(getInventorySize()-9+i, GuiConfig.Generic.EMPTY_BUTTON_ITEM);
		
		if (this.loadPreviusPageButtonPosition()>=0&&this.loadPreviusPageButtonPosition()<9)
			setControlButton(loadPreviusPageButtonPosition(),new PreviusPageButton(this));
		
		if (this.loadNextPageButtonPosition()>=0&&this.loadNextPageButtonPosition()<9)
			Bukkit.getScheduler().runTaskLater(Quests.get(), new Runnable() {
				public void run(){
					setControlButton(loadNextPageButtonPosition(),new NextPageButton(PagedChestGui.this));
				}
			}, 1);
		
		if (this.loadBackButtonPosition()>=0&&this.loadBackButtonPosition()<9)
			setControlButton(loadBackButtonPosition(),new BackButton(this));
	}
	private String rawTitle;
	
	private Inventory getInventory(int page) {
		Inventory inv = Bukkit.createInventory(this, getInventorySize(), 
				Utils.fixString(rawTitle,getTargetPlayer(),true,GuiConfig.PAGE_HOLDER,String.valueOf(page)));
		return inv;
	}
	
	private int page;

	@Override
	public int getPage() {
		return page;
	}
	
	/**
	 * returns true if the page changed
	 */
	public boolean setPage(int pag) {
		pag = Math.max(1, pag);
		if (pag == page)
			return false;
		if ((pag - 1) * (getInventorySize() - 9) >= getMaxSlot())
			return false;
		page = pag;
		setInventory(getInventory(page));
		reloadInventory();
		return true;
	}
	
	public void reloadInventory() {
		updateInventory();
		for (int i = 0 ; i < 9; i++)
			if (controlButtons[i]!=null)
				getInventory().setItem(getInventorySize()-9+i, controlButtons[i].getItem());
	}
	/**
	 * 
	 * @param inv
	 * @param page
	 * 
	 * has to set all of the button's items on inv
	 */
	//protected abstract void setInventoryPage(Inventory inv);
	
	private Button[] controlButtons = new Button[9];
	
	/**
	 * control buttons are the last line buttons
	 * 
	 * @param slot - from 0 to 8
	 * @param button - what button? might be null
	 */
	public void setControlButton(int slot,Button button) {
		if (slot<0 || slot>=9)
			return;
		controlButtons[slot]=button;
		getInventory().setItem(getInventorySize()-9+slot,button.getItem());
	}
	public int getMaxPage() {
		return (getMaxSlot()-1)/(getInventorySize()-9)+1;
	}
	
	/*
	private PreviusPageButton previusPageButton = new PreviusPageButton(this);
	private NextPageButton nextPageButton = new NextPageButton(this);
	private BackButton backButton = new BackButton(this);
	
	public NextPageButton getNextPageButton() {
		return nextPageButton;
	}
	public PreviusPageButton getPreviusPageButton() {
		return previusPageButton;
	}
	public BackButton getBackButton() {
		return backButton;
	}*/
	
	/**
	 * @param pos - last line slot 0 to 8 or anything else if won't show
	 * 
	 * override to change position (default 2)
	 * 
	 * warning this method is called in the constructor
	 */
	protected int loadPreviusPageButtonPosition() {
		return 2;
	}
	/**
	 * @param pos - last line slot 0 to 8 or anything else if won't show
	 * 
	 * override to change position (default 6)
	 * 
	 * warning this method is called in the constructor
	 */
	protected int loadNextPageButtonPosition() {
		return 6;
	}
	/**
	 * @param pos - last line slot 0 to 8 or anything else if won't show
	 * 
	 * override to change position (default 8)
	 * 
	 * warning this method is called in the constructor
	 */
	protected int loadBackButtonPosition() {
		return 8;
	}
	
	public void onSlotClick(Player clicker, int slot, ClickType click) {
		if (slot >= getInventorySize()-9 && slot < getInventorySize())
			if (controlButtons[slot-getInventorySize()+9]!=null)
				controlButtons[slot-getInventorySize()+9].onClick(clicker, click);
	}
	/**
	 * utility
	 * @return
	 */
	protected int getPageOffset() {
		return (getPage() - 1) * (getInventorySize() - 9);
	}
	
	public boolean updateInventory() {
		boolean result = false;
		for (int i = 0 ; i < 9; i++) {
			if (controlButtons[i]==null) {
				if (getInventory().getItem(getInventorySize()-9+i)!= GuiConfig.Generic.EMPTY_BUTTON_ITEM)
					getInventory().setItem(getInventorySize()-9+i, GuiConfig.Generic.EMPTY_BUTTON_ITEM);
			}
			else {
				if (controlButtons[i].update()) 
					getInventory().setItem(getInventorySize()-9+i, controlButtons[i].getItem());
			}
		}
		return result;
	}
}