package emanondev.quests.task;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.StringUtils;

public class DropsTaskInfo {

	private final static String PATH_REMOVE_DROP = "remove-drops";
	private final static String PATH_REMOVE_EXP = "remove-exp";
	private boolean removeDrops;
	private boolean removeExp;
	private final ConfigSection section;
	private final Task parent;
	public DropsTaskInfo(ConfigSection m,Task parent) {
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
			private ItemStack item = new ItemStack(Material.GOLD_INGOT);
			public RemoveDropsButton(CustomGui parent) {
				super(parent);
				update();
			}
			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lDrop Flag");
				desc.add("&6Click to Toggle");
				if (removeDrops) {
					desc.add("&cDrops are removed");
				}
				else {
					desc.add("&7Drops are not removed");
					desc.add("&7(Vanilla behavior)");
				}
				StringUtils.setDescription(item, desc);
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
			private ItemStack item = new ItemStack(Material.EXP_BOTTLE);
			public RemoveExpButton(CustomGui parent) {
				super(parent);
				update();
			}
			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lExp Drops Flag");
				desc.add("&6Click to Toggle");
				if (removeExp) {
					desc.add("&cExp Drops are removed");
				}
				else {
					desc.add("&7Exp Drops are not removed");
					desc.add("&7(Vanilla behavior)");
				}
				StringUtils.setDescription(item, desc);
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
