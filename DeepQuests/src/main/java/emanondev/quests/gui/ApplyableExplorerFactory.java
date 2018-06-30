package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.StringUtils;

public abstract class ApplyableExplorerFactory<T extends Applyable<?>> implements EditorButtonFactory {
	private final String title;

	public ApplyableExplorerFactory(String title) {
		this.title = title;
	}
	protected abstract ArrayList<String> getExplorerButtonDescription();
	protected abstract Collection<T> getCollection();

	private class ExplorerButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.GOLD_INGOT);

		public ExplorerButton(CustomGui parent) {
			super(parent);
			update();
		}
		public void update() {
			item.setAmount(Math.max(1, Math.min(127, getCollection().size())));
			StringUtils.setDescription(item, getExplorerButtonDescription());
		}
		@Override
		public ItemStack getItem() {
			if (getCollection().isEmpty())
				return null;
			return item;
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getCollection().isEmpty())
				return;
			clicker.openInventory(new Explorer(clicker, this.getParent()).getInventory());
		}
		private class Explorer extends CustomMultiPageGui<CustomButton> {

			public Explorer(Player p, CustomGui parent) {
				super(p, parent, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (T applyable : getCollection())
					addButton(new Button(applyable));
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}
			private class Button extends CustomButton {
				private final T appl;
				private final ItemStack item;

				public Button(T applyable) {
					super(Explorer.this);
					this.appl = applyable;
					this.item = new ItemStack(appl.getType().getGuiItemMaterial());
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6"+appl.getInfo());
					StringUtils.setDescription(item, desc);
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					appl.openEditorGui(clicker, ExplorerButton.this.getParent());
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new ExplorerButton(parent);
	}
}
