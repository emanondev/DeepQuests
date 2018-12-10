package emanondev.quests.interfaces;

import java.util.Collection;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * 
 * @author emanon<br>
 * 
 * remember to add <br>
 * 
 * .@SerializableAs("PlayerTask")<br>
 * .@DelegateDeserialization(PlayerTaskManager.class)<br>
 * for playertasks
 *
 * @param <T> - User Type
 * 
 */
public interface Task<T extends User<T>> extends QuestComponentWithWorlds<T> {
	
	@Override
	public Mission<T> getParent();
	
	/**
	 * @return ordered list of rewards
	 */
	public Collection<Reward<T>> getRewards();
	/**
	 * 
	 * @param key - key of the reward
	 * @return the reward with key or null
	 */
	public Reward<T> getReward(String key);
	/**
	 * @throw IlleagalArgumentException if reward.getParent() != null
	 * @throw IlleagalArgumentException if getReward(reward.getKey()) != null
	 * 
	 * @param reward - the reward to add
	 * @return true if sucessfully added
	 */
	public boolean addReward(Reward<T> reward);
	/**
	 * 
	 * @param key - the key of the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeReward(String key);
	
	/**
	 * ProgressChance is the probability to Obtain a progress
	 * <br>on the task doing the action, by default 1 = 100%
	 * <br>rewards are given only when progress is sucessfull
	 * 
	 * @return progressChance of this
	 */
	public double getProgressChance();
	/**
	 * Allowed values are ]0;1]
	 * 
	 * @param progressChance
	 * @return true if succesfully updated progressChance
	 */
	public boolean setProgressChance(double progressChance);
	
	/**
	 * When the max progress is reached the task is completed
	 * 
	 * @return max allowed progress for this task
	 */
	public int getMaxProgress();
	
	/**
	 * Allowed values are [1;Integer.MAX_VALUE]
	 * 
	 * @param maxProgress - new value for the max progress
	 * @return if the maxProgress was succesfully set
	 */
	public boolean setMaxProgress(int maxProgress);
	
	/**
	 * 
	 * @return the TaskType of this
	 */
	public TaskType<T> getType();

	/**
	 * @return BossBar style of this
	 */
	public BarStyle getBossBarStyle();

	/**
	 * @return BossBar color of this
	 */
	public BarColor getBossBarColor();
	/**
	 * set BossBar style of this
	 */
	public boolean setBossBarStyle(BarStyle barStyle);

	/**
	 * set BossBar color of this
	 */
	public boolean setBossBarColor(BarColor barColor);
	
	/**
	 * @return should BossBar be shown for this task progress?
	 */
	public boolean showBossBar();
	
	/**
	 * sets if BossBar should be used
	 */
	public void setShowBossBar(Boolean value);
	/**
	 * 
	 * @return description of this when not started by user
	 */
	public String getRawUnstartedDescription();
	/**
	 * 
	 * @return description of this when started by user
	 */
	public String getRawProgressDescription();
	
	/**
	 * called when the task has a progress for user
	 * 
	 * @param user
	 * @return the final progress amount
	 */
	public default int onProgress(T user) {
		return onProgress(user,1);
	}
	/**
	 * called when the task has a progress of amount for user
	 * 
	 * @param user
	 * @return the final progress amount
	 */
	public default int onProgress(T user,int amount) {
		if (getProgressChance()>=1)
			return user.getData().progressTask(this, amount);
		int counter = 0;
		for (int i = 0; i < amount; i++) {
			if (Math.random()<=getProgressChance())
				counter++;
		}
		if (counter>0)
			return user.getData().progressTask(this, counter);
		return 0;
	}
	
	/**
	 * 
	 * @return taskType ID of this
	 */
	public String getTypeName();
	
	/**
	 * 
	 * @param user
	 * @return getRawUnstartedDescription() replacing holders
	 */
	public default String getUnstartedDescription(T user) {
		return getRawUnstartedDescription().replace(Holders.DISPLAY_NAME,getDisplayName())
				.replace(Holders.TASK_MAX_PROGRESS,String.valueOf(getMaxProgress()))
				.replace(Holders.TASK_CURRENT_PROGRESS,String.valueOf(user.getData().getTaskData(this).getProgress()));
	}
	
	public boolean setUnstartedDescription(String desc);
	
	/**
	 * 
	 * @param user
	 * @return getRawProgressDescription() replacing holders
	 */
	public default String getProgressDescription(T user) {
		return getRawProgressDescription().replace(Holders.DISPLAY_NAME,getDisplayName())
				.replace(Holders.TASK_MAX_PROGRESS,String.valueOf(getMaxProgress()))
				.replace(Holders.TASK_CURRENT_PROGRESS,String.valueOf(user.getData().getTaskData(this).getProgress()));
	}
	
	public boolean setProgressDescription(String desc);
	

}
