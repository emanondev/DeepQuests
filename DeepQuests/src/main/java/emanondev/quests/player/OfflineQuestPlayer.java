package emanondev.quests.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData.TaskData;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.utils.DisplayState;

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
	public boolean passTo(OfflineQuestPlayer offQP) {
		if (offQP.getPlayer().getName().equals(getPlayer().getName()))
			return false;
		save();
		
		offQP.save();
		if (getPlayer().isOnline())
			getPlayer().getPlayer().kickPlayer("Changing Quest Database");
		if (offQP.getPlayer().isOnline())
			offQP.getPlayer().getPlayer().kickPlayer("Changing Quest Database");
		try {
			String data1 = data.saveToString();
			String data2 = offQP.data.saveToString();

			data.getKeys(false).forEach((key) -> data.set(key,null));
			offQP.data.getKeys(false).forEach((key) -> offQP.data.set(key,null));
			
			data.loadFromString(data2);
			offQP.data.loadFromString(data1);
			
			data.save();
			offQP.data.save();
			/*
			File temp = new File(Quests.get().getDataFolder(),FILE_BASE_PATH+"tempFile");
			File player1 = new File(Quests.get().getDataFolder(),FILE_BASE_PATH+getPlayer().getUniqueId().toString());
			File player2 = new File(Quests.get().getDataFolder(),FILE_BASE_PATH+offQP.getPlayer().getUniqueId().toString());
			
			if(!data.getFile().renameTo(temp))
					throw new IOException();
			if(!offQP.data.getFile().renameTo(player1))
					throw new IOException();
			if(!data.getFile().renameTo(player2))
					throw new IOException();*/
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static final String FILE_BASE_PATH = "playerdatabase"+File.separator;
	private static final String PATH_QUESTS = "quests";
	private static final String PATH_QUEST_POINTS = "quest-points";
	
	private boolean[] seeQuests = new boolean[DisplayState.values().length];
	private boolean[] seeMissions = new boolean[DisplayState.values().length];
	
	public boolean canSeeQuestState(DisplayState state) {
		return seeQuests[state.ordinal()];
	}
	public boolean canSeeMissionState(DisplayState state) {
		return seeMissions[state.ordinal()];
	}
	public void toggleCanSeeQuestState(DisplayState state) {
		seeQuests[state.ordinal()] = !seeQuests[state.ordinal()];
		data.set("cansee.quest."+state.toString().toLowerCase(),seeQuests[state.ordinal()]);
		shouldSave = true;
	}
	public void toggleCanSeeMissionState(DisplayState state) {
		seeMissions[state.ordinal()] = !seeMissions[state.ordinal()];
		data.set("cansee.mission."+state.toString().toLowerCase(),seeMissions[state.ordinal()]);
		shouldSave = true;
	}
	
	
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
		return getMissionData(task.getParent()).tasksData.get(task.getID());
	}
	public MissionData getMissionData(Mission mission) {
		return getQuestData(mission.getParent())
				.missionsData.get(mission.getID());
	}
	public QuestData getQuestData(Quest quest) {
		return questsData.get(quest.getID());
	}
	
	private int questPoints;
	public OfflineQuestPlayer(OfflinePlayer p) {
		if (p== null)
			throw new NullPointerException();
		this.p = p;
		data = new YMLConfig(Quests.get(),FILE_BASE_PATH+p.getUniqueId().toString());

		Quests.get().getQuestManager().getQuests().forEach((quest)->{
			questsData.put(quest.getID(),new QuestData(quest));
		});
		questPoints = data.getInt(PATH_QUEST_POINTS, 0);
		for (DisplayState state:DisplayState.values()) {
			seeQuests[state.ordinal()] = data.getBoolean("cansee.quest."+state.toString().toLowerCase(), Defaults.PlayerDef.canSeeQuestDisplay(state));
			seeMissions[state.ordinal()] = data.getBoolean("cansee.mission."+state.toString().toLowerCase(), Defaults.PlayerDef.canSeeMissionDisplay(state));
		}
		
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
	private void unregisterActiveTaskData(Collection<TaskData> coll) {
		coll.forEach((data)->{
			unregisterActiveTask(data.task);
		});
	}
	private void unregisterActiveTask(Collection<Task> coll) {
		coll.forEach((task)->{
			unregisterActiveTask(task);
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
	public void resetMission(Mission mission) {
		resetMission(mission,true);
	}
	private void resetMission(Mission mission,boolean reload) {
		unregisterActiveTask(mission.getTasks());
		MissionData missionData = getMissionData(mission);
		missionData.erase();
		if (reload) {
			if (getPlayer() instanceof Player)
				Quests.get().getPlayerManager().reloadPlayer((Player) getPlayer());
			else
				data.save();
		}
	}
	public void resetQuest(Quest quest) {
		for(Mission mission:quest.getMissions())
			resetMission(mission,false);
		QuestData questData = getQuestData(quest);
		questData.erase();
		if (getPlayer() instanceof Player)
			Quests.get().getPlayerManager().reloadPlayer((Player) getPlayer());
		else
			data.save();
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
		@SuppressWarnings("unused")
		private int completedTimes;//TODO
		private boolean isFailed;
		
		private HashMap<String,MissionData> missionsData = new HashMap<String,MissionData>();
		private QuestData(Quest quest) {
			if (quest==null)
				throw new NullPointerException();
			this.quest = quest;
			baseQuestPath = PATH_QUESTS+"."+this.quest.getID();
			this.quest.getMissions().forEach((mission)->{
				missionsData.put(mission.getID(), new MissionData(mission));
			});
			this.lastStarted = data.getLong(baseQuestPath+"."+PATH_LAST_STARTED, 0);
			this.lastCompleted = data.getLong(baseQuestPath+"."+PATH_LAST_COMPLETED, 0);
			this.completedBefore = data.getBoolean(baseQuestPath+"."+PATH_WAS_COMPLETED_BEFORE, false);
			this.completedTimes = data.getInt(baseQuestPath+"."+PATH_COMPLETED_TIMES, 0);
			this.isFailed = data.getBoolean(baseQuestPath+"."+PATH_IS_FAILED, false);
		}
		public void erase() {
			data.set(baseQuestPath,null);
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
		public long getCooldownTimeLeft() {
			return lastCompleted+quest.getCooldownData().getCooldownTime()-new Date().getTime();
		}
		public boolean isOnCooldown() {
			return getCooldownTimeLeft()>0;
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
				baseMissionPath = baseQuestPath+"."+PATH_MISSIONS+"."+this.mission.getID();
				this.mission.getTasks().forEach((task)->{
					tasksData.put(task.getID(), new TaskData(task));
				});
				this.lastStarted = data.getLong(baseMissionPath+"."+PATH_LAST_STARTED, 0);
				this.lastCompleted = data.getLong(baseMissionPath+"."+PATH_LAST_COMPLETED, 0);
				this.started = data.getBoolean(baseMissionPath+"."+PATH_IS_STARTED, false);
				this.completedBefore = data.getBoolean(baseMissionPath+"."+PATH_WAS_COMPLETED_BEFORE, false);
				this.active = data.getBoolean(baseMissionPath+"."+PATH_IS_ACTIVE, false);
				this.completedTimes = data.getInt(baseMissionPath+"."+PATH_COMPLETED_TIMES, 0);
				this.isFailed = data.getBoolean(baseMissionPath+"."+PATH_IS_FAILED, false);
				if (this.active == true)
					registerActiveTask(tasksData.values());
			}
			public void erase() {
				data.set(baseMissionPath,null);
			}
			public boolean isFailed() {
				return this.isFailed;
			}
			public boolean isStarted() {
				return this.started;
			}
			public boolean isPaused() {
				return !this.active;
			}
			public long getLastStarted() {
				return this.lastStarted;
			}
			public long getLastCompleted() {
				return this.lastCompleted;
			}
			public boolean hasStarted() {
				return this.lastStarted > 0;
			}
			public boolean hasCompleted() {
				return completedBefore;
			}
			public long getCooldownTimeLeft() {
				return this.lastCompleted+this.mission.getCooldownData().getCooldownTime()-new Date().getTime();
			}
			public boolean isOnCooldown() {
				return this.getCooldownTimeLeft()>0;
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
						unregisterActiveTaskData(tasksData.values());
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
				if (mission.getCooldownData().isRepeatable()==false) {
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
					baseTaskPath = baseMissionPath+"."+PATH_TASKS+"."+this.task.getID();
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
				public Task getTask() {
					return task;
				}
			}
		}
	}
}
