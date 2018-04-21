package emanondev.quests.reward;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public interface Reward extends MissionReward,QuestReward {
	
	public void applyReward(QuestPlayer p);
	
	public default void applyReward(QuestPlayer p,Mission m) {
		applyReward(p);
	}
	public default void applyReward(QuestPlayer p,Quest q) {
		applyReward(p);
	}

}
