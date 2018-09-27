package emanondev.quests.newgui.button;

import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.utils.Utils;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.ListGui;
import emanondev.quests.newgui.gui.MapGui;

public abstract class SelectOneElementButton<E> extends AButton {
	private ItemStack item;
	private final String subGuiTitle;
	private final Collection<E> possibleValues;
	private final boolean allowNull;
	private final boolean allowBack;
	private final boolean requireConfirm;

	public SelectOneElementButton(String subGuiTitle, ItemStack item, Gui parent, Collection<E> possibleValues,
			boolean allowNull, boolean allowBack, boolean requireConfirm) {
		super(parent);
		this.allowNull = allowNull;
		this.allowBack = allowBack;
		this.item = item;
		this.possibleValues = possibleValues;
		this.subGuiTitle = subGuiTitle;
		this.requireConfirm = requireConfirm;
		update();
	}

	/**
	 * @return description of the item
	 */
	public abstract List<String> getButtonDescription();

	public abstract List<String> getElementDescription(E element);

	public abstract ItemStack getElementItem(E element);

	public abstract void onElementSelectRequest(E element);

	@Override
	public ItemStack getItem() {
		if(possibleValues.isEmpty())
			return null;
		return item;
	}

	public boolean update() {
		Utils.updateDescription(item, getButtonDescription(), getParent().getTargetPlayer(), true);
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		if(possibleValues.isEmpty())
			return;
		clicker.openInventory(new ListEditorGui(clicker).getInventory());
	}

	private class ListEditorGui extends ListGui<Button> {

		public ListEditorGui(Player clicker) {// String title, int rows, Player player, Gui previusHolder, int 1) {
			super(subGuiTitle, 6, clicker, SelectOneElementButton.this.getParent(), 1);
			for (E element : possibleValues) {
				this.addButton(new ElementButton(element));
			}
			if (allowNull)
				this.setControlButton(0, new NullElementButton());
			if (allowBack)
				this.setControlButton(8, new BackButton(this));
		}

		@Override
		public int loadNextPageButtonPosition() {
			return 7;
		}

		@Override
		public int loadPreviusPageButtonPosition() {
			return 6;
		}

		private class NullElementButton extends StaticButton {

			public NullElementButton() {
				super(nullElement, ListEditorGui.this);
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				if (requireConfirm)
					clicker.openInventory(new ConfirmGui(getItem(), clicker).getInventory());
				else
					onElementSelectRequest(null);
			}

			private class ConfirmGui extends MapGui {

				public ConfirmGui(ItemStack item, Player p) {
					super(GuiConfig.Generic.CONFIRM_CLICK_GUI_TITLE, 6, p, ListEditorGui.this);
					this.putButton(53, new BackButton(this));
					this.putButton(4, new StaticButton(item, ConfirmGui.this) {
						public void onClick(Player clicker, ClickType click) {
						}
					});
					this.putButton(29, new StaticButton(
							GuiConfig.Generic.getConfirmButtonItem(ListEditorGui.this.getTargetPlayer()), this) {
						public void onClick(Player clicker, ClickType click) {
							onElementSelectRequest(null);
						}
					});
					this.putButton(33, new StaticButton(
							GuiConfig.Generic.getUnconfirmButtonItem(ListEditorGui.this.getTargetPlayer()), this) {
						public void onClick(Player clicker, ClickType click) {
							clicker.openInventory(SelectOneElementButton.this.getParent().getInventory());
						}
					});
				}
			}

		}

		private class ElementButton extends StaticButton {
			private final E element;

			public ElementButton(E element) {
				super(Utils.setDescription(getElementItem(element), getElementDescription(element), null, true),
						ListEditorGui.this);
				this.element = element;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				if (requireConfirm)
					clicker.openInventory(new ConfirmGui(getItem(), clicker).getInventory());
				else
					onElementSelectRequest(element);
			}

			private class ConfirmGui extends MapGui {

				public ConfirmGui(ItemStack item, Player p) {
					super(GuiConfig.Generic.CONFIRM_CLICK_GUI_TITLE, 6, p, ListEditorGui.this);
					this.putButton(53, new BackButton(this));
					this.putButton(4, new StaticButton(item, ConfirmGui.this) {
						public void onClick(Player clicker, ClickType click) {
						}
					});
					this.putButton(29, new StaticButton(
							GuiConfig.Generic.getConfirmButtonItem(ListEditorGui.this.getTargetPlayer()), this) {
						public void onClick(Player clicker, ClickType click) {
							onElementSelectRequest(element);
						}
					});
					this.putButton(33, new StaticButton(
							GuiConfig.Generic.getUnconfirmButtonItem(ListEditorGui.this.getTargetPlayer()), this) {
						public void onClick(Player clicker, ClickType click) {
							clicker.openInventory(SelectOneElementButton.this.getParent().getInventory());
						}
					});
				}
			}
		}
	}

	private static final ItemStack nullElement = getNullElement();

	private static ItemStack getNullElement() {
		return Utils.setDescription(new ItemStack(Material.BARRIER), GuiConfig.Generic.NULL_ELEMENT, null, true);
	}
}
