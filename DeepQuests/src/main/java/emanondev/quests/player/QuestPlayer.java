package emanondev.quests.player;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Perms;
import emanondev.quests.events.PlayerCompleteMissionEvent;
import emanondev.quests.events.PlayerCompleteTaskEvent;
import emanondev.quests.events.PlayerFailMissionEvent;
import emanondev.quests.events.PlayerProgressTaskEvent;
import emanondev.quests.events.PlayerStartMissionEvent;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData.TaskData;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.require.QuestRequire;
import emanondev.quests.reward.MissionReward;
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
		if (data.isOnCooldown()&&mission.isRepetable())
			return DisplayState.COOLDOWN;
		if (data.hasCompleted()&&!mission.isRepetable())
			return DisplayState.COMPLETED;
		if (hasRequires(mission))
			return DisplayState.UNSTARTED;
		return DisplayState.LOCKED;
	}
	public DisplayState getDisplayState(Quest quest) {
		QuestData data = getQuestData(quest);
		if (data.isFailed())
			return DisplayState.FAILED;
		if (data.isStarted())
			return DisplayState.ONPROGRESS;
		if (data.isOnCooldown()&&quest.isRepetable())
			return DisplayState.COOLDOWN;
		if (data.hasCompleted()&&!quest.isRepetable())
			return DisplayState.COMPLETED;
		if (hasRequires(quest))
			return DisplayState.UNSTARTED;
		return DisplayState.LOCKED;
	}
	public ItemStack getGuiItem(Mission mission, boolean bypassHidden) {
		DisplayState state = getDisplayState(mission);
		if (!bypassHidden && mission.getDisplayInfo().isHidden(state))
			return null;
		return mission.getDisplayInfo().getGuiItem(getPlayer(), state);
	}
	public ItemStack getGuiItem(Quest quest, boolean bypassHidden) {
		DisplayState state = getDisplayState(quest);
		if (!bypassHidden && quest.getDisplayInfo().isHidden(state))
			return null;
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
		for (MissionRequire req: mission.getRequires())
			if (!req.isAllowed(this))
				return false;
		return true;
	}
	private boolean hasRequires(Quest quest) {
		for (QuestRequire req: quest.getRequires())
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
		for (MissionReward reward : event.getRewards())
			reward.applyReward(this,mission);
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
		for (MissionReward reward : event.getRewards())
			reward.applyReward(this,mission);
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
		if (getPlayer().hasPermission(Perms.GUI_SEE_ALL))
			return true;
		return !quest.getDisplayInfo().isHidden(getDisplayState(quest));
	}
	public boolean canSee(Mission mission) {
		if (getPlayer().hasPermission(Perms.GUI_SEE_ALL))
			return true;
		return !mission.getDisplayInfo().isHidden(getDisplayState(mission));
	}
	
}
