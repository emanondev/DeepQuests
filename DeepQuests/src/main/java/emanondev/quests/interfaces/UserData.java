package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import emanondev.quests.utils.DisplayState;

public class UserData<T extends User<T>> implements ConfigurationSerializable {

	//private final String key;
	private int questPoints;

	@SuppressWarnings("unchecked")
	public UserData(Map<String,Object> map) {/*
		key = (String) map.get(Paths.KEY);
		if (key == null || key.isEmpty())
			throw new NullPointerException();*/
		questPoints = map.get(Paths.USERDATA_QUEST_POINTS) == null ? 0 : (int) map.get(Paths.USERDATA_QUEST_POINTS);
		for (DisplayState state:DisplayState.values()) {
			try{
				if (map.get(Paths.USERDATA_SEE_QUEST)!=null && (map.get(Paths.USERDATA_SEE_QUEST) instanceof Map<?,?>))
					canSeeQuestState[state.ordinal()] = 
					((Map<String,Boolean>) map.get(Paths.USERDATA_SEE_QUEST))
						.get(state.toString())==null ? true :  
							((Map<String,Boolean>) map.get(Paths.USERDATA_SEE_QUEST))
							.get(state.toString());
				else
					canSeeQuestState[state.ordinal()] = true;
			} catch (Exception e) {
				e.printStackTrace();
				canSeeQuestState[state.ordinal()] = true;
			}
			try {
				if (map.get(Paths.USERDATA_SEE_MISSION)!=null && (map.get(Paths.USERDATA_SEE_MISSION) instanceof Map<?,?>))
					canSeeMissionState[state.ordinal()] = 
					((Map<String,Boolean>) map.get(Paths.USERDATA_SEE_MISSION))
						.get(state.toString())==null ? true :  
							((Map<String,Boolean>) map.get(Paths.USERDATA_SEE_MISSION))
							.get(state.toString()); 
				else
					canSeeMissionState[state.ordinal()] = true;
			}catch (Exception e) {
				e.printStackTrace();
				canSeeMissionState[state.ordinal()] = true;
			}
		}
		List<UserQuestData> list = (List<UserQuestData>) map.get(Paths.USERDATA_QUEST_DATA_LIST);
		for (UserQuestData data: list) {
			data.setParent(this);
			questDatas.put(data.getKey(),data);
		}
	}

	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		//map.put(Paths.KEY, key);
		if (questPoints != 0)
			map.put(Paths.USERDATA_QUEST_POINTS, questPoints);
		LinkedHashMap<String,Boolean> canSeeQuest = new LinkedHashMap<>();
		LinkedHashMap<String,Boolean> canSeeMission = new LinkedHashMap<>();
		for (DisplayState state:DisplayState.values()) {
			if (canSeeQuestState[state.ordinal()]==false)
				canSeeQuest.put(state.toString(),false);
			if (canSeeMissionState[state.ordinal()]==false)
				canSeeMission.put(state.toString(),false);
		}
		if (canSeeQuest.size()>0)
			map.put(Paths.USERDATA_SEE_QUEST,canSeeQuest);
		if (canSeeMission.size()>0)
			map.put(Paths.USERDATA_SEE_MISSION,canSeeMission);
		
		List<UserQuestData> list = new ArrayList<>();
		for (Quest<T> quest:getQuestManager().getQuestContainer().getQuests()) {
			UserQuestData data = questDatas.get(quest.getKey());
			if (data!=null && data.serialize().size()>1)
				list.add(data);
		}
		if (!list.isEmpty())
			map.put(Paths.USERDATA_QUEST_DATA_LIST,list);
		return map;
	}

	/**
	 * 
	 * @return an unique id of this
	 *//*
	public String getUID() {
		return key;
	}*/

	/**
	 * 
	 */
	public int getQuestsPoints() {
		return questPoints;
	}

	public void setQuestsPoints(int value) {
		questPoints = value;
	}

	public void addQuestsPoints(int value) {
		setQuestsPoints(getQuestsPoints() + value);
	}

	public int getMissionsPoints(Quest<T> quest) {
		return getQuestData(quest).getMissionsPoints();
	}

	public void setMissionsPoints(int value, Quest<T> quest) {
		getQuestData(quest).setMissionsPoints(value);
	}

	public void addMissionsPoints(int value, Quest<T> quest) {
		setMissionsPoints(getMissionsPoints(quest) + value, quest);
	}

	private boolean[] canSeeQuestState = new boolean[DisplayState.values().length];
	private boolean[] canSeeMissionState = new boolean[DisplayState.values().length];

	public boolean canSeeQuestState(DisplayState state) {
		return canSeeQuestState[state.ordinal()];
	}

	public boolean canSeeMissionState(DisplayState state) {
		return canSeeMissionState[state.ordinal()];
	}

	public void toggleCanSeeQuestState(DisplayState state) {
		canSeeQuestState[state.ordinal()] = !canSeeQuestState[state.ordinal()];
	}

	public void toggleCanSeeMissionState(DisplayState state) {
		canSeeMissionState[state.ordinal()] = !canSeeMissionState[state.ordinal()];
	}

	private Map<String, UserQuestData> questDatas = new HashMap<>();

	public UserQuestData getQuestData(Quest<T> quest) {
		if (questDatas.containsKey(quest.getKey()))
			return questDatas.get(quest.getKey());
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.KEY,quest.getKey());
		UserQuestData questData = new UserQuestData(map);
		questData.setParent(this);
		questDatas.put(questData.getKey(),questData);
		return questData;
	}
	
	public void addQuestData(UserQuestData questData) {
		questData.setParent(this);
		questDatas.put(questData.getKey(),questData);
	}

	public void removeQuestData(String key) {
		questDatas.remove(key);
	}

	public UserMissionData getMissionData(Mission<T> mission) {
		return getQuestData(mission.getParent()).getMissionData(mission.getKey());
	}

	public UserTaskData getTaskData(Task<T> task) {
		return getMissionData(task.getParent()).getTaskData(task.getKey());
	}
	
	public void startCheck() {
		//TODO
		//force load all data
		for (Quest<T> quest:getQuestManager().getQuestContainer().getQuests()) {
			for (Mission<T> mission:quest.getMissions()) {
				
				UserMissionData missionData = getMissionData(mission);
				if (missionData.isStarted()) {
					for (Task<T> task:mission.getTasks()) {
						UserTaskData taskData = getTaskData(task);
						if (taskData.isCompleted())
						    register(task);
					}
				}
				else
					for (Task<T> task:mission.getTasks()) {
						getTaskData(task);
					}
			}
		}
		
	}
	
	public void register(Mission<T> mission) {
		for(Task<T> task:mission.getTasks())
			register(task);
	}
	public boolean register(Task<T> task) {
		if(!getTaskData(task).isCompleted())
			return activeTasks.get(task.getType()).add(task);
		return false;
	}
	public void unregister(Quest<?> quest) {
		for (Mission<?> mission:quest.getMissions())
			unregister(mission);
	}
	public void unregister(Mission<?> mission) {
		for(Task<?> task:mission.getTasks())
			unregister(task);
	}
	public boolean unregister(Task<?> task) {
		return activeTasks.get(task.getType()).remove(task);
	}


	private HashMap<TaskType<T>,List<Task<T>>> activeTasks = new HashMap<>();
	/**
	 * 
	 * @param type
	 * @return
	 */
	public List<Task<T>> getActiveTasks(TaskType<T> type){
		List<Task<T>> list = activeTasks.get(type);
		if (list==null) {
			list = new Vector<Task<T>>();
			activeTasks.put(type, list);
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(list);
	}

	public void resetMission(Mission<T> mission) {
		getMissionData(mission).reset();
	}

	public void resetQuest(Quest<T> quest) {
		getQuestData(quest).reset();
	}

	public boolean canSee(Player player, Quest<T> quest) {//TODO replace is OP with permission
		return quest.isWorldAllowed(player.getWorld()) && canSeeQuestState(getDisplayState(quest)) && (quest.isDeveloped() || player.isOp());
	}

	public boolean canSee(Player player, Mission<T> mission) {
		return canSeeMissionState(getDisplayState(mission));
	}

	public int getTaskProgress(Task<T> task) {
		if (task == null)
			throw new NullPointerException();
		return getTaskData(task).getProgress();
	}

	public boolean completeQuest(Quest<T> quest) {
		UserQuestData data = getQuestData(quest);
		Bukkit.getPluginManager().callEvent(new QuestCompleteEvent<T>(getParent(),quest));
		data.complete();
		return true;
	}
	public boolean failQuest(Quest<T> quest) {
		UserQuestData data = getQuestData(quest);
		Bukkit.getPluginManager().callEvent(new QuestFailEvent<T>(getParent(),quest));
		data.fail();
		return true;
	}
	/*public boolean startQuest(Quest<T> quest) {
		UserQuestData data = getQuestData(quest);
		Bukkit.getPluginManager().callEvent(new QuestStartEvent<T>((T) this,task));
	}*/
	public int progressTask(Task<T> task, int amount) {
		UserTaskData data = getTaskData(task);
		int limit = task.getMaxProgress()-data.getProgress();
		amount = Math.min(limit,amount);
		if (amount>0) {
			
			TaskProgressEvent<T> event = new TaskProgressEvent<T>(getParent(),task,amount,limit);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled() || event.getProgress()==0)
				return 0;
			amount = event.getProgress();
			Map<Reward<T>, Integer> rewards = event.getRewards();
			for (Reward<T> reward:rewards.keySet()) {
				int rewardAmount = rewards.get(reward);
				if (rewardAmount>0)
					reward.apply(getParent(),rewardAmount);
			}
			data.addProgress(amount);
		}
		if (data.isCompleted()) {
			unregister(task);
			Bukkit.getPluginManager().callEvent(new TaskCompleteEvent<T>(getParent(),task));
			
			checkMissionCompleted(task.getParent());
		}
		return amount;
	}
	
	public boolean checkMissionCompleted(Mission<T> mission) {
		UserMissionData missionData = getMissionData(mission);
		if (missionData.isCompleted()) {
			return true;
		}
		if (missionData.isFailed() || !missionData.isStarted()) {
			return false;
		}
		boolean completedMission = true;
		for(Task<T> task:mission.getTasks()) {
			if (!missionData.getTaskData(task.getKey()).isCompleted()) {
				completedMission = false;
				break;
			}
		}
		if (completedMission)
			completeMission(mission);
		return completedMission;
	}
	
	public boolean completeMission(Mission<T> mission) {
		UserMissionData missionData = getMissionData(mission);
		if (missionData.isCompleted())
			return false;
		Bukkit.getPluginManager().callEvent(
				new MissionCompleteEvent<T>(getParent(),mission));
		missionData.complete();
		return true;
	}
	public boolean failMission(Mission<T> mission) {
		UserMissionData missionData = getMissionData(mission);
		if (missionData.isFailed())
			return false;
		Bukkit.getPluginManager().callEvent(
				new MissionFailEvent<T>(getParent(),mission));
		missionData.fail();
		return true;
	}
	public boolean startMission(Mission<T> mission) {
		UserMissionData missionData = getMissionData(mission);
		missionData.start();
		MissionStartEvent<T> event = new MissionStartEvent<T>(getParent(),mission);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return false;
		missionData.start();
		return true;
	}

	/**
	 * if Mission isStarted return ONPROGRESS<br>
	 * if Mission isOnCooldown return COOLDOWN<br>
	 * if Mission isCompleted return COMPLETED<br>
	 * if Mission isFailed return FAILED<br>
	 * if user satisfy requires for Mission return UNSTARTED<br>
	 * else return LOCKED
	 * 
	 * @param mission
	 * @return
	 */
	public DisplayState getDisplayState(Mission<T> mission) {
		UserMissionData data = getMissionData(mission);
		if (data.isStarted())
			return DisplayState.ONPROGRESS;
		if (data.isOnCooldown())
			return DisplayState.COOLDOWN;
		if (mission.isRepeatable() && hasRequires(mission))
			return DisplayState.UNSTARTED;
		if (data.isCompleted())
			return DisplayState.COMPLETED;
		if (data.isFailed())
			return DisplayState.FAILED;
		return DisplayState.LOCKED;
	}

	/**
	 * 
	 * @param mission
	 * @return true if user satisfy all requires of mission
	 */
	public boolean hasRequires(Mission<T> mission) {
		for (Require<T> require : mission.getRequires()) {
			if (!require.isAllowed(getParent()))
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param quest
	 * @return in ordine di checks <br>
	 * 		se la Quest è dichiarata Completata ritorna COMPLETED <br>
	 * 		se la Quest è dichiarata Fallita ritorna FAILED <br>
	 * 		se la Quest è dichiarata Iniziata ritorna ONPROGRESS <br>
	 * 		se la Quest è in cooldown ritorna COOLDOWN <br>
	 * 		se user non soddisfa le require la quest è LOCKED <br>
	 * 		se la Quest non ha missioni la quest è UNSTARTED <br>
	 * 		se la Quest ha missioni in avanzamento (ONPROGRESS) è ONPROGRESS <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni in
	 *         attesa (COOLDOWN) è in COOLDOWN <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni
	 *         bloccate (LOCKED) è ONPROGRESS <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni
	 *         completate (COMPLETED) è COMPLETED <br>
	 * 		se la Quest non ha missioni non iniziate (UNSTARTED) è FAILED <br>
	 * 		se la Quest ha missioni in attesa o completate o fallite è UNSTARTED
	 *         <br>
	 * 		altrimenti è ONPROGRESS
	 */
	public DisplayState getDisplayState(Quest<T> quest) {
		UserQuestData data = getQuestData(quest);
		if (data.isCompleted())
			return DisplayState.COMPLETED;
		if (data.isFailed())
			return DisplayState.FAILED;
		if (data.isStarted())
			return DisplayState.ONPROGRESS;
		if (data.isOnCooldown())
			return DisplayState.COOLDOWN;
		if (!hasRequires(quest))
			return DisplayState.LOCKED;
		if (quest.getMissions().size() == 0)
			return DisplayState.UNSTARTED;

		EnumMap<DisplayState, Integer> values = getMissionsStates(quest);

		if (values.get(DisplayState.ONPROGRESS) > 0)
			return DisplayState.ONPROGRESS;

		// ONPROGRESS == 0
		if (values.get(DisplayState.UNSTARTED) == 0) {
			// onprogress ==0 && unstarted == 0
			if (values.get(DisplayState.COOLDOWN) > 0)
				return DisplayState.COOLDOWN;
			// onprogress ==0 && unstarted == 0 && cooldown==0
			// completed,failed,locked
			if (values.get(DisplayState.LOCKED) > 0)
				return DisplayState.ONPROGRESS;
			if (values.get(DisplayState.COMPLETED) > 0)
				return DisplayState.COMPLETED;
			return DisplayState.FAILED;
		}

		// ONPROGRESS ==0 && UNSTARTED >0
		if (values.get(DisplayState.COOLDOWN) == 0 && values.get(DisplayState.COMPLETED) == 0
				&& values.get(DisplayState.FAILED) == 0)
			return DisplayState.UNSTARTED;

		return DisplayState.ONPROGRESS;
	}

	/**
	 * 
	 * @param quest
	 * @return true if user satisfy all requires of quest
	 */
	public boolean hasRequires(Quest<T> quest) {
		for (Require<T> require : quest.getRequires()) {
			if (!require.isAllowed(getParent()))
				return false;
		}
		return true;
	}

	public EnumMap<DisplayState, Integer> getMissionsStates(Quest<T> quest) {
		EnumMap<DisplayState, Integer> values = new EnumMap<DisplayState, Integer>(DisplayState.class);
		for (DisplayState state : DisplayState.values())
			values.put(state, 0);
		for (Mission<T> mission : quest.getMissions()) {
			DisplayState missionState = getDisplayState(mission);
			values.put(missionState, values.get(missionState) + 1);
		}
		return values;
	}

	public QuestManager<T> getQuestManager() {
		return getParent().getQuestManager();
	}

	private T parent = null;

	public T getParent() {
		return parent;
	}

	public void setParent(T user) {
		if (parent != null)
			throw new IllegalStateException();
		parent = user;
	}
	
	

}