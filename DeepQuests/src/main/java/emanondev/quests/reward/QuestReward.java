package emanondev.quests.reward;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.WithGui;

/**
 * Any implementations of this class must provide a costructor(String text);
 * 
 * @author utente
 *
 */
public interface QuestReward {
	public void applyReward(QuestPlayer p,Quest q);

	public String getDescription();
	public QuestRewardType getRewardType();
	public WithGui getParent();
	public String getNameID();
	public default String getKey() {
		return getRewardType().getKey();
	}

}
