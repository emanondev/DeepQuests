package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.Listener;

import emanondev.quests.mission.Mission;

public abstract class TaskType implements Listener {
	
	private final String key;
	private final String displayName;
	private final Class<? extends Task> clazz;
	public TaskType(String value,String displayName,Class<? extends Task> clazz) {
		if (value == null || clazz == null)
			throw new NullPointerException();
		if (value.isEmpty() || value.contains(" "))
			throw new IllegalArgumentException("invalid task name");
		this.key = value.toUpperCase();

		if (displayName==null)
			this.displayName = this.key.toLowerCase().replace("_", " ");
		else
			this.displayName = displayName;
		boolean ok = true;
		
		try {
			clazz.getConstructor(MemorySection.class,Mission.class)
							.newInstance(null,null);
		} catch (InstantiationException|IllegalAccessException
				|NoSuchMethodException|SecurityException e) {
			e.printStackTrace();
			ok = false;
		} catch (Exception e) {
		}
		if (!ok)
			throw new IllegalArgumentException(clazz.getName()+" must implement "
				+ "a public constructor with (MemorySection m, Mission mission) ");
		this.clazz = clazz;
	}
	
	public final Class<? extends Task> getTaskClass(){
		return clazz;
	}
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
		return "TaskType:[Key: "+key+", DisplayName: "+displayName+"]";
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

	/**
	 * @return display name
	 */
	public String getDisplayName() {
		return displayName;
	}
	
}
