package emanondev.quests.events;

import org.bukkit.event.HandlerList;

import emanondev.quests.Quests;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.Task;

public class PlayerCompleteTaskEvent extends QuestPlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Task task;
	
	public PlayerCompleteTaskEvent(QuestPlayer questPlayer, Task t) {
		super(questPlayer);
		this.task = t;
		Quests.getLogger("debug").log("PlayerCompleteTaskEvent "+questPlayer.getPlayer().getName()
				+" "+task.getDisplayName());
	}
	public Task getTask() {
		return task;
	}
}
