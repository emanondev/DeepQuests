package emanondev.quests.newgui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.gui.Gui;

public interface Button {
	public ItemStack getItem();
	public boolean update();
	public void onClick(Player clicker,ClickType click);
	public default Player getTargetPlayer() {
		if (getParent()!=null)
			return getParent().getTargetPlayer();
		return null;
	}
	public Gui getParent();
}