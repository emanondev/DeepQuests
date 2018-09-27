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
	private final emanondev.quests.newgui.button.MissionButton missionButton2;
	public PlayerClickMissionGuiEvent(Player clicker, ClickType click, MissionButton missionButton) {
		super(clicker,click);
		if (missionButton == null)
			throw new NullPointerException();
		this.missionButton = missionButton;
		this.missionButton2 = null;
	}

	public PlayerClickMissionGuiEvent(Player clicker, ClickType click,
			emanondev.quests.newgui.button.MissionButton missionButton2) {
		super(clicker,click);
		if (missionButton2 == null)
			throw new NullPointerException();
		this.missionButton2 = missionButton2;
		this.missionButton = null;
	}
	public Mission getMission() {
		if (missionButton!=null)
			return missionButton.getMission();
		return missionButton2.getQuestComponent();
	}
	public QuestPlayer getQuestPlayer() {
		if (missionButton!=null)
			return missionButton.getParent().getQuestPlayer();
		return missionButton2.getParent().getQuestPlayer();
	}
}
