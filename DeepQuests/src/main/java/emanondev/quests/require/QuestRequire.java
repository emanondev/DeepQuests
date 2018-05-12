package emanondev.quests.require;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public interface QuestRequire {
	public boolean isAllowed(QuestPlayer p,Quest q);
	public String toText();
}
