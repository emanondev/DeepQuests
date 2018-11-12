package emanondev.quests.newgui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.newgui.gui.Gui;

public abstract class AButton implements Button {
	private Gui parent;
	
	public AButton(Gui parent) {
		this.parent = parent;
	}
	
	public abstract void onClick(Player clicker,ClickType click);
	
	public Player getTargetPlayer() {
		return parent.getTargetPlayer();
	}
	
	public Gui getParent() {
		return parent;
	}
}