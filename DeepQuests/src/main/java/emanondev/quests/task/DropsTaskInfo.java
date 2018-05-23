package emanondev.quests.task;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.StringUtils;

public class DropsTaskInfo {

	private final static String PATH_REMOVE_DROP = "remove-drops";
	private final static String PATH_REMOVE_EXP = "remove-exp";
	private boolean removeDrops;
	private boolean removeExp;
	private final MemorySection section;
	private final Task parent;
	public DropsTaskInfo(MemorySection m,Task parent) {
		this.section = m;
		this.parent = parent;
		removeDrops = m.getBoolean(PATH_REMOVE_DROP,false);
		removeExp = m.getBoolean(PATH_REMOVE_EXP,false);
	}
	
	public boolean areDropsRemoved() {
		return removeDrops;
	}
	public boolean isExpRemoved() {
		return removeExp;
	}
	public boolean setDropsRemoved(boolean value) {
		if (value == removeDrops)
			return false;
		removeDrops = value;
		section.set(PATH_REMOVE_DROP,removeDrops);
		parent.setDirty(true);
		return true;
	}
	public boolean setExpRemoved(boolean value) {
		if (value == removeExp)
			return false;
		removeExp = value;
		section.set(PATH_REMOVE_EXP,removeExp);
		parent.setDirty(true);
		return true;
	}
	public EditorButtonFactory getRemoveDropsEditorButtonFactory(){
		return new RemoveDropsButtonFactory();
	}
	public EditorButtonFactory getRemoveExpEditorButtonFactory(){
		return new RemoveExpButtonFactory();
	}
	
	private class RemoveDropsButtonFactory implements EditorButtonFactory {
		private class RemoveDropsButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.WOOL);
			public RemoveDropsButton(CustomGui parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders(
						"&6&lDrop Editor"));
				item.setItemMeta(meta);
				update();
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				if (removeDrops) {
					item.setDurability((short) 14);
					lore.add(StringUtils.fixColorsAndHolders(
							"&cDrops are removed"));
				}
				else {
					item.setDurability((short) 5);
					lore.add(StringUtils.fixColorsAndHolders(
							"&7Drops are not removed"));
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				setDropsRemoved(!removeDrops);
				update();
				getParent().reloadInventory();
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new RemoveDropsButton(parent);
		}
	}
	private class RemoveExpButtonFactory implements EditorButtonFactory {
		private class RemoveExpButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.WOOL);
			public RemoveExpButton(CustomGui parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders(
						"&6&lExp Drops Editor"));
				item.setItemMeta(meta);
				update();
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				if (removeExp) {
					item.setDurability((short) 14);
					lore.add(StringUtils.fixColorsAndHolders(
							"&cExp Drops are removed"));
				}
				else {
					item.setDurability((short) 5);
					lore.add(StringUtils.fixColorsAndHolders(
							"&7Exp Drops are not removed"));
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				setExpRemoved(!removeExp);
				update();
				getParent().reloadInventory();
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new RemoveExpButton(parent);
		}
	}
	
	

}
