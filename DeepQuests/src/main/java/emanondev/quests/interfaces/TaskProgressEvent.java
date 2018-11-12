package emanondev.quests.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TaskProgressEvent<T extends User<T>> extends UserEvent<T> implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Task<T> task;
	private final Map<Reward<T>,Integer> rewards = new HashMap<>();
	private int progress;
	private final int limit;

	public TaskProgressEvent(T user, Task<T> task, int progress, int limit) {
		super(user);
		this.task = task;
		for (Reward<T> reward:task.getRewards())
			rewards.put(reward,progress);
		this.progress = progress;
		this.limit = limit;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int amount) {
		progress = Math.min(Math.max(0,amount),limit);
	}
	public Task<T> getTask(){
		return task;
	}
	public Map<Reward<T>, Integer> getRewards() {
		return rewards;
	}
	private boolean cancelled = false;
	public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
