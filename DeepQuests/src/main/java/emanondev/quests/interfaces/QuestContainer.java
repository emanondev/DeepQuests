package emanondev.quests.interfaces;

import java.util.Collection;

import org.bukkit.Bukkit;

import emanondev.quests.Quests;

public interface QuestContainer<T extends User<T>> extends QuestComponent<T>{
	

	public default QuestManager<T> getQuestManager(){
		return getQuestManager();
	}

	public default QuestContainer<T> getParent(){
		return this;
	}
	
	public Quest<T> getQuest(String key);
	
	public Collection<Quest<T>> getQuests();
	
	/**
	 * force save the QuestContainer and player progress
	 */
	public void save();

	/**
	 * call save delayed by asynchronous 5 seconds
	 */
	public default void saveDelayedAsync() {
		Bukkit.getScheduler().runTaskLaterAsynchronously(Quests.get(), new Runnable() {
			public void run() {
				save();
			}
		}, 5000);
	}

}
