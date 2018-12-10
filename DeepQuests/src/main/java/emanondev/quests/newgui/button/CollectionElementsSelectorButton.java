package emanondev.quests.newgui.button;

import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.ListGui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.Utils;

public abstract class CollectionElementsSelectorButton<E> extends AButton {
	private ItemStack item;
	private final String subGuiTitle;
	private final Collection<E> possibleValues;
	private final boolean canToggleWhitelist;

	public CollectionElementsSelectorButton(String subGuiTitle, ItemStack item, Gui parent,Collection<E> possibleValues) {
		this(subGuiTitle, item, parent, possibleValues,true);
	}
	public CollectionElementsSelectorButton(String subGuiTitle, ItemStack item, Gui parent,Collection<E> possibleValues,boolean canToggleWhitelist) {
		super(parent);
		this.canToggleWhitelist = canToggleWhitelist;
		this.item = item;
		this.possibleValues = possibleValues;
		this.subGuiTitle = subGuiTitle;
		update();
	}

	/**
	 * @return description of the item
	 */
	public abstract List<String> getButtonDescription();
	public abstract List<String> getElementDescription(E element);
	public abstract ItemStack getElementItem(E element);
	
	public abstract boolean currentCollectionContains(E element);
	public abstract boolean getIsWhitelist();
	
	public abstract boolean onToggleElementRequest(E element);
	public abstract boolean onWhiteListChangeRequest(boolean isWhitelist);

	@Override
	public ItemStack getItem() {
		return item;
	}
	
	public boolean update() {
		Utils.updateDescription(item, getButtonDescription(), getParent().getTargetPlayer(), true);
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		clicker.openInventory(new ListEditorGui(clicker).getInventory());
	}
	
	private class ListEditorGui extends ListGui<Button> {

		public ListEditorGui(Player clicker) {//String title, int rows, Player player, Gui previusHolder, int 1) {
			super(subGuiTitle, 6, clicker, CollectionElementsSelectorButton.this.getParent(), 1);
			for (E element:possibleValues) {
				this.addButton(new ElementButton(element));
			}
			if (canToggleWhitelist)
				this.setControlButton(1, new WhitelistFlagButton());
		}

		@Override
		public int loadNextPageButtonPosition() {
			return 7;
		}
		@Override
		public int loadPreviusPageButtonPosition() {
			return 6;
		}
		
		private class WhitelistFlagButton extends FlagButton {

			public WhitelistFlagButton() {
				super(createBlacklistItem(), createWhitelistItem(), ListEditorGui.this);
			}
			@Override
			public List<String> getButtonDescription() {
				if (getIsWhitelist())
					return GuiConfig.Generic.WHITELIST_DESCRIPTION;
				return GuiConfig.Generic.BLACKLIST_DESCRIPTION;
			}
			@Override
			public boolean getCurrentValue() {
				return CollectionElementsSelectorButton.this.getIsWhitelist();
			}
			@Override
			public boolean onValueChangeRequest(boolean value) {
				return CollectionElementsSelectorButton.this.onWhiteListChangeRequest(value);
			}
		}
		
		private class ElementButton extends FlagButton {
			private final E element;
			public ElementButton(E element) {
				super(getElementItem(element),createSelectedElementItem(getElementItem(element)),ListEditorGui.this);
				this.element = element;
			}
			@Override
			public List<String> getButtonDescription() {
				return getElementDescription(element);
			}
			@Override
			public boolean getCurrentValue() {
				if (currentCollectionContains(element))
					return getIsWhitelist();
				else
					return !getIsWhitelist();
			}
			@Override
			public boolean onValueChangeRequest(boolean value) {
				return onToggleElementRequest(element);
			}
			
		}
		
		
		
	}
	private static ItemStack createSelectedElementItem(ItemStack item) {
		return new ItemBuilder(item).setGuiProperty().addEnchantment(Enchantment.DURABILITY,1).build();
	}

	private static ItemStack createBlacklistItem() {
		return new ItemBuilder(Material.RED_WOOL).setGuiProperty().build();
	}

	private static ItemStack createWhitelistItem() {
		return new ItemBuilder(Material.WHITE_WOOL).setGuiProperty().build();
	}

}
