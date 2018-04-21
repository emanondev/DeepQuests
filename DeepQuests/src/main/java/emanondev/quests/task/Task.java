package emanondev.quests.task;

import java.util.List;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.utils.YmlLoadable;

/**
 * Any implementations of Task require to implement a Constructor like<br>
 * [YourSubTaskClass](MemorySection m,Mission parent)
 * 
 * @author emanon <br>
 *
 * 
 */
public abstract class Task extends YmlLoadable{
	/**
	 * path from MemorySection m of the task to the type of the task
	 */
	public static final String PATH_TASK_TYPE = "type";
	/**
	 * path from MemorySection m of the task to the max progress value of the task
	 */
	public static final String PATH_TASK_MAX_PROGRESS = "max-progress";
	
	/**
	 * Config variables can be stored and read from MemorySection m<br>
	 * also additional info may be taken from Mission parent<br>
	 * N.B. parent mission is not fully loaded when Task is generating<br>
	 * some metods might not work as expected:<br>
	 * - info from parent.getDisplayInfo() might be uncompleted/uncorrect<br>
	 * - parent.getTask() will be empty
	 * @param m must be != null
	 * @param parent must be != null
	 */
	public Task(MemorySection m,Mission parent) {
		super(m);
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		this.type = loadType(m);
		this.maxProgress = loadMaxProgress(m);
		if (this.maxProgress <= 0)
			throw new IllegalArgumentException("task max progress must be always > 0");
		
	}
	private final TaskType type;
	private final int maxProgress;
	private final Mission parent;
	/**
	 * 
	 * @return the Mission that holds this task
	 */
	public Mission getParent() {
		return parent;
	}
	/**
	 * 
	 * @return the type of the task
	 */
	public TaskType getTaskType() {
		return type;
	}
	/**
	 * when a player starts a Task his progress on th task is 0<br>
	 * task is completed when player progress reach this value
	 * @return the maximus progress of this task
	 */
	public int getMaxProgress() {
		return maxProgress;
	}
	/**
	 * tasks name may have a default prefix as but<br>
	 * not limited to colors and formatting codes
	 * @return the prefix
	 */
	@Override
	protected String getDisplayNameDefaultPrefix() {
		return "";
	}
	private TaskType loadType(MemorySection m) {
		if (m == null)
			throw new NullPointerException();
		String typeTxt = m.getString(PATH_TASK_TYPE);
		if (typeTxt == null || typeTxt.isEmpty())
			throw new NullPointerException();
		return Quests.getInstance().getTaskManager().getTaskType(typeTxt);
	}
	/**
	 * returned value must be > 0
	 * @param m config
	 * @return the max progress amount
	 */
	protected int loadMaxProgress(MemorySection m) {
		if (m == null)
			throw new NullPointerException();
		int pr = m.getInt(PATH_TASK_MAX_PROGRESS);
		return pr;
	}
	/**
	 * utility to autogenerate descriptions on gui<br>
	 * this method should return a string that represent<br>
	 * info abouth the task<br><br>
	 * 
	 * ex:<br>
	 * a Mining task<br>
	 * task.getDefaultDescription() = "&eMine "+task.getMaxProgress()+" "+task.getBlockType()+" blocks"
	 * @return
	 */
	protected abstract String getDefaultDescription();

	protected  List<String> getWorldsListDefault(){
		return Defaults.TaskDef.getWorldsListDefault();
	}
	protected boolean shouldWorldsAutogen() {
		return Defaults.TaskDef.shouldWorldsAutogen();
	}
	protected boolean getUseWorldsAsBlackListDefault() {
		return Defaults.TaskDef.getUseWorldsAsBlackListDefault();
	}
	@Override
	protected boolean shouldAutogenDisplayName() {
		return Defaults.TaskDef.shouldAutogenDisplayName();
	}
	
	/*
	 * displayname:
	 * worlds:
	 * 		list:
	 * 		isblacklist:
	 * type:
	 * maxprogress:
	 */
	

}
