package emanondev.quests.newgui.button;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.Utils;

public abstract class AmountSelectorButton extends AButton {
	private ItemStack item;
	private final String subGuiTitle;

	public AmountSelectorButton(String subGuiTitle, ItemStack item, Gui parent) {
		super(parent);
		this.item = item;
		this.subGuiTitle = subGuiTitle;
		update();
	}

	/**
	 * @return description of the item
	 */
	public abstract List<String> getButtonDescription();

	public abstract long getCurrentAmount();

	public abstract boolean onAmountChangeRequest(long i);

	@Override
	public ItemStack getItem() {
		return item;
	}

	public boolean update() {
		Utils.updateDescription(item, getButtonDescription(), getParent().getTargetPlayer(), true, GuiConfig.AMOUNT_HOLDER,
				getCurrentAmount() + "");
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		clicker.openInventory(new AmountEditorGui().getInventory());
	}

	private class AmountEditorGui extends MapGui {
		public AmountEditorGui() {
			super(Utils.fixString(subGuiTitle, AmountSelectorButton.this.getTargetPlayer(), true), 6,
					AmountSelectorButton.this.getTargetPlayer(), AmountSelectorButton.this.getParent());
			this.putButton(4, new ShowAmountButton());
			this.putButton(19, new EditAmountButton(1));
			this.putButton(20, new EditAmountButton(10));
			this.putButton(21, new EditAmountButton(100));
			this.putButton(22, new EditAmountButton(1000));
			this.putButton(23, new EditAmountButton(10000));
			this.putButton(24, new EditAmountButton(100000));
			this.putButton(25, new EditAmountButton(1000000));
			this.putButton(28, new EditAmountButton(-1));
			this.putButton(29, new EditAmountButton(-10));
			this.putButton(30, new EditAmountButton(-100));
			this.putButton(31, new EditAmountButton(-1000));
			this.putButton(32, new EditAmountButton(-10000));
			this.putButton(33, new EditAmountButton(-100000));
			this.putButton(34, new EditAmountButton(-1000000));
			this.putButton(53, new BackButton(this));
		}

		private class ShowAmountButton extends AButton {
			private ItemStack item = new ItemStack(Material.DIODE);

			public ShowAmountButton() {
				super(AmountEditorGui.this);
				update();
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public boolean update() {
				Utils.updateDescription(item,GuiConfig.Generic.AMOUNT_SELECTOR_SHOW, getTargetPlayer(), true,GuiConfig.AMOUNT_HOLDER,""+ getCurrentAmount());
				return true;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {}

		}

		private class EditAmountButton extends StaticButton {
			private final long amount;

			public EditAmountButton(long amount) {
				super(craftEditorAmountButtonItem(amount),AmountEditorGui.this);
				this.amount = amount;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				if (click == ClickType.LEFT)
					if (onAmountChangeRequest(getCurrentAmount() + amount))
						getParent().updateInventory();
			}
		}
	}

	
	private static ItemStack craftEditorAmountButtonItem(long amount) {
		ItemStack item;
		if (amount > 0) {
			item = new ItemBuilder(Material.WOOL).setDamage(5).build();
			Utils.updateDescription(item,GuiConfig.Generic.AMOUNT_SELECTOR_ADD, null, true,
					GuiConfig.AMOUNT_HOLDER, amount + "");
		} else {
			item = new ItemBuilder(Material.WOOL).setDamage(14).build();
			Utils.updateDescription(item,GuiConfig.Generic.AMOUNT_SELECTOR_REMOVE, null, true,
					GuiConfig.AMOUNT_HOLDER, -amount + "");
		}
		return item;
	}
}
