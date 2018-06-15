package emanondev.quests.gui.button;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.StringUtils;

public abstract class AmountEditorButtonFactory implements EditorButtonFactory {
	protected abstract boolean onChange(int amount);

	protected abstract int getAmount();

	protected abstract ArrayList<String> getButtonDescription();

	private final Material material;
	private final short durability;
	private final String title;

	public AmountEditorButtonFactory(String title, Material material) {
		this(title, material, 0);
	}

	/**
	 * 
	 * @param buttonDescription
	 * @param title
	 */
	public AmountEditorButtonFactory(String title, Material material, int durability) {
		this.durability = (short) durability;
		this.material = material;
		this.title = title;
	}

	private class AmountEditorButton extends CustomButton {
		private ItemStack item = new ItemStack(material);

		public AmountEditorButton(CustomGui parent) {
			super(parent);
			if (durability!=0)
				item.setDurability(durability);
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
			clicker.openInventory(new AmountEditorGui(clicker, getParent()).getInventory());
		}

		private class AmountEditorGui extends CustomLinkedGui<CustomButton> {
			public AmountEditorGui(Player p, CustomGui previusHolder) {
				super(p, previusHolder, 6);
				this.addButton(4, new ShowAmountButton());
				this.addButton(19, new EditAmountButton(1));
				this.addButton(20, new EditAmountButton(10));
				this.addButton(21, new EditAmountButton(100));
				this.addButton(22, new EditAmountButton(1000));
				this.addButton(23, new EditAmountButton(10000));
				this.addButton(24, new EditAmountButton(100000));
				this.addButton(25, new EditAmountButton(1000000));
				this.addButton(28, new EditAmountButton(-1));
				this.addButton(29, new EditAmountButton(-10));
				this.addButton(30, new EditAmountButton(-100));
				this.addButton(31, new EditAmountButton(-1000));
				this.addButton(32, new EditAmountButton(-10000));
				this.addButton(33, new EditAmountButton(-100000));
				this.addButton(34, new EditAmountButton(-1000000));
				this.setFromEndCloseButtonPosition(8);
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}

			private class ShowAmountButton extends CustomButton {
				private ItemStack item = new ItemStack(material);

				public ShowAmountButton() {
					super(AmountEditorGui.this);
					if (durability!=0)
						item.setDurability(durability);
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void update() {
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Amount: &e" + getAmount()));
					item.setItemMeta(meta);
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
				}
			}

			private class EditAmountButton extends CustomButton {
				private final int amount;

				private ItemStack item = new ItemStack(Material.WOOL);

				public EditAmountButton(int amount) {
					super(AmountEditorGui.this);
					this.amount = amount;

					ItemMeta meta = item.getItemMeta();
					if (this.amount > 0) {
						this.item.setDurability((short) 5);
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&aAdd " + this.amount));
					} else {
						this.item.setDurability((short) 14);
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&cRemove " + (-this.amount)));
					}
					item.setItemMeta(meta);
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				public void update() {
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					if (click == ClickType.LEFT)
						if (onChange(getAmount() + amount))
							getParent().update();
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new AmountEditorButton(parent);
	}
}
