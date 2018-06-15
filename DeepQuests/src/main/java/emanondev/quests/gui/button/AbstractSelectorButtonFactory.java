package emanondev.quests.gui.button;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.StringUtils;

public abstract class AbstractSelectorButtonFactory<T> implements EditorButtonFactory {

	public abstract Collection<T> getCollection();
	protected abstract boolean onSelection(T object);
	protected abstract T getObject();
	protected abstract ArrayList<String> getButtonDescription();
	protected abstract ItemStack getButtonItemStack();
	

	protected abstract ArrayList<String> getObjectButtonDescription(T object);
	protected abstract ItemStack getObjectItemStack(T object);
	private final String title;

	/**
	 * 
	 * @param buttonDescription
	 * @param title
	 */
	public AbstractSelectorButtonFactory(String title) {
		this.title = title;
	}
	


	private class ObjectEditorButton extends CustomButton {
		private ItemStack item;

		public ObjectEditorButton(CustomGui parent) {
			super(parent);
			item = new ItemStack(getButtonItemStack());
			update();
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		public void update() {
			StringUtils.setDescription(item, getButtonDescription());
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new ObjectEditorGui(clicker, getParent()).getInventory());
		}

		private class ObjectEditorGui extends CustomMultiPageGui<CustomButton> {
			public ObjectEditorGui(Player p, CustomGui previusHolder) {
				super(p, previusHolder, 6, 1);
				for (T object: getCollection())
					this.addButton(new ObjectButton(object));
				this.setFromEndCloseButtonPosition(8);
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}

			private class ObjectButton extends CustomButton {
				private ItemStack item;
				private T object;

				public ObjectButton(T object) {
					super(ObjectEditorGui.this);
					this.object = object;
					item = new ItemStack(getObjectItemStack(object));
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void update() {
					StringUtils.setDescription(item, getObjectButtonDescription(object));
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					if (onSelection(object))
						ObjectEditorButton.this.getParent().update();
					clicker.openInventory(ObjectEditorButton.this.getParent().getInventory());
				}
			}
		}
	}
	
	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new ObjectEditorButton(parent);
	}

}
