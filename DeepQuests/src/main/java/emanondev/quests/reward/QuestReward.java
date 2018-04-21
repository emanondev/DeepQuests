package emanondev.quests.reward;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public interface QuestReward {
	public void applyReward(QuestPlayer p,Quest q);
}
