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
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.WithGui;

public class EditorGui<T extends WithGui> extends CustomLinkedGui<CustomButton> {
	private final T wg;
	//private final Class<T> clazz;

	public EditorGui(Player p, T wg/*,Class<T> clazz*/, CustomGui previusHolder,
			HashMap<Integer,EditorButtonFactory> facts) {
		super(p, previusHolder, 6);
		if (wg == null /*|| clazz == null*/)
			throw new NullPointerException();
		this.wg = wg;
		//this.clazz = clazz;
		for (Integer key : facts.keySet()) {
			CustomButton button = facts.get(key).getCustomButton(this);
			if (button!=null)
				this.addButton(key,button);
		}
		this.setFromEndCloseButtonPosition(8);
		parentButton = new ParentButton();
		updateTitle();
		reloadInventory();
	}
	public void updateTitle() {
		if (wg instanceof Task) {
			setTitle(null,StringUtils.fixColorsAndHolders("&8Task: &9"+StringUtils.withoutColor(wg.getDisplayName())
					+"&7 ("+((Task) wg).getTaskType().getKey()+")"));
		}
		else if (wg instanceof Mission) {
			setTitle(null,StringUtils.fixColorsAndHolders("&8Mission: &9"+StringUtils.withoutColor(wg.getDisplayName())));
		}
		else if (wg instanceof Quest) {
			setTitle(null,StringUtils.fixColorsAndHolders("&8Quest: &9"+StringUtils.withoutColor(wg.getDisplayName())));
		}
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
	
	public WithGui getLoadable() {
		return wg;
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
	
	public class ParentButton extends CustomButton {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public ParentButton() {
			super(EditorGui.this);
			this.item = new ItemStack(Material.BOOK);
			ItemMeta meta = item.getItemMeta();
			if (wg instanceof Task) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Mission Editor"));
			}
			else if (wg instanceof Mission) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quest Editor"));
			}
			else if (wg instanceof Quest) {
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lBack to Quests Manager"));
			}
			item.setItemMeta(meta);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (wg instanceof Task) {
				((Task) wg).getParent().openEditorGui(clicker, EditorGui.this);
				return;
			}
			else if (wg instanceof Mission) {
				((Mission) wg).getParent().openEditorGui(clicker, EditorGui.this);
				return;
			}
			else if (wg instanceof Quest) {
				((Quest) wg).getParent().openEditorGui(clicker, EditorGui.this);
				return;
			}
		}
	}
}
