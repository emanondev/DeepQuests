package emanondev.quests.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.HandlerList;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.MissionReward;

public class PlayerCompleteMissionEvent extends QuestPlayerEvent{
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	private final Mission mission;
	private final List<MissionReward> rewards = new ArrayList<MissionReward>();

	public PlayerCompleteMissionEvent(QuestPlayer questPlayer, Mission m) {
		super(questPlayer);
		this.mission = m;
		rewards.addAll(m.getCompleteRewards());
	}
	public Mission getMission() {
		return mission;
	}
	public List<MissionReward> getRewards() {
		return rewards;
	}
	public void setRewards(List<MissionReward> rewardsList) {
		rewards.clear();
		if (rewardsList!=null)
			rewards.addAll(rewardsList);
	}

}