package emanondev.quests.interfaces;

import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface QuestComponent<T extends User<T>> extends ConfigurationSerializable,Comparable<QuestComponent<T>> {
	
	/**
	 * 
	 * @return the parent of this
	 */
	public QuestComponent<T> getParent();
	
	/**
	 * @return recap of info abouth this
	 */
	public List<String> getInfo();
	
	/**
	 * 
	 * @return the QuestManager of this
	 */
	public default QuestManager<T> getQuestManager(){
		return getParent().getQuestManager();
	}
	
	/**
	 * 
	 * @return the unique key of this
	 */
	public String getKey();
	
	
	/**
	 * Sets the parent of this, parent shall be set only once
	 */
	public void setParent(QuestComponent<T> parent);

	public default int compareTo(QuestComponent<T> qc) {
		if (qc == null)
			return -getPriority();
		return qc.getPriority()-getPriority();
	}
	/**
	 * priority is used for sorting like ordering display, reward assign order and such
	 * 
	 * @return priority of this
	 */
	public int getPriority();
	/**
	 * Allowed values [Integer.MIN_VALUE;Integer.MAX_VALUE]
	 * 
	 * @param priority the value
	 * @return true if succesfully updated
	 */
	public boolean setPriority(int priority);
	
	/**
	 * return the displayName of this
	 */
	public String getDisplayName();
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean setDisplayName(String name);

}
