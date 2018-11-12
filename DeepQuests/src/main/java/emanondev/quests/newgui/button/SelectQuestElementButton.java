package emanondev.quests.newgui.button;

import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.newgui.gui.SortedListGui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.Utils;

public abstract class SelectQuestElementButton<E extends QuestComponent> extends AButton {
	private ItemStack item;
	private final String subGuiTitle;
	protected final Collection<E> possibleValues;
	private final boolean allowNull;
	private final boolean allowBack;
	private final boolean requireConfirm;

	public SelectQuestElementButton(String subGuiTitle, ItemStack item, Gui parent, Collection<E> possibleValues,
			boolean allowNull, boolean allowBack, boolean requireConfirm) {
		super(parent);
		if (possibleValues==null)
			throw new NullPointerException();
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

	private class ListEditorGui extends SortedListGui<ElementButton> {

		public ListEditorGui(Player clicker) {
			super(subGuiTitle, 6, clicker, SelectQuestElementButton.this.getParent(), 1);
			for (E element : possibleValues) {
				this.addButton(new ElementButton(this,element));
			}
			if (allowNull)
				this.setControlButton(0, new NullElementButton());
			if (allowBack)
				this.setControlButton(8, new BackButton(this));
			updateInventory();
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
							clicker.openInventory(SelectQuestElementButton.this.getParent().getInventory());
						}
					});
				}
			}

		}
	}
	
	private class ElementButton extends QCButton<E> {
		private ItemStack item;

		public ElementButton(Gui parent,E element) {
			super(parent,element);
			item = Utils.setDescription(new ItemBuilder(Material.PAPER).setGuiProperty().build()
					,getQuestComponent().getInfo(),null,true);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (requireConfirm)
				clicker.openInventory(new ConfirmGui(getItem(), clicker).getInventory());
			else
				onElementSelectRequest(getQuestComponent());
		}

		private class ConfirmGui extends MapGui {

			public ConfirmGui(ItemStack item, Player p) {
				super(GuiConfig.Generic.CONFIRM_CLICK_GUI_TITLE, 6, p, ElementButton.this.getParent());
				this.putButton(53, new BackButton(this));
				this.putButton(4, new StaticButton(item, ConfirmGui.this) {
					public void onClick(Player clicker, ClickType click) {
					}
				});
				this.putButton(29, new StaticButton(
						GuiConfig.Generic.getConfirmButtonItem(ElementButton.this.getParent().getTargetPlayer()), this) {
					public void onClick(Player clicker, ClickType click) {
						onElementSelectRequest(getQuestComponent());
					}
				});
				this.putButton(33, new StaticButton(
						GuiConfig.Generic.getUnconfirmButtonItem(ElementButton.this.getParent().getTargetPlayer()), this) {
					public void onClick(Player clicker, ClickType click) {
						clicker.openInventory(SelectQuestElementButton.this.getParent().getInventory());
					}
				});
			}
		}

		@Override
		public ItemStack getItem() {
			return Utils.setDescription(item,getQuestComponent().getInfo(),null,true);
		}

		@Override
		public boolean update() {
			return true;
		}
	}

	private static final ItemStack nullElement = getNullElement();

	private static ItemStack getNullElement() {
		return Utils.setDescription(new ItemStack(Material.BARRIER), GuiConfig.Generic.NULL_ELEMENT, null, true);
	}
}