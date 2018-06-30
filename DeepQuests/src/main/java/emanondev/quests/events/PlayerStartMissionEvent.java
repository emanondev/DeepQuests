package emanondev.quests.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.Reward;

public class PlayerStartMissionEvent extends QuestPlayerEvent implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private boolean cancelled = false;

	private final Mission mission;
	private final List<Reward> rewards = new ArrayList<Reward>();

	public PlayerStartMissionEvent(QuestPlayer questPlayer, Mission m) {
		super(questPlayer);
		this.mission = m;
		rewards.addAll(m.getStartRewards());
		Quests.getLogger("debug").log("PlayerStartMissionEvent "+questPlayer.getPlayer().getName()
				+" "+m.getDisplayName());
	}
	
	public Mission getMission() {
		return mission;
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
