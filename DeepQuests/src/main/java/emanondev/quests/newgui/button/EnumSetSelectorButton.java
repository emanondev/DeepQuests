package emanondev.quests.newgui.button;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

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

public abstract class EnumSetSelectorButton<E extends Enum<E>> extends AButton {
	private ItemStack item;
	private final String subGuiTitle;
	private final EnumSet<E> possibleValues;
	private final boolean canToggleWhitelist;

	public EnumSetSelectorButton(Class<E> clazz, String subGuiTitle, ItemStack item, Gui parent, Predicate<E> predicate,
			boolean canToggleWhitelist) {
		super(parent);
		this.canToggleWhitelist = canToggleWhitelist;
		this.item = item;
		this.possibleValues = EnumSet.allOf(clazz);
		if (predicate != null)
			for (E element : clazz.getEnumConstants())
				if (!predicate.test(element))
					possibleValues.remove(element);
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

		public ListEditorGui(Player clicker) {
			super(subGuiTitle, 6, clicker, EnumSetSelectorButton.this.getParent(), 1);
			for (E element : possibleValues) {
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
				return EnumSetSelectorButton.this.getIsWhitelist();
			}
			@Override
			public boolean onValueChangeRequest(boolean value) {
				return EnumSetSelectorButton.this.onWhiteListChangeRequest(value);
			}
		}

		private class ElementButton extends FlagButton {
			private final E element;

			public ElementButton(E element) {
				super(getElementItem(element), createSelectedElementItem(getElementItem(element)), ListEditorGui.this);
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
		return new ItemBuilder(Material.WOOL).setDamage(15).setGuiProperty().build();
	}

	private static ItemStack createWhitelistItem() {
		return new ItemBuilder(Material.WOOL).setGuiProperty().build();
	}

}