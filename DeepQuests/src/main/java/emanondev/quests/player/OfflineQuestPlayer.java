package emanondev.quests.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.OfflinePlayer;

import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData.TaskData;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class OfflineQuestPlayer {
	/*
	 * questpoints:
	 * 			amount: <amount>
	 * quests:
	 * 		<questname1>:
	 * 				missions:
	 * 						<missionname1>:
	 * 									state: ongoing
	 * 									startedon: 1001022921
	 * 									completedon: 
	 * 									tasks:
	 * 										<taskname1>
	 * 										<taskname2>
	 * 										<taskname3>
	 * 						<missionname2>:
	 * 									state: unstarted (/paused/ongoing)
	 * 						<missionname3>:
	 * 		<questname2>:
	 * 
	 * 
	 * 
	 * 
	 */
	protected boolean shouldSave = false;
	public boolean shouldSave() {
		return shouldSave;
	}
	public void save() {
		data.save();
		shouldSave = false;
	}
	
	private static final String FILE_BASE_PATH = "playerdatabase"+File.separator;
	private static final String PATH_QUESTS = "quests";
	private static final String PATH_QUEST_POINTS = "quest-points";
	
	private final OfflinePlayer p;
	private HashMap<String,QuestData> questsData = new HashMap<String,QuestData>();
	private HashMap<TaskType,List<Task>> activeTasks = new HashMap<TaskType,List<Task>>();
	private final YMLConfig data;

	public int getCompletedMissionAmount(Quest q) {
		int i = 0;
		for(MissionData missionData : getQuestData(q).missionsData.values())
			if (missionData.hasCompleted())
				i++;
		return i;
	}
	public TaskData getTaskData(Task task) {
		return getMissionData(task.getParent()).tasksData.get(task.getNameID());
	}
	public MissionData getMissionData(Mission mission) {
		return getQuestData(mission.getParent())
				.missionsData.get(mission.getNameID());
	}
	public QuestData getQuestData(Quest quest) {
		return questsData.get(quest.getNameID());
	}
	
	private int questPoints;
	public OfflineQuestPlayer(OfflinePlayer p) {
		if (p== null)
			throw new NullPointerException();
		this.p = p;
		data = new YMLConfig(Quests.getInstance(),FILE_BASE_PATH+p.getUniqueId().toString());

		Quests.getInstance().getQuestManager().getQuests().forEach((quest)->{
			questsData.put(quest.getNameID(),new QuestData(quest));
		});
		questPoints = data.getInt(PATH_QUEST_POINTS, 0);
		
		
	}
	public int getQuestPoints() {
		return questPoints;
	}
	public void setQuestPoints(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException();
		if (questPoints==amount)
			return;
		questPoints = amount;
		data.set(PATH_QUEST_POINTS, questPoints);
		shouldSave = true;
	}
	
	private void registerActiveTask(Task task) {
		List<Task> l = activeTasks.get(task.getTaskType());
		if (l==null) {
			l = new ArrayList<Task>();
			activeTasks.put(task.getTaskType(),l);
		}
		l.add(task);
	}
	private void registerActiveTask(Collection<TaskData> c) {
		if (c==null)
			return;
		c.forEach((data)->{
			if (data.isCompleted())
				return;
			registerActiveTask(data.task);
		});
	}
	private void unregisterActiveTask(Task task) {
		List<Task> l = activeTasks.get(task.getTaskType());
		if (l==null)
			return;
		l.remove(task);
	}
	private void unregisterActiveTask(Collection<TaskData> c) {
		c.forEach((data)->{
			unregisterActiveTask(data.task);
		});
	}
	/**
	 * do never ever loop this with iterator or forEach<br>
	 * use instead a for (int i = 0; i < list.size(); i++)
	 * @param type
	 * @return
	 */
	public List<Task> getActiveTasks(TaskType type) {
		List<Task> c;
		c = activeTasks.get(type);
		if (c== null)
			c = new ArrayList<Task>();
		return Collections.unmodifiableList(c);
	}
	
	public OfflinePlayer getPlayer() {
		return p;
	}
	
	
	
	
	public class QuestData {
		private final String baseQuestPath;
		private final Quest quest;
		private final static String PATH_LAST_STARTED = "last-started";
		private final static String PATH_LAST_COMPLETED = "last-completed";
		private final static String PATH_IS_STARTED = "started";
		private static final String PATH_IS_FAILED = "is-failed";
		private final static String PATH_WAS_COMPLETED_BEFORE = "completed-before";
		private final static String PATH_COMPLETED_TIMES = "completed-times";
		private final static String PATH_IS_ACTIVE = "active";
		
		private long lastStarted;
		private long lastCompleted;
		private boolean completedBefore;
		private int completedTimes;//TODO
		private boolean isFailed;
		
		private HashMap<String,MissionData> missionsData = new HashMap<String,MissionData>();
		private QuestData(Quest quest) {
			if (quest==null)
				throw new NullPointerException();
			this.quest = quest;
			baseQuestPath = PATH_QUESTS+"."+this.quest.getNameID();
			this.quest.getMissions().forEach((mission)->{
				missionsData.put(mission.getNameID(), new MissionData(mission));
			});
			this.lastStarted = data.getLong(baseQuestPath+"."+PATH_LAST_STARTED, 0);
			this.lastCompleted = data.getLong(baseQuestPath+"."+PATH_LAST_COMPLETED, 0);
			this.completedBefore = data.getBoolean(baseQuestPath+"."+PATH_WAS_COMPLETED_BEFORE, false);
			this.completedTimes = data.getInt(baseQuestPath+"."+PATH_COMPLETED_TIMES, 0);
			this.isFailed = data.getBoolean(baseQuestPath+"."+PATH_IS_FAILED, false);
		}
		public boolean isFailed() {
			return isFailed;
		}
		public boolean isStarted() {
			for(MissionData missionData : missionsData.values()) {
				if (missionData.isStarted())
					return true;
			}
			return false;
		}
		public long getLastStarted() {
			return lastStarted;
		}
		public long getLastCompleted() {
			return lastCompleted;
		}
		public boolean hasStarted() {
			return lastStarted > 0;
		}
		public boolean hasCompleted() {
			return completedBefore;
		}
		public long cooldownTimeLeft() {
			if (this.quest.getCooldownTime()<0)
				return -1L;
			long l =  new Date().getTime()-(lastCompleted+quest.getCooldownTime());
			if (l<0)
				return 0L;
			return l;
		}
		public boolean isOnCooldown() {
			return cooldownTimeLeft()>0;
		}
		
		
		
		
		
		
		
		public class MissionData {
			private final static String PATH_MISSIONS = "missions";

			private final String baseMissionPath;
			private long lastStarted;
			private long lastCompleted;
			private boolean started;
			private boolean completedBefore;
			private int completedTimes;
			private boolean active;
			private boolean isFailed;
			private Mission mission;
			private HashMap<String,TaskData> tasksData = new HashMap<String,TaskData>();
			public MissionData(Mission mission) {
				if (mission==null)
					throw new NullPointerException();
				this.mission = mission;
				baseMissionPath = baseQuestPath+"."+PATH_MISSIONS+"."+this.mission.getNameID();
				this.mission.getTasks().forEach((task)->{
					tasksData.put(task.getNameID(), new TaskData(task));
				});
				this.lastStarted = data.getLong(baseMissionPath+"."+PATH_LAST_STARTED, 0);
				this.lastCompleted = data.getLong(baseMissionPath+"."+PATH_LAST_COMPLETED, 0);
				this.started = data.getBoolean(baseMissionPath+"."+PATH_IS_STARTED, false);
				this.completedBefore = data.getBoolean(baseMissionPath+"."+PATH_WAS_COMPLETED_BEFORE, false);
				this.active = data.getBoolean(baseMissionPath+"."+PATH_IS_ACTIVE, false);
				this.completedTimes = data.getInt(baseMissionPath+"."+PATH_COMPLETED_TIMES, 0);
				this.isFailed = data.getBoolean(baseMissionPath+"."+PATH_IS_FAILED, false);
				registerActiveTask(tasksData.values());
					
			}
			public boolean isFailed() {
				return isFailed;
			}
			public boolean isStarted() {
				return started;
			}
			public boolean isPaused() {
				return !active;
			}
			public long getLastStarted() {
				return lastStarted;
			}
			public long getLastCompleted() {
				return lastCompleted;
			}
			public boolean hasStarted() {
				return lastStarted > 0;
			}
			public boolean hasCompleted() {
				return completedBefore;
			}
			public long getCooldownTimeLeft() {
				if (this.mission.getCooldownTime()<0)
					return -1L;
				long l =  lastCompleted+mission.getCooldownTime()-new Date().getTime();
				if (l<0)
					return 0L;
				return l;
			}
			
			protected void start() {//TODO
				this.lastStarted = new Date().getTime();
				data.set(baseMissionPath+"."+PATH_LAST_STARTED, lastStarted);
				this.started = true;
				data.set(baseMissionPath+"."+PATH_IS_STARTED, started);
				for(TaskData taskData : tasksData.values()) {
					taskData.setProgress(0);
				}
				this.active = true;
				data.set(baseMissionPath+"."+PATH_IS_ACTIVE, active);
				registerActiveTask(tasksData.values());
				shouldSave = true;
			}
			protected void setPaused(boolean paused) {//TODO
				if (this.active==paused) {
					this.active = !active;
					data.set(baseMissionPath+"."+PATH_IS_ACTIVE, active);
					if (this.active == true)
						registerActiveTask(tasksData.values());
					else
						unregisterActiveTask(tasksData.values());
					shouldSave = true;
				}
			}
			
			protected void complete() {//TODO
				if (this.completedBefore==false) {
					this.completedBefore = true;
					data.set(baseMissionPath+"."+PATH_WAS_COMPLETED_BEFORE, completedBefore);
				}
				this.completedTimes = this.completedTimes+1;
				data.set(baseMissionPath+"."+PATH_COMPLETED_TIMES, completedTimes);
				end();
			}
			protected void fail() {//TODO
				if (mission.getCooldownTime()<0) {
					this.isFailed = true;
					data.set(baseMissionPath+"."+PATH_IS_FAILED, isFailed);
				}
				end();
			}
			private void end() {
				this.active = false;
				data.set(baseMissionPath+"."+PATH_IS_ACTIVE, active);
				this.started = false;
				data.set(baseMissionPath+"."+PATH_IS_STARTED, started);
				this.lastCompleted = new Date().getTime();
				data.set(baseMissionPath+"."+PATH_LAST_COMPLETED, lastCompleted);
				
				for (TaskData taskData : this.tasksData.values()){
					//taskData.setProgress(0);
					unregisterActiveTask(taskData.task);
				}
				shouldSave = true;
			}
			
			public Collection<TaskData> getTasksData(){
				return Collections.unmodifiableCollection(tasksData.values());
			}
			
			public class TaskData {
				private static final String PATH_TASKS = "tasks";
				private static final String PATH_PROGRESS = "progress";
				private final String baseTaskPath;
				private final Task task;
				private int progress;
				public TaskData(Task task) {
					this.task = task;
					baseTaskPath = baseMissionPath+"."+PATH_TASKS+"."+this.task.getNameID();
					progress = data.getInt(baseTaskPath+"."+PATH_PROGRESS,0);
				}
				public int getProgress() {
					return progress;
				}
				public boolean isCompleted() {
					return this.progress>=task.getMaxProgress();
				}
				protected void setProgress(int progress) {//TODO
					this.progress = Math.min(progress,task.getMaxProgress());
					if (isCompleted()) {
						unregisterActiveTask(this.task);
					}
					data.set(baseTaskPath+"."+PATH_PROGRESS, this.progress);
					shouldSave = true;
				}
				
			}
		}
	}
}
