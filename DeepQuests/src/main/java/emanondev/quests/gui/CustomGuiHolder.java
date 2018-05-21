package emanondev.quests.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.utils.StringUtils;

public abstract class CustomGuiHolder implements InventoryHolder {
	private final Player player;
	private final CustomGuiHolder previusHolder;
	private int size;
	private final Inventory inv;
	
	public CustomGuiHolder(Player p,CustomGuiHolder previusHolder,int rows) {
		this.player = p;
		this.previusHolder = previusHolder;
		this.size = 9*Math.max(1,rows);
		this.inv = Bukkit.createInventory(this,size);
	}
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	public Player getPlayer() {
		return player;
	}
	public abstract void update();
	public abstract void onSlotClick(Player clicker,int slot,ClickType click);
	public Inventory getPreviusInventory() {
		if (previusHolder==null)
			return null;
		return previusHolder.getInventory();
	}
	public CustomGuiHolder getPreviusHolder() {
		return previusHolder;
	}
	public int size() {
		return size;
	}
	public int fromEndBackButtonPosition() {
		return backButtonPos;
	}
	public int fromEndCloseButtonPosition() {
		return closeButtonPos;
	}
	private CustomGuiItem backButton = craftBackButton();
	private CustomGuiItem closeButton = craftCloseButton();
	protected CustomGuiItem getBackButton() {
		return backButton;
	}
	protected CustomGuiItem getCloseButton() {
		return closeButton;
	}
	
	protected boolean setFromEndBackButtonPosition(int i) {
		if (i>0 && i <= size() && i!=backButtonPos) {
			if (getInventory().getItem(size()-backButtonPos).equals(backButton.getItem()))
				getInventory().setItem(size()-backButtonPos,null);
			backButtonPos=i;
			
			getInventory().setItem(size()-backButtonPos,backButton.getItem());
			return true;
		}
		return false;
		
	}
	protected boolean setFromEndCloseButtonPosition(int i) {
		if (i>0 && i <= size() && i!=closeButtonPos) {
			if (getInventory().getItem(size()-closeButtonPos).equals(closeButton.getItem()))
				getInventory().setItem(size()-closeButtonPos,null);
			closeButtonPos=i;
			
			getInventory().setItem(size()-closeButtonPos,closeButton.getItem());
			return true;
		}
		return false;
	}
	private int backButtonPos = 9;
	private int closeButtonPos = 1;
	protected CustomGuiItem craftBackButton() {
		return new BackButton(this);
	}
	protected CustomGuiItem craftCloseButton() {
		return new CloseButton(this);
	}

	//TODO read material and text by config
	private static ItemStack loadBackItem() {
		ItemStack item = new ItemStack(Material.WOOD_DOOR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.fixColorsAndHolders("&c&lGo Back"));
		item.setItemMeta(meta);
		return item;
	}
	private final static ItemStack backButtonItem = loadBackItem();
	public class BackButton extends CustomGuiItem {
		public ItemStack getItem() {
			return backButtonItem;
		}
		
		public BackButton(CustomGuiHolder parent) {
			super(parent);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent().getPreviusHolder()==null)
				clicker.closeInventory();
			else {
				getParent().getPreviusHolder().update();
				clicker.openInventory(getParent().getPreviusInventory());
			}
		}
	}
	private static ItemStack loadCloseItem() {
		ItemStack item = new ItemStack(Material.IRON_DOOR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.fixColorsAndHolders("&c&lClose Gui"));
		item.setItemMeta(meta);
		return item;
	}
	private final static ItemStack closeButtonItem = loadCloseItem();
	public class CloseButton extends CustomGuiItem {
		public ItemStack getItem() {
			return closeButtonItem;
		}
		
		public CloseButton(CustomGuiHolder parent) {
			super(parent);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent()==null)
				clicker.closeInventory();
		}
	}
	
	
			
}
