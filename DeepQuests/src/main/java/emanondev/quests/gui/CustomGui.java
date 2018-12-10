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

@Deprecated
public abstract class CustomGui implements InventoryHolder {
	private final Player player;
	private final CustomGui previusHolder;
	private int size;
	private Inventory inv;
	
	public CustomGui(Player p,CustomGui previusHolder,int rows) {
		this.player = p;
		this.previusHolder = previusHolder;
		this.size = 9*Math.max(1,rows);
		this.inv = Bukkit.createInventory(this,size);
	}
	public void setTitle(Player p,String title) {
		Inventory oldInv = inv;
		this.inv = Bukkit.createInventory(this,oldInv.getSize(),title);
		inv.setStorageContents(oldInv.getStorageContents());
		if (p!=null)
			p.openInventory(inv);
	}
	
	
	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	public Player getPlayer() {
		return player;
	}
	public abstract void update();
	public void onSlotClick(Player clicker,int slot,ClickType click) {
		if (slot == size()-fromEndBackButtonPosition()) {
			backButton.onClick(clicker,click);
			return;
		}
		if (slot == size()-fromEndCloseButtonPosition()) {
			closeButton.onClick(clicker,click);
			return;
		}
	}
	public Inventory getPreviusInventory() {
		if (previusHolder==null)
			return null;
		return previusHolder.getInventory();
	}
	public CustomGui getPreviusHolder() {
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
	private CustomButton backButton = craftBackButton();
	private CustomButton closeButton = craftCloseButton();
	protected CustomButton getBackButton() {
		return backButton;
	}
	protected CustomButton getCloseButton() {
		return closeButton;
	}
	
	protected boolean setFromEndBackButtonPosition(int i) {
		if (i>0 && i <= size() && i!=backButtonPos) {
			if (getInventory().getItem(size()-backButtonPos)!=null
					&& getInventory().getItem(size()-backButtonPos).equals(backButton.getItem()))
				getInventory().setItem(size()-backButtonPos,null);
			backButtonPos=i;
			
			getInventory().setItem(size()-backButtonPos,backButton.getItem());
			return true;
		}
		return false;
		
	}
	protected boolean setFromEndCloseButtonPosition(int i) {
		if (i>0 && i <= size() && i!=closeButtonPos) {
			if (getInventory().getItem(size()-closeButtonPos)!=null
					&& getInventory().getItem(size()-closeButtonPos).equals(closeButton.getItem()))
				getInventory().setItem(size()-closeButtonPos,null);
			closeButtonPos=i;
			
			getInventory().setItem(size()-closeButtonPos,closeButton.getItem());
			return true;
		}
		return false;
	}
	private int backButtonPos = 9;
	private int closeButtonPos = 1;
	protected CustomButton craftBackButton() {
		return new BackButton();
	}
	protected CustomButton craftCloseButton() {
		return new CloseButton();
	}

	//TODO read material and text by config
	private static ItemStack loadBackItem() {
		ItemStack item = new ItemStack(Material.OAK_DOOR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.fixColorsAndHolders("&c&lGo Back"));
		item.setItemMeta(meta);
		return item;
	}
	private final static ItemStack backButtonItem = loadBackItem();
	public class BackButton extends CustomButton {
		public ItemStack getItem() {
			if (getParent().getPreviusInventory()!=null)
				return backButtonItem;
			return null;
		}
		
		
		public BackButton() {
			super(CustomGui.this);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent().getPreviusHolder()!=null) {
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
	public class CloseButton extends CustomButton {
		public ItemStack getItem() {
			return closeButtonItem;
		}
		
		public CloseButton() {
			super(CustomGui.this);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.closeInventory();
		}
	}
	
	public void reloadInventory() {
		if (backButton!=null&&backButton.getItem()!=null && !backButton.getItem().equals(getInventory().getItem(size()-this.fromEndBackButtonPosition())))
			getInventory().setItem(size()-this.fromEndBackButtonPosition(),getBackButton().getItem());
		if (closeButton!=null&&closeButton.getItem()!=null && !closeButton.getItem().equals(getInventory().getItem(size()-this.fromEndCloseButtonPosition())))
			getInventory().setItem(size()-this.fromEndCloseButtonPosition(),getCloseButton().getItem());
	}
	
	
			
}
