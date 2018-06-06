package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.inventory.MissionButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;

public class PlayerClickMissionGuiEvent extends AbstractPlayerClickEvent {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final MissionButton missionButton;
	public PlayerClickMissionGuiEvent(Player clicker, ClickType click, MissionButton missionButton) {
		super(clicker,click);
		if (missionButton == null)
			throw new NullPointerException();
		this.missionButton = missionButton;
	}

	public Mission getMission() {
		return missionButton.getMission();
	}
	public QuestPlayer getQuestPlayer() {
		return missionButton.getParent().getQuestPlayer();
	}
}
