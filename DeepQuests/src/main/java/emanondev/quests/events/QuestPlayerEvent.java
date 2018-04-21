package emanondev.quests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import emanondev.quests.player.QuestPlayer;

public abstract class QuestPlayerEvent extends Event {
	private final QuestPlayer questPlayer;
	
	public QuestPlayerEvent(QuestPlayer qPlayer) {
		this.questPlayer = qPlayer;
	}
	
	public Player getPlayer() {
		return questPlayer.getPlayer();
	}
	public QuestPlayer getQuestPlayer() {
		return questPlayer;
	}
}
