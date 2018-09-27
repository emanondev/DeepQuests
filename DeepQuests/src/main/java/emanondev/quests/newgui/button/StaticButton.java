package emanondev.quests.newgui.button;

import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.gui.Gui;

public abstract class StaticButton extends AButton {
	ItemStack item;

	public StaticButton(ItemStack item,Gui parent) {
		super(parent);
		this.item = item;
	}

	@Override
	public ItemStack getItem() {
		return item;
	}

	@Override
	public boolean update() {
		return false;
	}

}