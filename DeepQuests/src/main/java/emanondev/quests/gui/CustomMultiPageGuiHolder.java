package emanondev.quests.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.utils.StringUtils;

public abstract class CustomMultiPageGuiHolder<T extends CustomGuiItem> extends CustomGuiHolder{

	
	protected final ArrayList<T> items = new ArrayList<T>();

	private int page;
	public CustomMultiPageGuiHolder(Player p, CustomGuiHolder previusHolder, 
				int rows,int page) {
		super(p, previusHolder, Math.max(2, rows));
		this.page = Math.max(1,page);
	}
	public void addButton(T button) {
		if (button!=null)
			items.add(button);
	}
	public void removeButton(int position) {
		if (position>=0 && position<items.size())
			items.remove(position);
	}
	public int getPage() {
		return this.page;
	}
	protected void incrementPage() {
		setPage(page+1);
	}
	protected void decrementPage() {
		setPage(page-1);
	}
	public void setPage(int pag) {
		pag = Math.max(1,pag);
		if (pag==page)
			return;
		if (pag*(size()-9)>items.size())
			return;
		page = pag;
		for (int i = 0; i < size()-9 ; i++)
			if ((getPageOffset()+size()) < items.size() )
				getInventory().setItem(i,items.get(getPageOffset()+i).getItem());
			else
				getInventory().setItem(i,null);
		currPageButton.update();
	}
	public void onSlotClick(Player clicker,int slot,ClickType click) {
		if (slot == size()-this.fromEndPrevPageButtonPosition()) {
			decrementPage();
			return;
		}
		if (slot == size()-this.fromEndNextPageButtonPosition()) {
			incrementPage();
			return;
		}
		if (slot<(size()-9) && getPageOffset()+slot < items.size() ) {
			items.get(getPageOffset()+slot).onClick(clicker,click);
			return;
		}
		super.onSlotClick(clicker,slot,click);
	}
	public void reloadInventory() {
		for (int i = 0; i < size()-9;i++)
			if ((getPageOffset()+i) <items.size())
				getInventory().setItem(i,items.get(getPageOffset()+i).getItem());
			else
				getInventory().setItem(i,null);
		
		if (!nextPageButton.getItem().equals(getInventory().getItem(size()-this.fromEndNextPageButtonPosition())))
				getInventory().setItem(size()-this.fromEndNextPageButtonPosition(),nextPageButton.getItem());
		if (!prevPageButton.getItem().equals(getInventory().getItem(size()-this.fromEndPrevPageButtonPosition())))
				getInventory().setItem(size()-this.fromEndPrevPageButtonPosition(),prevPageButton.getItem());
		if (!currPageButton.getItem().equals(getInventory().getItem(size()-this.fromEndCurrentPageButtonPosition())))
				getInventory().setItem(size()-this.fromEndCurrentPageButtonPosition(),currPageButton.getItem());
		super.reloadInventory();
	}
	public void update() {
		for (T customItem : items)
			customItem.update();
		currPageButton.update();
		reloadInventory();
	}
	private int getPageOffset() {
		return (this.page-1)*(size()-9);
	}
	private int prevPageButtonPos = 6;
	private int nextPageButtonPos = 4;
	private int currPageButtonPos = 5;
	
	public int fromEndPrevPageButtonPosition() {
		return prevPageButtonPos;
	}
	public int fromEndNextPageButtonPosition() {
		return nextPageButtonPos ;
	}
	public int fromEndCurrentPageButtonPosition() {
		return currPageButtonPos ;
	}

	protected boolean setFromEndPrevPageButtonPosition(int i) {
		if (i>0 && i <= size() && i!=prevPageButtonPos) {
			if (getInventory().getItem(size()-prevPageButtonPos).equals(prevPageButton.getItem()))
				getInventory().setItem(size()-prevPageButtonPos,null);
			prevPageButtonPos=i;
			
			getInventory().setItem(size()-prevPageButtonPos,prevPageButton.getItem());
			return true;
		}
		return false;
	}
	protected boolean setFromEndNextPageButtonPosition(int i) {
		if (i>0 && i <= size() && i!=nextPageButtonPos) {
			if (getInventory().getItem(size()-nextPageButtonPos).equals(nextPageButton.getItem()))
				getInventory().setItem(size()-nextPageButtonPos,null);
			nextPageButtonPos=i;
			
			getInventory().setItem(size()-nextPageButtonPos,nextPageButton.getItem());
			return true;
		}
		return false;
	}
	protected boolean setFromEndCurrentPageButtonPosition(int i) {
		if (i>0 && i <= size() && i!=currPageButtonPos) {
			if (getInventory().getItem(size()-currPageButtonPos).equals(currPageButton.getItem()))
				getInventory().setItem(size()-currPageButtonPos,null);
			currPageButtonPos=i;
			
			getInventory().setItem(size()-currPageButtonPos,currPageButton.getItem());
			return true;
		}
		return false;
	}

	protected CustomGuiItem craftPrevPageButton() {
		return new PrevPageButton();
	}
	protected CustomGuiItem craftNextPageButton() {
		return new NextPageButton();
	}
	protected CustomGuiItem craftCurrentPageButton() {
		return new CurrPageButton();
	}

	private static ItemStack loadPrevPageItem() {
		ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.fixColorsAndHolders("&9&l<<<<<<<"));
		item.setItemMeta(meta);
		return item;
	}
	private static ItemStack loadNextPageItem() {
		ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(StringUtils.fixColorsAndHolders("&9&l>>>>>>>"));
		item.setItemMeta(meta);
		return item;
	}
	private final static ItemStack nextPageButtonItem = loadNextPageItem();
	private final static ItemStack prevPageButtonItem = loadPrevPageItem();
	
	public class PrevPageButton extends CustomGuiItem {
		public ItemStack getItem() {
			return prevPageButtonItem;
		}
		public PrevPageButton() {
			super(CustomMultiPageGuiHolder.this);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			decrementPage();
		}
	}
	public class NextPageButton extends CustomGuiItem {
		public ItemStack getItem() {
			return nextPageButtonItem;
		}
		public NextPageButton() {
			super(CustomMultiPageGuiHolder.this);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			incrementPage();
		}
	}
	public class CurrPageButton extends CustomGuiItem {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public CurrPageButton() {
			super(CustomMultiPageGuiHolder.this);
			this.item = new ItemStack(Material.NAME_TAG);
			update();
		}
		public void update() {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(StringUtils.fixColorsAndHolders("&9&lPage <&5&l{page}&9&l>","{page}",""+page));
			item.setItemMeta(meta);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent()==null)
				clicker.closeInventory();
		}
	}
	private CustomGuiItem currPageButton = craftCurrentPageButton();
	private CustomGuiItem nextPageButton = craftNextPageButton();
	private CustomGuiItem prevPageButton = craftPrevPageButton();

}
