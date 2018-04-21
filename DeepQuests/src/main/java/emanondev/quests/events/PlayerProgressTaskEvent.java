package emanondev.quests.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.Reward;
import emanondev.quests.task.Task;

public class PlayerProgressTaskEvent extends QuestPlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private boolean cancelled = false;
	private final Task task;
	private final List<Reward> rewards = new ArrayList<Reward>();
	private int progressAmount;
	
	public PlayerProgressTaskEvent(QuestPlayer questPlayer, Task t, int amount) {
		super(questPlayer);
		this.task = t;
		this.progressAmount = amount;//TODO rewards
	}
	public int getProgressAmount() {
		return progressAmount;
	}
	public void setProgressAmount(int amount) {
		progressAmount = Math.max(0,amount);
	}
	public Task getTask() {
		return task;
	}
	public List<Reward> getRewards() {
		return rewards;
	}
	public void setRewards(List<Reward> rewardsList) {
		rewards.clear();
		if (rewardsList!=null)
			rewards.addAll(rewardsList);
	}
	public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
