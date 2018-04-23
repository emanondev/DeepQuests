package emanondev.quests.task;

import java.util.HashMap;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.HandlerList;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;

/**
 * Get and instance of the class with Quests.getTaskManager()
 * <br><br>
 * this class is responsible for registering TaskTypes so if you want to add a custom
 * <br>tasktype be sure to register it
 * 
 * 
 * @author emanon
 *
 */
public class TaskManager {
	public TaskManager() {
		//unregisterAll();
	}

	private static HashMap<String,TaskType> types = new HashMap<String,TaskType>();
	
	/**
	 * @param type
	 * @return true if there is a registered TaskType with type == taskType.getKey() 
	 */
	public boolean existType(String type) {
		return types.containsKey(type.toUpperCase());
	}
	/**
	 * 
	 * @param type - taskType to register
	 * @throws IllegalArgumentException - if (existType(type.getKey()) == true)
	 */
	public void registerType(TaskType type) {
		if (types.containsKey(type.getKey()))
			throw new IllegalArgumentException("The TaskType "+type.toString()
						+" is already registered");
		types.put(type.getKey(), type);
		Quests.getInstance().registerListener(type);
		Quests.getInstance().getLoggerManager().getLogger("log")
			.log("Registered Task Type "+type.getKey());
	}
	/**
	 * 
	 * @param type - taskType to register
	 * @throws IllegalArgumentException - if (existType(type.getKey()) == false)
	 */
	public void unregisterType(TaskType type) {
		if (!types.containsKey(type.getKey()))
			throw new IllegalArgumentException("The TaskType "+type.toString()+" is not registered");
		types.remove(type.getKey());
		HandlerList.unregisterAll(type);
	}
	/**
	 * uregister all taskType registered
	 */
	public void unregisterAll() {
		for (TaskType type : types.values()) {
			types.remove(type.getKey());
			HandlerList.unregisterAll(type);
		}
	}
	/**
	 * 
	 * @param key
	 * @return <br>- a TaskType with taskType.getkey().equals(key)
	 * <br>- null if no taskType.getkey().equals(key) is registered
	 */
	public TaskType getTaskType(String key) {
		return types.get(key.toUpperCase());
	}
	
	public Task readTask(String type, MemorySection m, Mission mission) {
		TaskType info = types.get(type.toUpperCase());
		if (info == null)
			throw new IllegalArgumentException("tasktype "+type+" do not exist");
		try {
			return info.getTaskClass().getConstructor(MemorySection.class,Mission.class)
					.newInstance(m,mission);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error while creating an instance of "
										+info.getTaskClass().getName());
		}
	}
	

}
