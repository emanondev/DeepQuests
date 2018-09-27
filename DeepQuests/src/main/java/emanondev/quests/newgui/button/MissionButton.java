package emanondev.quests.newgui.button;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.events.PlayerClickMissionGuiEvent;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.MissionsMenu;
import emanondev.quests.utils.DisplayState;

public class MissionButton extends QCButton<Mission> {

	public MissionButton(MissionsMenu missionsMenu, Mission mission) {
		super(missionsMenu,mission);
	}

	public MissionsMenu getParent() {
		return (MissionsMenu) super.getParent();
	}

	@Override
	public ItemStack getItem() {
		return getParent().getQuestPlayer().getGuiItem(getQuestComponent());
	}

	@Override
	public boolean update() {
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		PlayerClickMissionGuiEvent event = new PlayerClickMissionGuiEvent(clicker, click, this);
		DisplayState state = getParent().getQuestPlayer().getDisplayState(getQuestComponent());
		switch (state) {
		case LOCKED:
			event.setCancelled(true);
		default:
			break;
		}
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		if (clicker.equals(getParent().getTargetPlayer())) {
			switch (click) {
			case LEFT:
				switch (state) {
				case UNSTARTED:
					getParent().getQuestPlayer().startMission(getQuestComponent());
					update();
					getParent().updateInventory();
					return;
				case ONPROGRESS:
					if (getQuestComponent().mayBePaused()) {
						getParent().getQuestPlayer().togglePauseMission(getQuestComponent());
						update();
						getParent().updateInventory();
						return;
					}
				default:
					break;
				}
				return;
			case RIGHT:
				// TODO tasks gui
				return;
			default:
				break;
			}
		}

	}

}
