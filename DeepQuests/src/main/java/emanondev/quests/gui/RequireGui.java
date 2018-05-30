package emanondev.quests.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;

public class RequireGui extends CustomLinkedGui<CustomButton>{
	private AbstractRequire require;
	private ParentButton parentButton;
	private int parentButtonPos = 1;
	public RequireGui(Player p, AbstractRequire require, CustomGui previusHolder,
			HashMap<Integer, EditorButtonFactory> tools,String title) {
		super(p, previusHolder, 6);
		for (Integer key : tools.keySet()) {
			CustomButton button = tools.get(key).getCustomButton(this);
			if (button!=null)
				this.addButton(key,button);
		}
		this.require = require;
		this.setFromEndCloseButtonPosition(8);
		parentButton = new ParentButton();
		//TODO updateTitle();
		reloadInventory();
	}

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
	public class ParentButton extends CustomButton {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public ParentButton() {
			super(RequireGui.this);
			this.item = new ItemStack(Material.BOOK);
			ItemMeta meta = item.getItemMeta();
			if (require.getParent() instanceof Task) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Task"));
			}
			else if (require.getParent() instanceof Mission) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Mission"));
			}
			else if (require.getParent() instanceof Quest) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quest"));
			}
			item.setItemMeta(meta);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (require.getParent() instanceof Task) {
				((Task) require.getParent()).openEditorGui(clicker, RequireGui.this);
				return;
			}
			else if (require.getParent() instanceof Mission) {
				((Mission) require.getParent()).openEditorGui(clicker, RequireGui.this);
				return;
			}
			else if (require.getParent() instanceof Quest) {
				((Quest) require.getParent()).openEditorGui(clicker, RequireGui.this);
				return;
			}
		}
	}
}
