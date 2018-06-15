package emanondev.quests.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.events.PlayerClickMissionGuiEvent;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.utils.DisplayState;

public class MissionButton extends CustomButton {
	private final Mission mission;
	
	public MissionButton(PlayerMissionsGui parent,Mission mission) {
		super(parent);
		if (mission==null)
			throw new NullPointerException();
		this.mission = mission;
	}
	public Mission getMission() {
		return mission;
	}
	@Override
	public ItemStack getItem() {
		return getParent().getQuestPlayer().getGuiItem(mission);
	}
	public PlayerMissionsGui getParent() {
		return (PlayerMissionsGui) super.getParent();
	}
	@Override
	public void onClick(Player clicker, ClickType click) {
		PlayerClickMissionGuiEvent event = new PlayerClickMissionGuiEvent(clicker,click,this);
		DisplayState state = getParent().getQuestPlayer().getDisplayState(mission);
		switch(state) {
		case LOCKED:
			event.setCancelled(true);
		default:
			break;
		}
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		if(clicker.equals(getParent().getPlayer())) {
			switch (click) {
			case LEFT:
				switch (state) {
				case UNSTARTED:
					getParent().getQuestPlayer().startMission(mission);
					update();
					getParent().reloadInventory();
					return;
				case ONPROGRESS:
					if (mission.mayBePaused()) {
						getParent().getQuestPlayer().togglePauseMission(mission);
						update();
						getParent().reloadInventory();
						return;
					}
				default:
					break;
				}
				return;
			case RIGHT:
				//TODO tasks gui
				return;
			default:
				break;
			}
		}
		
	}
	
}