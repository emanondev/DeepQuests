package emanondev.quests.interfaces;


public interface TaskManager<T extends User<T>> extends QuestComponentTypeManager<T,Task<T>,TaskType<T>> {
	
	public default TaskType<T> getType(Task<T> task){
		return getType(task.getTypeName());
	}

	@Override
	public TaskType<T> getType(String id);

}
