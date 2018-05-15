package emanondev.quests.task;

import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Defaults;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.YmlLoadable;

/**
 * Any implementations of Task require to implement a Constructor like<br>
 * [YourSubTaskClass](MemorySection m,Mission parent)
 * 
 * @author emanon <br>
 *
 * 
 */
public abstract class AbstractTask extends YmlLoadable implements Task {
	/**
	 * path from MemorySection m of the task to the type of the task
	 */
	public static final String PATH_TASK_TYPE = "type";
	/**
	 * path from MemorySection m of the task to the max progress value of the task
	 */
	public static final String PATH_TASK_MAX_PROGRESS = "max-progress";
	

	public static final String PATH_TASK_DESCRIPTION_PROGRESS = "description-progress";
	public static final String PATH_TASK_DESCRIPTION_UNSTARTED = "description-unstarted";
	
	/**
	 * Config variables can be stored and read from MemorySection m<br>
	 * also additional info may be taken from Mission parent<br>
	 * 
	 * N.B. parent mission is not fully loaded when Task is generating<br>
	 * - parent.getTask() will be empty
	 * @param m must be != null
	 * @param parent must be != null
	 */
	public AbstractTask(MemorySection m,Mission parent,TaskType type) {
		super(m);
		if (parent == null||type ==null)
			throw new NullPointerException();
		this.parent = parent;
		this.type = type;
		this.maxProgress = loadMaxProgress(m);
		if (this.maxProgress <= 0)
			throw new IllegalArgumentException("task max progress must be always > 0");
		this.descUnstarted = loadDescUnstarted(m);
		this.descProgress = loadDescProgress(m);
	}
	private String loadDescProgress(MemorySection m) {
		if (!m.isString(PATH_TASK_DESCRIPTION_UNSTARTED)) {
			String val = Defaults.TaskDef.getUnstartedDescription()
					.replace("<task>",this.getNameID());
			m.set(PATH_TASK_DESCRIPTION_UNSTARTED, val);
			setDirty(true);
			return val;
		}
		return m.getString(PATH_TASK_DESCRIPTION_UNSTARTED, 
			Defaults.TaskDef.getUnstartedDescription()
			.replace("<task>",this.getNameID()));
	}
	public String getUnstartedDescription() {
		return this.descUnstarted;
	}
	public String getProgressDescription() {
		return this.descProgress;
	}
	private String loadDescUnstarted(MemorySection m) {
		if (!m.isString(PATH_TASK_DESCRIPTION_PROGRESS)) {
			String val = Defaults.TaskDef.getProgressDescription()
					.replace("<task>",this.getNameID());
			m.set(PATH_TASK_DESCRIPTION_PROGRESS, val);
			setDirty(true);
			return val;
		}
		return m.getString(PATH_TASK_DESCRIPTION_PROGRESS, 
			Defaults.TaskDef.getProgressDescription()
			.replace("<task>",this.getNameID()));
	}
	private final String descUnstarted;
	private final String descProgress;
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
	public void setDirty(boolean value) {
		super.setDirty(value);
		if (this.isDirty()==true)
			parent.setDirty(true);
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
	//protected abstract String getDefaultDescription();

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
	
	/**
	 * checks if the task, his mission and associated quest are all allowed in this world
	 */
	@Override
	public boolean isWorldAllowed(World w) {
		return super.isWorldAllowed(w)
				&& getParent().isWorldAllowed(w)
				&& getParent().getParent().isWorldAllowed(w);
	}
	
	public boolean onProgress(QuestPlayer p) {
		return onProgress(p,1);
	}
	
	
	public boolean onProgress(QuestPlayer p,int amount) {
		return p.progressTask(this, amount);
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
