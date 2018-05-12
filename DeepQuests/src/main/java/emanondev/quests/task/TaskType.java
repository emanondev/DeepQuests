package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.Listener;

import emanondev.quests.mission.Mission;

public abstract class TaskType implements Listener {
	
	protected final String key;
	/**
	 * 
	 * @param key
	 */
	public TaskType(String key) {
		if (key == null)
			throw new NullPointerException();
		if (key.isEmpty() || key.contains(" "))
			throw new IllegalArgumentException("invalid task name");
		this.key = key.toUpperCase();
	}

	public abstract Task getTaskInstance(MemorySection m,Mission parent);
	/**
	 * 
	 * @return the string value of the TaskType unique key
	 */
	public final String getKey() {
		return key;
	}

	public boolean equals(Object o) {
		if(!(o instanceof TaskType))
			return false;
		return key.equals(((TaskType) o).key);
	}
	
	
	public String toString() {
		return "TaskType:[Key: "+key+"]";
	}
	
	/**
	 * Class to build taskTypes
	 * @author emanon
	 *
	 *//*
	public static class Builder {
		/**
		 * Note: key will be registered with UpperCase
		 * @param key must be != null, not empty and must not contains " "
		 * @return a new TaskType
		 * @throws IllegalArgumentException - if there is already a registered TaskType with this name
		 *//*
		public static TaskType build(String key) {
			return build(key, null);
		}
		/**
		 * Note: key will be registered with UpperCase
		 * @param key must be != null, not empty and must not contains " "
		 * @param displayName task name for display utilities
		 * @return a new TaskType
		 * @throws IllegalArgumentException - if there is already a registered TaskType with this name
		 *//*
		public static TaskType build(String key,String displayName) {
			if (Quests.getInstance().getTaskManager().existType(key))
				throw new IllegalArgumentException("A task with this key already exist, use Quests.getInstance().getTaskManager().getTask(key)");
			TaskType taskType = new TaskType(key,displayName);
			//Quests.getInstance().getTaskManager().registerType(taskType);
			return taskType;
		}
	}*/
	
}
