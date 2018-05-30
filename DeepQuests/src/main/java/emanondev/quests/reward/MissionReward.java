package emanondev.quests.reward;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.WithGui;

/**
 * Any implementations of this class must provide a costructor(String text);
 * 
 * @author utente
 *
 */
public interface MissionReward {
	public void applyReward(QuestPlayer p,Mission q);

	public String getDescription();
	public MissionRewardType getRewardType();
	public WithGui getParent();

}
