package emanondev.quests.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;

public class EditorGui extends CustomMultiPageGuiHolder<CustomGuiItem> {
	private final YmlLoadable loadable;

	public EditorGui(Player p, YmlLoadable loadable, CustomGuiHolder previusHolder,
			ArrayList<EditorGuiItemFactory> facts) {
		super(p, previusHolder, 6, 1);
		if (loadable==null)
			throw new NullPointerException();
		this.loadable = loadable;
		for (EditorGuiItemFactory factory : facts)
			this.addButton(factory.getCustomGuiItem(this));
		this.setFromEndCloseButtonPosition(8);
		parentButton = new ParentButton();
		setTitle(null,loadable.getGuiTitle());
		reloadInventory();
	}
	@Override
	public void onSlotClick(Player clicker,int slot,ClickType click) {
		if (slot == size()-parentButtonPos) {
			parentButton.onClick(clicker,click);
			return;
		}
		super.onSlotClick(clicker,slot,click);
	}
	public void reloadInventory() {
		super.reloadInventory();
		getInventory().setItem(size()-parentButtonPos,parentButton.getItem());
	}
	
	public YmlLoadable getLoadable() {
		return loadable;
	}
	private int parentButtonPos = 1;
	protected boolean setFromEndParentButtonPosition(int i) {
		if (i>0 && i <= size() && i!=parentButtonPos) {
			if (getInventory().getItem(size()-parentButtonPos).equals(parentButton.getItem()))
				getInventory().setItem(size()-parentButtonPos,null);
			parentButtonPos=i;
			
			getInventory().setItem(size()-parentButtonPos,parentButton.getItem());
			return true;
		}
		return false;
	}
	private ParentButton parentButton;
	
	public class ParentButton extends CustomGuiItem {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public ParentButton() {
			super(EditorGui.this);
			this.item = new ItemStack(Material.BOOK);
			ItemMeta meta = item.getItemMeta();
			if (loadable instanceof Task) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Mission Editor"));
			}
			else if (loadable instanceof Mission) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quest Editor"));
			}
			else if (loadable instanceof Quest) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quests Editor"));
			}
			item.setItemMeta(meta);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (loadable instanceof Task) {
				((Task) loadable).getParent().openEditorGui(clicker, EditorGui.this);
				return;
			}
			else if (loadable instanceof Mission) {
				((Mission) loadable).getParent().openEditorGui(clicker, EditorGui.this);
				return;
			}
			else if (loadable instanceof Quest) {
				((Quest) loadable).getParent().openEditorGui(clicker, EditorGui.this);
				return;
			}
		}
	}
}
