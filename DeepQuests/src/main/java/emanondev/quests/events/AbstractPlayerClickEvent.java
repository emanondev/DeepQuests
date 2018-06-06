package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;


abstract class AbstractPlayerClickEvent extends Event  implements Cancellable {
	private final ClickType clickType;
	private boolean cancelled = false;
	private final Player clicker;
	public AbstractPlayerClickEvent(Player who,ClickType click) {
		if (who == null || click == null)
			throw new NullPointerException();
		this.clicker = who;
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
	public Player getClicker() {
		return clicker;
	}
}
