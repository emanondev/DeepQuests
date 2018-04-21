package emanondev.quests.reward;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;

public interface MissionReward {
	public void applyReward(QuestPlayer p,Mission q);
}
