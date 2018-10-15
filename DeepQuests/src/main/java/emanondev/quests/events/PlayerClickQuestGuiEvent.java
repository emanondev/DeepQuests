package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.newgui.button.QuestButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public class PlayerClickQuestGuiEvent extends AbstractPlayerClickEvent {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final QuestButton questButton;
	
	public PlayerClickQuestGuiEvent(Player clicker, ClickType click,
			QuestButton questButton) {
		super(clicker,click);
		if (questButton==null)
			throw new NullPointerException();
		this.questButton = questButton;
	}
	public Quest getQuest() {
		return questButton.getQuestComponent();
	}
	public QuestPlayer getQuestPlayer() {
		return questButton.getParent().getQuestPlayer();
	}

}
