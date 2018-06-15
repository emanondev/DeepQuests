package emanondev.quests.task;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.button.ItemEditorButton;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ItemTaskInfo {
	private final static String PATH_ITEM = "itemstack";
	private final MemorySection section;
	private final Task parent;
	private ItemStack item;

	public ItemTaskInfo(MemorySection m, Task parent) {
		if (m == null || parent == null)
			throw new NullPointerException();
		this.parent = parent;
		this.section = m;
		item = m.getItemStack(PATH_ITEM,null);
	}

	public boolean setItem(ItemStack item) {
		if (this.item == null)
			if (item == null)
				return false;
			else
				this.item = new ItemStack(item);
		else if (item == null)
			this.item = null;
		else if (this.item.isSimilar(item))
			return false;
		else
			this.item = new ItemStack(item);
		if (this.item != null)
			this.item.setAmount(1);
		section.set(PATH_ITEM, this.item);
		parent.setDirty(true);
		return true;
	}

	public ItemStack getItem() {
		return new ItemStack(item);
	}

	public EditorButtonFactory getItemEditorButtonFactory() {
		return new ItemEditorButtonFactory();
	}

	private class ItemEditorButtonFactory implements EditorButtonFactory {
		private class EditEntityTypeButton extends ItemEditorButton {
			private ItemStack item;

			public EditEntityTypeButton(CustomGui parent) {
				super(parent);
				update();
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			public void update() {
				if (ItemTaskInfo.this.item == null)
					this.item = new ItemStack(Material.BARRIER);
				else
					this.item = new ItemStack(ItemTaskInfo.this.item);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lItem Editor");
				desc.add("&6Click to edit");
				if (ItemTaskInfo.this.item == null)
					desc.add("&7No item is set");
				else {
					desc.add("&6Current Item is the item showed with");
					desc.add("&6the following name and lore:");
					if (ItemTaskInfo.this.item.hasItemMeta()) {
						ItemMeta meta = ItemTaskInfo.this.item.getItemMeta();
						if (meta.hasDisplayName())
							desc.add(meta.getDisplayName());
						else
							desc.add("");
						
						if (meta.hasLore())
							desc.addAll(desc.size(),meta.getLore());
					}
				}
				StringUtils.setDescription(item, desc);
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestItem(clicker, changeTitleDesc);
			}

			@Override
			public void onReicevedItem(ItemStack item) {
				if (setItem(item)) {
					update();
					getParent().reloadInventory();
				} else
					getOwner().sendMessage(StringUtils.fixColorsAndHolders("&cSelected item was not a valid item"));
			}

		}

		@Override
		public EditEntityTypeButton getCustomButton(CustomGui parent) {
			return new EditEntityTypeButton(parent);
		}
	}

	private final static BaseComponent[] changeTitleDesc = new ComponentBuilder(
			ChatColor.GOLD + "Click to set the item in your main hand").create();

}
