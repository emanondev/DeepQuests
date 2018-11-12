package emanondev.quests.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import emanondev.quests.Quests;

public class ATaskManager<T extends User<T>> implements TaskManager<T> {
	
	private Map<String,TaskType<T>> types = new HashMap<>();

	@Override
	public TaskType<T> getType(String id) {
		return types.get(id);
	}

	@Override
	public void registerType(TaskType<T> type) {
		if (type == null)
			throw new NullPointerException();
		types.put(type.getID(), type);
		//enabled listener
		Bukkit.getPluginManager().registerEvents(type, Quests.get());
	}

	@Override
	public Collection<TaskType<T>> getTypes() {
		return Collections.unmodifiableCollection(types.values());
	}

}
