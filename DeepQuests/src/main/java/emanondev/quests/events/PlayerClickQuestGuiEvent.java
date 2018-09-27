package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.inventory.QuestButton;
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
	private final emanondev.quests.newgui.button.QuestButton questButton2;
	public PlayerClickQuestGuiEvent(Player clicker, ClickType click, QuestButton questButton) {
		super(clicker,click);
		if (questButton==null)
			throw new NullPointerException();
		this.questButton = questButton;
		this.questButton2 = null;
	}
	public PlayerClickQuestGuiEvent(Player clicker, ClickType click,
			emanondev.quests.newgui.button.QuestButton questButton2) {
		super(clicker,click);
		if (questButton2==null)
			throw new NullPointerException();
		this.questButton = null;
		this.questButton2 = questButton2;
	}
	public Quest getQuest() {
		if (questButton!=null)
			return questButton.getQuest();
		return questButton2.getQuestComponent();
	}
	public QuestPlayer getQuestPlayer() {
		if (questButton!=null)
			return questButton.getParent().getQuestPlayer();
		return questButton2.getParent().getQuestPlayer();
	}

}
