package emanondev.quests.task;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.configuration.MemorySection;

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

	private HashMap<String,TaskTypeInfo> types = new HashMap<String,TaskTypeInfo>();
	
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
	public void registerType(TaskType type,Class<? extends Task> clazz) {
		if (types.containsKey(type.getKey()))
			throw new IllegalArgumentException("The TaskType "+type.toString()
						+" is already registered");
		types.put(type.getKey(), new TaskTypeInfo(type,clazz));
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
	}
	/**
	 * uregister all taskType registered
	 */
	public void unregisterAll() {
		types.clear();
	}
	/**
	 * 
	 * @param key
	 * @return <br>- a TaskType with taskType.getkey().equals(key)
	 * <br>- null if no taskType.getkey().equals(key) is registered
	 */
	public TaskType getTaskType(String key) {
		return types.get(key.toUpperCase()).type;
	}
	
	
	
	private class TaskTypeInfo {
		private final TaskType type;
		private final Class<? extends Task> clazz;

		private TaskTypeInfo(TaskType type,Class<? extends Task> clazz) {
			if (type == null || clazz == null)
				throw new NullPointerException();
			this.type = type;
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
	}

	public Task readTask(String type, MemorySection m, Mission mission) {
		TaskTypeInfo info = types.get(type.toUpperCase());
		if (info == null)
			throw new IllegalArgumentException("tasktype "+type+" do not exist");
		try {
			return info.clazz.getConstructor(MemorySection.class,Mission.class)
					.newInstance(m,mission);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error while creating an instance of "+info.clazz.getName());
		}
	}
	

}
