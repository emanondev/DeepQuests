package emanondev.quests.reward;

import emanondev.quests.utils.AbstractApplyableType;
import emanondev.quests.utils.QuestComponent;

public abstract class AbstractRewardType extends AbstractApplyableType<QuestComponent> implements RewardType {
	public AbstractRewardType(String key) {
		super(key);
	}
}
