package emanondev.quests.interfaces;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.Listener;

public interface TaskType<T extends User<T>> extends QuestComponentType<T,Task<T>>,Listener {

	/**
	 * @return BossBar style of this
	 */
	public BarStyle getDefaultBossBarStyle();

	/**
	 * @return BossBar color of this
	 */
	public BarColor getDefaultBossBarColor();

	public String getDefaultUnstartedDescription();

	public String getDefaultProgressDescription();
	
	public boolean getDefaultShowBossBar();
	

}
