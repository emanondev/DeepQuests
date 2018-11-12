package emanondev.quests.interfaces;

import java.util.Map;

public abstract class AQuestComponentWithCooldown<T extends User<T>> extends AQuestComponentWithWorlds<T>
				implements QuestComponentWithCooldown<T> {

	public AQuestComponentWithCooldown(Map<String, Object> map) {
		super(map);
		cooldownMinutes = (long) Math.max(0L,(long) map.getOrDefault(Paths.COOLDOWN_MINUTES,1440L));
		repeatable = (boolean) map.getOrDefault(Paths.REPEATABLE,false);
	}

	private long cooldownMinutes;
	private boolean repeatable;
	@Override
	public long getCooldownMinutes() {
		return cooldownMinutes;
	}

	@Override
	public boolean isRepeatable() {
		return repeatable;
	}

	@Override
	public boolean setCooldownMinutes(long cooldown) {
		cooldownMinutes = Math.max(0L,cooldown);
		return true;
	}

	@Override
	public boolean setRepeatable(boolean value) {
		repeatable = value;
		return true;
	}

}
