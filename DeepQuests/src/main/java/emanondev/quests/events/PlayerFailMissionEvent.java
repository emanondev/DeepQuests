package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;

public class PlayerFailMissionEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final QuestPlayer questPlayer;
	private final Mission mission;
	public PlayerFailMissionEvent(QuestPlayer questPlayer, Mission m) {
		this.questPlayer = questPlayer;
		this.mission = m;
	}
	public Player getPlayer() {
		return questPlayer.getPlayer();
	}
	public QuestPlayer getQuestPlayer() {
		return questPlayer;
	}
	public Mission getMission() {
		return mission;
	}
}
