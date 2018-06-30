package emanondev.quests.reward;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.YmlLoadable;

public interface Reward extends Applyable<YmlLoadable> {
	/**
	 * 
	 * @return the Type
	 */
	public RewardType getType();

	public default void applyReward(QuestPlayer qPlayer) {
		applyReward(qPlayer,1);
	}
	public void applyReward(QuestPlayer qPlayer,int amount);
}
