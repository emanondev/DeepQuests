package emanondev.quests.require;

import emanondev.quests.utils.AbstractApplyableType;
import emanondev.quests.utils.QCWithCooldown;

public abstract class AbstractRequireType extends AbstractApplyableType<QCWithCooldown> implements RequireType {
	public AbstractRequireType(String key) {
		super(key);
	}
}
