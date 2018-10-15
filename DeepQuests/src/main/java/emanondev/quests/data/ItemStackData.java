package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.ItemEditorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.QuestComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ItemStackData extends QCData {
	private final static String PATH_ITEM = "itemstack";
	private ItemStack item;

	public ItemStackData(ConfigSection m, QuestComponent parent) {
		super(m,parent);
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
		getSection().set(PATH_ITEM, this.item);
		getParent().setDirty(true);
		return true;
	}

	public ItemStack getItem() {
		if (item == null)
			return null;
		return new ItemStack(item);
	}

	public Button getItemSelectorButton(Gui parent) {
		return new ItemSelectorButton(parent);
	}
	private class ItemSelectorButton extends ItemEditorButton {

		public ItemSelectorButton(Gui parent) {
			super(parent);
		}

		@Override
		public ItemStack getCurrentItem() {
			return ItemStackData.this.getItem();
		}

		@Override
		public void onReicevedItem(ItemStack item) {
			if (ItemStackData.this.setItem(item))
				getParent().updateInventory();
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lItem Editor");
			desc.add("&6Click to edit");
			if (ItemStackData.this.item == null)
				desc.add("&7No item is set");
			else {
				desc.add("&6Current Item is the item showed with");
				desc.add("&6the following name and lore:");
			}
			return desc;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			this.requestItem(clicker, changeTitleDesc);
		}
		
	}

	private final static BaseComponent[] changeTitleDesc = new ComponentBuilder(
			ChatColor.GOLD + "Click here to set the item in your main hand").create();

}
