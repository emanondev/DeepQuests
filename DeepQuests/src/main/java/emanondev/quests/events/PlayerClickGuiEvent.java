package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerEvent;

import emanondev.quests.inventory.GuiManager.GuiHolder;

public abstract class PlayerClickGuiEvent extends PlayerEvent implements Cancellable {
	private final ClickType clickType;
	private boolean cancelled = false;
	private GuiHolder holder;
	public PlayerClickGuiEvent(Player who,ClickType click,GuiHolder holder) {
		super(who);
		this.holder = holder;
		this.clickType = click;
	}
	public ClickType getClick() {
		return clickType;
	}
	public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
	public GuiHolder getGuiHolder() {
		return holder;
	}
}
