package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.utils.ApplyableType;
import emanondev.quests.utils.StringUtils;

public abstract class AddApplyableFactory<T extends ApplyableType<?>> implements EditorButtonFactory {
	private final String selectorGuiTitle;
	public AddApplyableFactory(String title) {
		this.selectorGuiTitle = title;
	}
	protected abstract Collection<T> getCollection();
	protected abstract ArrayList<String> getAddButtonDescription();
	protected abstract ArrayList<String> getTypeButtonDescription(T applyable);
	protected abstract ItemStack getTypeButtonItemStack(T applyable);
	protected abstract void onAdd(T applyable);
	
	private class AddButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.GLOWSTONE);

		public AddButton(CustomGui parent) {
			super(parent);
			StringUtils.setDescription(item,getAddButtonDescription());
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new SelectorGui(clicker, this.getParent()).getInventory());
		}

		private class SelectorGui extends CustomMultiPageGui<CustomButton> {
			public SelectorGui(Player p, CustomGui previusHolder) {
				super(p, previusHolder, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (T type : getCollection())
					this.addButton(new ApplyableTypeButton(type));
				this.setTitle(null, StringUtils.fixColorsAndHolders(selectorGuiTitle));
				reloadInventory();
			}

			private class ApplyableTypeButton extends CustomButton {
				private final ItemStack item;
				private final T type;

				public ApplyableTypeButton(T type) {
					super(SelectorGui.this.getPreviusHolder());
					item = getTypeButtonItemStack(type);
					this.type = type;
					StringUtils.setDescription(item, getTypeButtonDescription(type));
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					onAdd(type);
					AddButton.this.getParent().update();
					clicker.openInventory(AddButton.this.getParent().getInventory());
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new AddButton(parent);
	}
}
