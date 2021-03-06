package emanondev.quests.events;

import org.bukkit.event.HandlerList;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;

public class PlayerFailMissionEvent extends QuestPlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Mission mission;
	public PlayerFailMissionEvent(QuestPlayer questPlayer, Mission m) {
		super(questPlayer);
		this.mission = m;
		Quests.getLogger("debug").log("PlayerFailMissionEvent "+questPlayer.getPlayer().getName()
				+" "+m.getDisplayName());
	}
	public Mission getMission() {
		return mission;
	}
	
}
