package emanondev.quests.newgui.gui;

import org.bukkit.entity.Player;
import emanondev.quests.Language;
import emanondev.quests.Quests;
import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.button.QuestButton;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.DisplayState;

public class QuestsMenu extends SortedListGui<QuestButton> {
	private final QuestManager questManager;

	public QuestsMenu(Player player, Gui previusHolder,QuestManager questManager) {
		super(Language.Gui.getQuestsMenuTitle(player, 1), 6, player, previusHolder, 1);
		if (player == null || questManager == null)
			throw new NullPointerException();
		this.questManager = questManager;
		this.setControlButton(0, new QuestStateShowToggler(DisplayState.LOCKED));
		this.setControlButton(1, new QuestStateShowToggler(DisplayState.UNSTARTED));
		this.setControlButton(2, new QuestStateShowToggler(DisplayState.ONPROGRESS));
		this.setControlButton(3, new QuestStateShowToggler(DisplayState.COMPLETED));
		this.setControlButton(4, new QuestStateShowToggler(DisplayState.COOLDOWN));
		this.setControlButton(5, new QuestStateShowToggler(DisplayState.FAILED));
		reloadComponents();
		
		updateInventory();
	}
	
	public QuestPlayer getQuestPlayer() {
		return Quests.get().getPlayerManager().getQuestPlayer(getTargetPlayer());
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
	
	private class QuestStateShowToggler extends StaticFlagButton {
		private final DisplayState state;
		public QuestStateShowToggler(DisplayState state) {
			super(GuiConfig.PlayerQuests.getQuestDisplayFlagItem(state,false),
					GuiConfig.PlayerQuests.getQuestDisplayFlagItem(state,true),
					QuestsMenu.this);
			if (state==null)
				throw new NullPointerException();
			this.state = state;
		}

		@Override
		public boolean getCurrentValue() {
			return getQuestPlayer().canSeeQuestState(state);
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			if (getQuestPlayer().canSeeQuestState(state)==value)
				return false;
			getQuestPlayer().toggleCanSeeQuestState(state);
			reloadComponents();
			updateInventory();
			return true;
		}
		
	}
	
	private void reloadComponents() {
		clearButtons();
		for (Quest quest:questManager.getQuests()) {
			if (getQuestPlayer().canSee(quest))
				this.addButton(new QuestButton(this,quest) );
		}
	}
	

}
