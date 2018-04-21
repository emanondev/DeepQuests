package emanondev.quests.require;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;

public interface MissionRequire {
	public boolean isAllowed(QuestPlayer p,Mission m);
}
