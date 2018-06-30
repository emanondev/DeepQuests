package emanondev.quests.gui;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;

public class ApplyableGui<T extends YmlLoadable> extends CustomLinkedGui<CustomButton>{
	private Applyable<T> reward;
	private ParentButton parentButton;
	private int parentButtonPos = 1;
	public ApplyableGui(Player p, Applyable<T> reward, CustomGui previusHolder,
			HashMap<Integer, EditorButtonFactory> tools,String title) {
		super(p, previusHolder, 6);
		for (Integer key : tools.keySet()) {
			CustomButton button = tools.get(key).getCustomButton(this);
			if (button!=null)
				this.addButton(key,button);
		}
		this.reward = reward;
		this.setFromEndCloseButtonPosition(8);
		parentButton = new ParentButton();
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
			super(ApplyableGui.this);
			this.item = new ItemStack(Material.BOOK);
			ItemMeta meta = item.getItemMeta();
			if (reward.getParent() instanceof Task) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Task"));
			}
			else if (reward.getParent() instanceof Mission) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Mission"));
			}
			else if (reward.getParent() instanceof Quest) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quest"));
			}
			item.setItemMeta(meta);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			reward.getParent().openEditorGui(clicker, ApplyableGui.this);
		}
	}
}
