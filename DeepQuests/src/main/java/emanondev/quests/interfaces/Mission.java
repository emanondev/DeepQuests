package emanondev.quests.interfaces;

import java.util.Collection;

public interface Mission<T extends User<T>> extends QuestComponentWithCooldown<T>{
	

	/**
	 * 
	 * @param key - id of the task
	 * @return selected task or null
	 */
	public Task<T> getTask(String key);
	/**
	 * @return immutable collection of tasks of this
	 */
	public Collection<Task<T>> getTasks();
	

	/**
	 * @throws IlleagalArgumentException if task.getParent() != null
	 * @throws IlleagalArgumentException if getTask(task.getKey()) != null
	 * 
	 * @param mission - the task to add
	 * @return true if sucessfully added
	 */
	public boolean addTask(Task<T> task);
	
	/**
	 * 
	 * @param key - the key of the task to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeTask(String key);
	
	@Override
	public Quest<T> getParent();

	/**
	 * 
	 * @return get requires of this
	 */
	public Collection<Require<T>> getRequires();
	
	/**
	 * @param key - key of require
	 * @return get require with key or null
	 */
	public Require<T> getRequire(String key);
	
	/**
	 * @throws IlleagalArgumentException if require.getParent() != null
	 * @throws IlleagalArgumentException if getRequire(require.getKey()) != null
	 * 
	 * @param require - the require to add
	 * @return true if sucessfully added
	 */
	public boolean addRequire(Require<T> require);
	
	/**
	 * 
	 * @param key - the key of the require to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeRequire(String key);
	

	/**
	 * 
	 * @return get rewards of this
	 */
	public Collection<Reward<T>> getStartRewards();
	
	/**
	 * @param key - key of reward
	 * @return get reward with key or null
	 */
	public Reward<T> getStartReward(String key);
	
	/**
	 * @throws IlleagalArgumentException if reward.getParent() != null
	 * @throws IlleagalArgumentException if getStartReward(reward.getKey()) != null
	 * 
	 * @param reward - the reward to add
	 * @return true if sucessfully added
	 */
	public boolean addStartReward(Reward<T> reward);
	
	/**
	 * 
	 * @param key - the key of the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeStartReward(String key);
	

	/**
	 * 
	 * @return get rewards of this
	 */
	public Collection<Reward<T>> getCompleteRewards();
	
	/**
	 * @param key - key of reward
	 * @return get reward with key or null
	 */
	public Reward<T> getCompleteReward(String key);
	
	/**
	 * @throws IlleagalArgumentException if reward.getParent() != null
	 * @throws IlleagalArgumentException if getCompleteReward(reward.getKey()) != null
	 * 
	 * @param reward - the reward to add
	 * @return true if sucessfully added
	 */
	public boolean addCompleteReward(Reward<T> reward);
	
	/**
	 * 
	 * @param key - the key of the reward to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeCompleteReward(String key);
	

	/**
	 * 
	 * @return get rewards of this
	 */
	public Collection<Reward<T>> getFailRewards();
	
	/**
	 * @param key - key of reward
	 * @return get reward with key or null
	 */
	public Reward<T> getFailReward(String key);
	
	/**
	 * @throws IlleagalArgumentException if reward.getParent() != null
	 * @throws IlleagalArgumentException if getFailReward(reward.getKey()) != null
	 * 
	 * @param reward - the reward to add
	 * @return true if sucessfully added
	 */
	public boolean addFailReward(Reward<T> reward);
	
	/**
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
