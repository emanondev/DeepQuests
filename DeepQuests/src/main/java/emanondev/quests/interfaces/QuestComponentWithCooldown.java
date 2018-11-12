package emanondev.quests.interfaces;

import emanondev.quests.utils.StringUtils;

public interface QuestComponentWithCooldown<T extends User<T>> extends QuestComponentWithWorlds<T> {

	public long getCooldownMinutes();
	public default long getCooldownTime() {
		return getCooldownMinutes()*60*1000;
	}

	public long getCooldownLeft(T user);
	public default String getStringCooldownLeft(T user) {
		return StringUtils.getStringCooldown(getCooldownLeft(user));
	}
	

	public boolean isRepeatable();
	public boolean setCooldownMinutes(long cooldown);

	public boolean setRepeatable(boolean value);

}
