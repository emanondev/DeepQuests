package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.WithGui;

public class SubExplorerFactory<T extends WithGui> implements EditorGuiItemFactory {
	public SubExplorerFactory(Class<T> type,Collection<T> coll) {
		this.coll = coll;
		this.type = type;
	}
	private Collection<T> coll;
	private Class<T> type;

	public class SubExplorer extends CustomGuiItem {
		public SubExplorer(CustomGuiHolder parent) {
			super(parent);
			item.setAmount(Math.max(1,Math.min(127,coll.size())));
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			if (type.isAssignableFrom(Task.class)) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lTasks Menù"));
				lore.add(StringUtils.fixColorsAndHolders("&6Click to Select a task to edit"));
			}
			else if (type.isAssignableFrom(Mission.class)) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lMissions Menù"));
				lore.add(StringUtils.fixColorsAndHolders("&6Click to Select a misssion to edit"));
				
			}
			else if (type.isAssignableFrom(Quest.class)) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lQuests Menù"));
				lore.add(StringUtils.fixColorsAndHolders("&6Click to Select a quest to edit"));
				
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
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

