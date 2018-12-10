package emanondev.quests.interfaces;

import java.util.Collection;

public interface Quest<T extends User<T>> extends QuestComponentWithCooldown<T> {
	
	/**
	 * 
	 * @param key - id of the mission
	 * @return selected mission or null
	 */
	public Mission<T> getMission(String key);
	
	/**
	 * @return immutable collection of missions of this
	 */
	public Collection<Mission<T>> getMissions();
	

	/**
	 * @throw IlleagalArgumentException if mission.getParent() != null
	 * @throw IlleagalArgumentException if getMission(mission.getKey()) != null
	 * 
	 * @param mission - the mission to add
	 * @return true if sucessfully added
	 */
	public boolean addMission(Mission<T> mission);
	
	/**
	 * 
	 * @param key - the key of the mission to remove
	 * @return true if sucessfully removed
	 */
	public boolean removeMission(String key);
	
	
	
	@Override
	public QuestContainer<T> getParent();
	
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
	 * @throw IlleagalArgumentException if require.getParent() != null
	 * @throw IlleagalArgumentException if getRequire(require.getKey()) != null
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
	 * @return
	 */
	public boolean isDeveloped();

}
