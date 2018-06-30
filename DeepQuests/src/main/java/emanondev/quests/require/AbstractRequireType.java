package emanondev.quests.require;

import emanondev.quests.utils.AbstractApplyableType;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public abstract class AbstractRequireType extends AbstractApplyableType<YmlLoadableWithCooldown> implements RequireType {
	public AbstractRequireType(String key) {
		super(key);
	}
}
