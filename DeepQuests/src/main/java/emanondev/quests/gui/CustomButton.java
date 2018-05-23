package emanondev.quests.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class CustomButton {
	private CustomGui parent;
	public CustomButton(CustomGui parent) {
		if (parent==null)
			throw new NullPointerException();
		this.parent = parent;
	}
	public abstract ItemStack getItem();
	public void update() {}
	
	public abstract void onClick(Player clicker,ClickType click);
	public Player getOwner() {
		return parent.getPlayer();
	}
	public CustomGui getParent() {
		return parent;
	}

}
