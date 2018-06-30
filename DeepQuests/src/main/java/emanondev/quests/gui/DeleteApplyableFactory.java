package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.StringUtils;

public abstract class DeleteApplyableFactory<T extends Applyable<?>> implements EditorButtonFactory {
	private final String selectorGuiTitle;
	private final String confirmationGuiTitle;
	public DeleteApplyableFactory(String selectorGuiTitle,String confirmationGuiTitle) {
		this.selectorGuiTitle = selectorGuiTitle;
		this.confirmationGuiTitle = confirmationGuiTitle;
	}
	protected abstract Collection<T> getCollection();
	protected abstract ArrayList<String> getDeleteButtonDescription();
	protected abstract ArrayList<String> getSelectButtonDescription(T applyable);
	protected abstract ItemStack getSelectedButtonItemStack(T applyable);
	protected abstract ArrayList<String> getConfirmationButtonDescription(T applyable);
	protected abstract void onDelete(T applyable);

	private class DeleteButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.NETHERRACK);

		public DeleteButton(CustomGui parent) {
			super(parent);
			StringUtils.setDescription(item, getDeleteButtonDescription());
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new SelectorGui(clicker, getParent()).getInventory());
		}

		private class SelectorGui extends CustomMultiPageGui<CustomButton> {

			public SelectorGui(Player p, CustomGui previusHolder) {
				super(p, previusHolder, 6, 1);
				this.setTitle(null, StringUtils.fixColorsAndHolders(selectorGuiTitle));
				for (T applyable : getCollection()) {
					this.addButton(new SelectButton(applyable));
				}
				this.setFromEndCloseButtonPosition(8);
				this.reloadInventory();
			}

			private class SelectButton extends CustomButton {
				private ItemStack item;
				private T applyable;

				public SelectButton(T applyable) {
					super(SelectorGui.this);
					this.applyable = applyable;
					this.item = getSelectedButtonItemStack(applyable);
					this.update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				public void update() {
					StringUtils.setDescription(item, getSelectButtonDescription(applyable));
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					clicker.openInventory(new ConfirmationGui(clicker, getParent()).getInventory());
				}

				private class ConfirmationGui extends CustomLinkedGui<CustomButton> {

					public ConfirmationGui(Player p, CustomGui previusHolder) {
						super(p, previusHolder, 6);
						this.addButton(22, new ConfirmationButton());
						this.setTitle(null, StringUtils.fixColorsAndHolders(confirmationGuiTitle));
						this.setFromEndCloseButtonPosition(8);
						reloadInventory();
					}

					private class ConfirmationButton extends CustomButton {
						private ItemStack item = new ItemStack(Material.WOOL);

						public ConfirmationButton() {
							super(ConfirmationGui.this);
							this.item.setDurability((short) 14);
							StringUtils.setDescription(item, getConfirmationButtonDescription(applyable));

						}

						@Override
						public ItemStack getItem() {
							return item;
						}

						@Override
						public void onClick(Player clicker, ClickType click) {
							onDelete(applyable);
							DeleteButton.this.getParent().update();
							clicker.openInventory(DeleteButton.this.getParent().getInventory());
						}
					}
				}
			}
		}
	}
	
	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		if (getCollection().size() > 0)
			return new DeleteButton(parent);
		return null;
	}
}
