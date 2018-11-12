package emanondev.quests.interfaces;

import java.io.File;
import java.util.Set;

import emanondev.quests.interfaces.storage.IConfig;

public interface QuestManager<T extends User<T>> {
	/**
	 * 
	 * @return container of quests of this
	 */
	public QuestContainer<T> getQuestContainer();
	
	/**
	 * name of this, must be unique, used for both filename too
	 * @return 
	 */
	public String getName();
	
	/**
	 * 
	 * @return require manager of this
	 */
	public RequireManager<T> getRequireManager();

	/**
	 * 
	 * @return reward manager of this
	 */
	public RewardManager<T> getRewardManager();

	/**
	 * 
	 * @return task manager of this
	 */
	public TaskManager<T> getTaskManager();
	
	/**
	 * 
	 * @return user manager of this
	 */
	public UserManager<T> getUserManager();
	
	/**
	 * 
	 * @param keys - quest container
	 * @return a newly generated key
	 */
	public String generateQuestKey(QuestContainer<T> qc);

	/**
	 * 
	 * @param keys - quest
	 * @return a newly generated key
	 */
	public String generateMissionKey(Quest<T> quest);

	/**
	 * 
	 * @param keys - mission
	 * @return a newly generated key
	 */
	public String generateTaskKey(Mission<T> mission);

	/**
	 * 
	 * @param keys - already used keys for double unique check
	 * @return a newly generated key
	 */
	public String generateRewardKey(Set<String> keys);

	/**
	 * 
	 * @param keys - already used keys for double unique check
	 * @return a newly generated key
	 */
	public String generateRequireKey(Set<String> keys);

	public void save();

	public IConfig getQuestContainerConfig();

	public IConfig getConfig();

	public File getUsersFolder();

	public File getFolder();

	public IConfig getUserConfig(String uid);

	public void saveQuestContainer();

}
