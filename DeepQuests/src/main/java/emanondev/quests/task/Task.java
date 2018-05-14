package emanondev.quests.task;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.Savable;

public interface Task extends Savable {
	public String getDisplayName();
	public String getNameID();
	/**
	 * 
	 * @return the Mission that holds this task
	 */
	public Mission getParent();
	/**
	 * 
	 * @return the type of the task
	 */
	public TaskType getTaskType();
	/**
	 * when a player starts a Task his progress on th task is 0<br>
	 * task is completed when player progress reach this value
	 * @return the maximus progress of this task
	 */
	public int getMaxProgress();
	
	
	public String getUnstartedDescription();
	public String getProgressDescription();
	
	public boolean onProgress(QuestPlayer p);
	public boolean onProgress(QuestPlayer p,int amount);
	
	
}
