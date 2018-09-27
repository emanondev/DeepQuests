package emanondev.quests.player;

import java.util.ArrayList;
import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.events.PlayerCompleteMissionEvent;
import emanondev.quests.events.PlayerCompleteTaskEvent;
import emanondev.quests.events.PlayerFailMissionEvent;
import emanondev.quests.events.PlayerProgressTaskEvent;
import emanondev.quests.events.PlayerStartMissionEvent;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData.TaskData;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.Require;
import emanondev.quests.reward.Reward;
import emanondev.quests.task.Task;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.StringUtils;

public class QuestPlayer extends OfflineQuestPlayer{
	
	public QuestPlayer(Player p) {
		super(p);
	}
	
	public Player getPlayer() {
		return (Player) super.getPlayer();
	}
	public int getTaskProgress(Task task) {
		if (task == null)
			throw new NullPointerException();
		return getTaskData(task).getProgress();
	}
	public DisplayState getDisplayState(Mission mission) {
		MissionData data = getMissionData(mission);
		if (data.isFailed())
			return DisplayState.FAILED;
		if (data.isStarted())
			return DisplayState.ONPROGRESS;
		if (data.isOnCooldown()&&mission.getCooldownData().isRepetable())
			return DisplayState.COOLDOWN;
		if (data.hasCompleted()&&!mission.getCooldownData().isRepetable())
			return DisplayState.COMPLETED;
		if (hasRequires(mission))
			return DisplayState.UNSTARTED;
		return DisplayState.LOCKED;
	}
	/**
	 * 
	 * @param quest
	 * @return in ordine di checks
	 * <br>se la Quest è dichiarata Fallita ritorna FAILED
	 * <br>se QuestPlayer non soddisfa le require la quest è LOCKED
	 * <br>se la Quest non ha missioni la quest è UNSTARTED
	 * <br>se la Quest ha missioni in avanzamento (ONPROGRESS) è ONPROGRESS
	 * <br>se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni in attesa (COOLDOWN) è in COOLDOWN
	 * <br>se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni bloccate (LOCKED) è ONPROGRESS
	 * <br>se la Quest non ha missioni non iniziate (UNSTARTED) e ha missioni completate (COMPLETED) è COMPLETED
	 * <br>se la Quest non ha missioni non iniziate (UNSTARTED) è FAILED
	 * <br>se la Quest ha missioni in attesa o completate o fallite è UNSTARTED
	 * <br>altrimenti è ONPROGRESS 
	 */
	public DisplayState getDisplayState(Quest quest) {
		QuestData data = getQuestData(quest);
		if (data.isFailed())
			return DisplayState.FAILED;
		if (!hasRequires(quest))
			return DisplayState.LOCKED;
		if (quest.getMissions().size()==0)
			return DisplayState.UNSTARTED;
		
		EnumMap<DisplayState,Integer> values = getMissionsStates(quest);
		
		if (values.get(DisplayState.ONPROGRESS)>0)
			return DisplayState.ONPROGRESS;
		
		//ONPROGRESS == 0
		if (values.get(DisplayState.UNSTARTED)==0) {
			//onprogress ==0 && unstarted == 0
			if (values.get(DisplayState.COOLDOWN)>0)
				return DisplayState.COOLDOWN;
			//onprogress ==0 && unstarted == 0 && cooldown==0
			//completed,failed,locked
			if (values.get(DisplayState.LOCKED)>0)
				return DisplayState.ONPROGRESS;
			if (values.get(DisplayState.COMPLETED)>0)
				return DisplayState.COMPLETED;
			return DisplayState.FAILED;
		}
		//ONPROGRESS == 0 && UNSTARTED >0
		if (values.get(DisplayState.COOLDOWN)==0
				&&values.get(DisplayState.COMPLETED)==0
				&&values.get(DisplayState.FAILED)==0)
			return DisplayState.UNSTARTED;
		
		return DisplayState.ONPROGRESS;
	}
	public EnumMap<DisplayState,Integer> getMissionsStates(Quest quest){
		EnumMap<DisplayState,Integer> values = new EnumMap<DisplayState,Integer>(DisplayState.class);
		for (DisplayState state: DisplayState.values())
			values.put(state,0);
		for(Mission mission:quest.getMissions()) {
			DisplayState missionState = getDisplayState(mission);
			values.put(missionState,values.get(missionState)+1);
		}
		return values;
	}
	
	
	public ItemStack getGuiItem(Mission mission) {
		DisplayState state = getDisplayState(mission);
		return mission.getDisplayInfo().getGuiItem(getPlayer(), state);
	}
	
	public ItemStack getGuiItem(Quest quest) {
		DisplayState state = getDisplayState(quest);
		return quest.getDisplayInfo().getGuiItem(getPlayer(), state);
	}
	public long getCooldown(Quest quest) {
		return getQuestData(quest).getCooldownTimeLeft();
	}
	public long getCooldown(Mission mission) {
		return getMissionData(mission).getCooldownTimeLeft();
	}
	public void startMission(Mission mission) {
		startMission(mission,false);
	}
	private boolean hasRequires(Mission mission) {
		for (Require req: mission.getRequires())
			if (!req.isAllowed(this))
				return false;
		return true;
	}
	private boolean hasRequires(Quest quest) {
		if (!quest.isWorldAllowed(getPlayer().getWorld()))
			return false;
		for (Require req: quest.getRequires())
			if (!req.isAllowed(this))
				return false;
		return true;
	}
	public void startMission(Mission mission,boolean forced) {
		//TODO mission limit
		if (!forced && !hasRequires(mission)) {
			return;
		}
		
		PlayerStartMissionEvent event = new PlayerStartMissionEvent(this,mission);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		
		MissionData missionData = this.getMissionData(mission);
		missionData.start();
		for (Reward reward : event.getRewards())
			reward.applyReward(this);
		ArrayList<String> mex = StringUtils.convertList(getPlayer(),mission.getStartMessage());
		if (mex!=null)
			for (String text:mex)
				getPlayer().sendMessage(text);
		
	}
	public void failMission(Mission mission) {
		DisplayState state = getDisplayState(mission);
		switch (state){
		case COMPLETED:
		case FAILED:
			return;
		default:
		}
		PlayerFailMissionEvent event = new PlayerFailMissionEvent(this,mission);
		Bukkit.getPluginManager().callEvent(event);
		getMissionData(mission).fail();
		ArrayList<String> mex = StringUtils.convertList(getPlayer(),mission.getFailMessage());
		if (mex!=null)
			for (String text:mex)
				getPlayer().sendMessage(text);
	}
	public void togglePauseMission(Mission mission) {//TODO check permission
		if (getDisplayState(mission)!=DisplayState.ONPROGRESS)
			return;
		MissionData missionData = getMissionData(mission);
		
		if (missionData.isPaused()) {
			ArrayList<String> mex = StringUtils.convertList(getPlayer(),mission.getUnpauseMessage());
			if (mex!=null)
				for (String text:mex)
					getPlayer().sendMessage(text);
		}
		else {
			ArrayList<String> mex = StringUtils.convertList(getPlayer(),mission.getPauseMessage());
			if (mex!=null)
				for (String text:mex)
					getPlayer().sendMessage(text);
		}
		missionData.setPaused(!missionData.isPaused());
	}
	public void completeMission(Mission mission) {

		PlayerCompleteMissionEvent event = new PlayerCompleteMissionEvent(this,mission);
		Bukkit.getPluginManager().callEvent(event);
		
		MissionData missionData = this.getMissionData(mission);
		missionData.complete();
		ArrayList<String> mex = StringUtils.convertList(getPlayer(),mission.getCompleteMessage());
		if (mex!=null)
			for (String text:mex)
				getPlayer().sendMessage(text);
		for (Reward reward : event.getRewards())
			reward.applyReward(this);
	}
	public boolean progressTask(Task task,int amount) {
		TaskData taskData = getTaskData(task);
		amount = Math.min(amount,task.getMaxProgress()-taskData.getProgress());
		if (amount <=0)
			return false;
		PlayerProgressTaskEvent event = new PlayerProgressTaskEvent(this,task,amount);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled() || event.getProgressAmount() <=0)
			return false;
		taskData.setProgress(taskData.getProgress()+event.getProgressAmount());
		Quests.get().getBossBarManager().onProgress(this,task);
		for (Reward rew : event.getRewards())
			for (int i = 0 ; i < event.getProgressAmount() ; i++)
				rew.applyReward(this);
		if (task.getMaxProgress()<=taskData.getProgress())
			completeTask(task);
		return true;
	}
	public void completeTask(Task task) {
		PlayerCompleteTaskEvent event = new PlayerCompleteTaskEvent(this,task);
		Bukkit.getPluginManager().callEvent(event);
		boolean completed = true;
		for (TaskData taskData : getMissionData(task.getParent()).getTasksData()) {
			if (!taskData.isCompleted()) {
				completed = false;
				break;
			}
		}
		if (completed==true) {
			completeMission(task.getParent());
		}
	}
	
	public boolean canSee(Quest quest) {
		if (!quest.isWorldAllowed(getPlayer().getWorld()))
			return false;
		DisplayState state = getDisplayState(quest);
		if (quest.getDisplayInfo().isHidden(state))
			return false;
		return this.canSeeQuestState(state);
	}
	public boolean canSee(Mission mission) {
		DisplayState state = getDisplayState(mission);
		if (mission.getDisplayInfo().isHidden(state))
			return false;
		return this.canSeeMissionState(state);
	}
	
}
