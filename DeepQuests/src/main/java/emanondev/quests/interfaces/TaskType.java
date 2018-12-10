package emanondev.quests.interfaces;

import org.bukkit.event.Listener;

public interface TaskType<T extends User<T>> extends QuestComponentType<T,Task<T>>,Listener {


	public String getDefaultUnstartedDescription(Task<T> task);

	public String getDefaultProgressDescription(Task<T> task);
	

}
