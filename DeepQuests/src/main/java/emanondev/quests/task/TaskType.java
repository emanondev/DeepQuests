package emanondev.quests.task;

import java.util.List;

import org.bukkit.Material;
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
	public abstract Material getGuiItemMaterial();
	public abstract List<String> getDescription();
	
	
}
