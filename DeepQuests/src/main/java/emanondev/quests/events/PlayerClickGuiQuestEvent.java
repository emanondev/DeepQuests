package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.inventory.GuiManager.QuestsGuiHolder;
import emanondev.quests.quest.Quest;

public class PlayerClickGuiQuestEvent extends PlayerClickGuiEvent {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Quest quest;
	
	public PlayerClickGuiQuestEvent(Player who,ClickType click,QuestsGuiHolder holder,
			Quest clickedQuest) {
		super(who,click,holder);
		this.quest = clickedQuest;
	}
	public Quest getQuest() {
		return quest;
	}
	public QuestsGuiHolder getGuiHolder() {
		return (QuestsGuiHolder) super.getGuiHolder();
	}
}
