package emanondev.quests.newgui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.gui.Gui;

public abstract class StaticFlagButton extends AButton {
	private ItemStack falseItem;
	private ItemStack trueItem;

	public StaticFlagButton(ItemStack falseItem,ItemStack trueItem, Gui parent) {
		super(parent);
		this.falseItem = falseItem;
		this.trueItem = trueItem;
	}

	public abstract boolean getCurrentValue();

	public abstract boolean onValueChangeRequest(boolean value);

	@Override
	public ItemStack getItem() {
		if (getCurrentValue())
			return trueItem;
		return falseItem;
	}
	@Override
	public boolean update(){
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		if (onValueChangeRequest(!getCurrentValue()))
			getParent().updateInventory();
	}

}