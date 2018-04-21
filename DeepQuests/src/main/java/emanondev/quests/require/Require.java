package emanondev.quests.require;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public interface Require extends QuestRequire,MissionRequire {
	public default boolean isAllowed(QuestPlayer p,Quest q) {
		return isAllowed(p);
	}
	public default boolean isAllowed(QuestPlayer p,Mission m) {
		return isAllowed(p);
	}

	public boolean isAllowed(QuestPlayer p);
}
