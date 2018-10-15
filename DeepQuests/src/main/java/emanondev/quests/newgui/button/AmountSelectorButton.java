package emanondev.quests.newgui.button;

import java.util.ArrayList;
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
	private ArrayList<Long> values = new ArrayList<Long>();

	public AmountSelectorButton(String subGuiTitle, ItemStack item, Gui parent) {
		this(item,subGuiTitle,parent,1L,10L,100L,1000L,10000L,100000L,1000000L);
	}
	
	public AmountSelectorButton(String subGuiTitle, ItemStack item, Gui parent,long one,long two,long three) {
		this(item,subGuiTitle,parent,one,two,three);
	}
	public AmountSelectorButton(String subGuiTitle, ItemStack item, Gui parent,long one,long two,long three,long four,long five,long six,long seven) {
		this(item,subGuiTitle,parent,one,two,three,four,five,six,seven);
	}
	private AmountSelectorButton(ItemStack item,String subGuiTitle,  Gui parent,long... values) {
		super(parent);
		this.item = item;
		this.subGuiTitle = subGuiTitle;
		for (long val:values)
			this.values.add(val);
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
			this.putButton(53, new BackButton(this));
			if (values.size()==3) {
				for (int i = 0; i < values.size(); i++) {
					this.putButton(19+i*3, new EditAmountButton(values.get(i)));
					this.putButton(28+i*3, new EditAmountButton(-values.get(i)));
				}
			}
			else if (values.size()==7) {
				for (int i = 0; i < values.size(); i++) {
					this.putButton(19+i, new EditAmountButton(values.get(i)));
					this.putButton(28+i, new EditAmountButton(-values.get(i)));
				}
			}
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
