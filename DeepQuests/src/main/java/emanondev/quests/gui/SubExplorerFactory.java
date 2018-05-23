package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.WithGui;

public class SubExplorerFactory<T extends WithGui> implements EditorGuiItemFactory {
	public SubExplorerFactory(Collection<T> coll) {
		this.coll = coll;
	}
	private Collection<T> coll;

	public class SubExplorer extends CustomGuiItem {
		public SubExplorer(CustomGuiHolder parent) {
			super(parent);
			item.setAmount(Math.max(1,Math.min(127,coll.size())));
		}
		private ItemStack item = new ItemStack(Material.PAINTING);
		@Override
		public ItemStack getItem() {
			return item;
		}
	
		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new YmlLoadableEditorExplorer(clicker,getParent(),coll).getInventory());
		}
		private class YmlLoadableEditorExplorer extends CustomMultiPageGuiHolder<CustomGuiItem>{
	
			public YmlLoadableEditorExplorer(Player p, CustomGuiHolder previusHolder, Collection<T> coll) {
				super(p, previusHolder, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (T ld : coll)
					addButton(new YLButton(ld));
				reloadInventory();
			}
			
			
			private class YLButton extends CustomGuiItem {
				private final T ld;
				public YLButton(T ld) {
					super(YmlLoadableEditorExplorer.this);
					this.ld = ld;
					update();
				}
				private ItemStack item = new ItemStack(Material.PAPER);
				@Override
				public ItemStack getItem() {
					return item;
				}
				public void update() {
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&e&l"+ld.getDisplayName()));
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(StringUtils.fixColorsAndHolders("&6Click to open editor"));
					meta.setLore(lore);
					getItem().setItemMeta(meta);
				}
				@Override
				public void onClick(Player clicker, ClickType click) {
					ld.openEditorGui(clicker, YmlLoadableEditorExplorer.this);			
				}
				
				
				
			}
				
	
		}
	
	}

	@Override
	public CustomGuiItem getCustomGuiItem(CustomGuiHolder parent) {
		return new SubExplorer(parent);
	}
}

