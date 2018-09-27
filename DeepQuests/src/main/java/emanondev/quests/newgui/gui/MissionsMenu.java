package emanondev.quests.newgui.gui;

import org.bukkit.entity.Player;

import emanondev.quests.Language;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.button.MissionButton;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.DisplayState;

public class MissionsMenu extends SortedListGui<MissionButton> {
	private final Quest quest;

	public MissionsMenu(Player player, Gui previusHolder,Quest quest) {
		super(Language.Gui.getMissionsMenuTitle(player, quest, 1), 6, player, previusHolder);
		if (player == null || quest == null)
			throw new NullPointerException();
		this.quest = quest;
		this.setControlButton(0, new MissionStateShowToggler(DisplayState.LOCKED));
		this.setControlButton(1, new MissionStateShowToggler(DisplayState.UNSTARTED));
		this.setControlButton(2, new MissionStateShowToggler(DisplayState.ONPROGRESS));
		this.setControlButton(3, new MissionStateShowToggler(DisplayState.COMPLETED));
		this.setControlButton(4, new MissionStateShowToggler(DisplayState.COOLDOWN));
		this.setControlButton(5, new MissionStateShowToggler(DisplayState.FAILED));
		reloadComponents();
		
		updateInventory();
	}

	@Override
	protected int loadPreviusPageButtonPosition() {
		return 6;
	}
	@Override
	protected int loadNextPageButtonPosition() {
		return 7;
	}
	@Override
	protected int loadBackButtonPosition() {
		return 8;
	}
	
	public QuestPlayer getQuestPlayer() {
		return Quests.get().getPlayerManager().getQuestPlayer(getTargetPlayer());
	}

	private class MissionStateShowToggler extends StaticFlagButton {
		private final DisplayState state;
		public MissionStateShowToggler(DisplayState state) {
			super(GuiConfig.PlayerQuests.getMissionDisplayFlagItem(state,false),
					GuiConfig.PlayerQuests.getMissionDisplayFlagItem(state,true),
					MissionsMenu.this);
			if (state==null)
				throw new NullPointerException();
			this.state = state;
		}

		@Override
		public boolean getCurrentValue() {
			return getQuestPlayer().canSeeMissionState(state);
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			if (getQuestPlayer().canSeeMissionState(state)==value)
				return false;
			getQuestPlayer().toggleCanSeeMissionState(state);
			reloadComponents();
			updateInventory();
			return true;
		}
		
	}
	private void reloadComponents() {
		clearButtons();
		for (Mission mission:quest.getMissions()) {
			if (getQuestPlayer().canSee(mission))
				this.addButton(new MissionButton(this,mission) );
		}
	}

}
