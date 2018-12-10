package emanondev.quests.interfaces;

import java.util.Collection;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public interface BossBarManager<T extends User<T>> {
	
	/**
	 * Clear all the current BossBar stored and reload his config
	 */
	public void reload();	

	/**
	 * Update BossBar of task for user
	 * 
	 * @param user - user
	 * @param task - task
	 */
	public void onProgress(T user,Task<T> task);
	
	/**
	 * Get default or stored color for taskType
	 * 
	 * @param taskType - TaskType
	 * @return not null BarColor
	 */
	public BarColor getBarColor(TaskType<T> taskType);
	/**
	 * Get default or stored style for taskType
	 * 
	 * @param taskType - TaskType
	 * @return not null BarStyle
	 */
	public BarStyle getBarStyle(TaskType<T> taskType);
	/**
	 * Get default or stored option for showing BossBar for taskType
	 * 
	 * @param taskType - TaskType
	 * @return boolean (default true)
	 */
	public boolean getShowBossBar(TaskType<T> taskType);

	/**
	 * Get collection of player related to user for task BossBar updates
	 * 
	 * @param user - user
	 * @param task - task
	 * @return not null collection (might be empty)
	 */
	public Collection<Player> getPlayers(T user,Task<T> task);


}
