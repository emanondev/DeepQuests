package emanondev.quests.reward;

import emanondev.quests.utils.AbstractApplyableType;
import emanondev.quests.utils.YmlLoadable;

public abstract class AbstractRewardType extends AbstractApplyableType<YmlLoadable> implements RewardType {
	public AbstractRewardType(String key) {
		super(key);
	}
}
