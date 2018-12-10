package emanondev.quests.interfaces;

import java.util.Collection;


public interface Mission<T extends User<T>> extends QuestComponentWithCooldown<T>{
	

	/**
	 * Get mission task with key equals to task.getKey() if exist
	 * 
	 * @param key - id of the task
	 * @return task or null if can't find
	 */
	public Task<T> getTask(String key);
	
	/**
	 * Get all registered Task for this
	 * 
	 * @return not null immutable collection of tasks
	 */
	public Collection<Task<T>> getTasks();
	

	/**
	 * Register Task for this
	 * 
	 * @throws IllegalArgumentException if task.getParent() != null
	 * @throws IllegalArgumentException if getTask(task.getKey()) != null
	 * 
	 * @param task - the task to add
	 * @return true if sucessfully added
	 */
	public boolean addTask(Task<T> task);
	
	/**
	 * Unregister Task with key equals task.getKey() for this if exist
	 * 
	 * @param key - the key of the task to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeTask(String key);
	/**
	 * Get the quest which contains this
	 * 
	 * @return parent quest or null
	 */
	@Override
	public Quest<T> getParent();

	/**
	 * Get all registered Require for this
	 * 
	 * @return not null immutable collection of requires
	 */
	public Collection<Require<T>> getRequires();
	
	/**
	 * Get mission require with key equals to require.getKey() if exist
	 * 
	 * @param key - id of the require
	 * @return require or null if can't find
	 */
	public Require<T> getRequire(String key);
	
	/**
	 * Register require for this
	 * 
	 * @throws IllegalArgumentException if require.getParent() != null
	 * @throws IllegalArgumentException if getRequire(require.getKey()) != null
	 * 
	 * @param require - the require to add
	 * @return true if sucessfully added
	 */
	public boolean addRequire(Require<T> require);
	
	/**
	 * Unregister require with key equals require.getKey() for this if exist
	 * 
	 * @param key - the key of the require to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeRequire(String key);
	

	/**
	 * Get all registered Start Rewards for this
	 * 
	 * @return not null immutable collection of rewards
	 */
	public Collection<Reward<T>> getStartRewards();

	/**
	 * Get start reward with key equals to reward.getKey() if exist
	 * 
	 * @param key - id of the reward
	 * @return reward or null if can't find
	 */
	public Reward<T> getStartReward(String key);
	

	/**
	 * Register a start reward for this
	 * 
	 * @throws IllegalArgumentException if reward.getParent() != null
	 * @throws IllegalArgumentException if getStartReward(require.getKey()) != null
	 * 
	 * @param reward - the require to add
	 * @return true if sucessfully added
	 */
	public boolean addStartReward(Reward<T> reward);

	/**
	 * Unregister getStartReward(key) if exist
	 * 
	 * @param key - the key of the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeStartReward(String key);
	

	/**
	 * Get all registered Complete Rewards for this
	 * 
	 * @return not null immutable collection of rewards
	 */
	public Collection<Reward<T>> getCompleteRewards();

	/**
	 * Get complete reward with key equals to reward.getKey() if exist
	 * 
	 * @param key - id of the reward
	 * @return reward or null if can't find
	 */
	public Reward<T> getCompleteReward(String key);

	/**
	 * Register a complete reward for this
	 * 
	 * @throws IllegalArgumentException if reward.getParent() != null
	 * @throws IllegalArgumentException if getCompleteReward(require.getKey()) != null
	 * 
	 * @param reward - the require to add
	 * @return true if sucessfully added
	 */
	public boolean addCompleteReward(Reward<T> reward);

	/**
	 * Unregister getCompleteReward(key) if exist
	 * 
	 * @param key - the key of the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeCompleteReward(String key);
	

	/**
	 * Get all registered Fail Rewards for this
	 * 
	 * @return not null immutable collection of rewards
	 */
	public Collection<Reward<T>> getFailRewards();

	/**
	 * Get fail reward with key equals to reward.getKey() if exist
	 * 
	 * @param key - id of the reward
	 * @return reward or null if can't find
	 */
	public Reward<T> getFailReward(String key);

	/**
	 * Register a fail reward for this
	 * 
	 * @throws IllegalArgumentException if reward.getParent() != null
	 * @throws IllegalArgumentException if getFailReward(require.getKey()) != null
	 * 
	 * @param reward - the require to add
	 * @return true if sucessfully added
	 */
	public boolean addFailReward(Reward<T> reward);

	/**
	 * Unregister getFailReward(key) if exist
	 * 
	 * @param key - the key of the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeFailReward(String key);
	
	/*
	public List<String> getStartMessage();

	public List<String> getCompleteMessage();

	public List<String> getFailMessage();

	
	public boolean setStartMessage();

	public boolean setCompleteMessage();

	public boolean setFailMessage();
	

	public boolean showStartMessage();

	public boolean showCompleteMessage();

	public boolean showFailMessage();*/

}
