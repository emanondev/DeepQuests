package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.inventory.GuiManager.MissionsGuiHolder;
import emanondev.quests.mission.Mission;

public class PlayerClickGuiMissionEvent extends PlayerClickGuiEvent {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Mission mission;

	public PlayerClickGuiMissionEvent(Player who,ClickType click,MissionsGuiHolder holder,
			Mission clickedMission) {
		super(who,click,holder);
		this.mission = clickedMission;
	}
	public Mission getMission() {
		return mission;
	}
	public MissionsGuiHolder getGuiHolder() {
		return (MissionsGuiHolder) super.getGuiHolder();
	}
}
