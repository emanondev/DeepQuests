package emanondev.quests.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		if (data.isOnCooldown())
			return DisplayState.COOLDOWN;
		if (data.hasCompleted()&&mission.getCooldownTime()<0)
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
		if (data.isOnCooldown())
			return DisplayState.COOLDOWN;
		if (data.hasCompleted()&&quest.getCooldownTime()<0)
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
		return getQuestData(quest).cooldownTimeLeft();
	}
	public void startMission(Mission m) {
		startMission(m,false);
	}
	private boolean hasRequires(Mission m) {
		for (MissionRequire req: m.getRequires())
			if (req.isAllowed(this, m))
				return false;
		return true;
	}
	private boolean hasRequires(Quest q) {
		for (QuestRequire req: q.getRequires())
			if (req.isAllowed(this, q))
				return false;
		return true;
	}
	public void startMission(Mission m,boolean forced) {
		//TODO mission limit
		if (!forced && !hasRequires(m)) {
			return;
		}
		
		PlayerStartMissionEvent event = new PlayerStartMissionEvent(this,m);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		
		for (MissionReward reward : event.getRewards())
			reward.applyReward(this,m);
		MissionData mData = this.getMissionData(m);
		mData.start();
		
	}
	public void failMission(Mission m) {
		DisplayState state = getDisplayState(m);
		switch (state){
		case COMPLETED:
		case FAILED:
			return;
		default:
		}
		PlayerFailMissionEvent event = new PlayerFailMissionEvent(this,m);
		Bukkit.getPluginManager().callEvent(event);
		getMissionData(m).fail();
	}
	public void togglePauseMission(Mission m) {
		if (getDisplayState(m)!=DisplayState.ONPROGRESS)
			return;
		MissionData missionData = getMissionData(m);
		missionData.setPaused(!missionData.isPaused());
	}
	public void completeMission(Mission m) {

		PlayerCompleteMissionEvent event = new PlayerCompleteMissionEvent(this,m);
		Bukkit.getPluginManager().callEvent(event);
		
		for (MissionReward reward : event.getRewards())
			reward.applyReward(this,m);
		MissionData mData = this.getMissionData(m);
		mData.complete();
	}
	public void progressTask(Task t,int amount) {
		TaskData tData = getTaskData(t);
		amount = Math.min(amount,t.getMaxProgress()-tData.getProgress());
		if (amount <=0)
			return;
		PlayerProgressTaskEvent event = new PlayerProgressTaskEvent(this,t,amount);
		Bukkit.getPluginManager().callEvent(event);
		if (event.getProgressAmount() <=0)
			return;
		tData.setProgress(tData.getProgress()+event.getProgressAmount());
		for (Reward rew : event.getRewards())
			for (int i = 0 ; i < event.getProgressAmount() ; i++)
				rew.applyReward(this);
		if (t.getMaxProgress()<=tData.getProgress())
			completeTask(t);
	}
	public void completeTask(Task t) {
		PlayerCompleteTaskEvent event = new PlayerCompleteTaskEvent(this,t);
		Bukkit.getPluginManager().callEvent(event);
		boolean completed = true;
		for (TaskData tData : getMissionData(t.getParent()).getTasksData()) {
			if (!tData.isCompleted()) {
				completed = false;
				break;
			}
		}
		if (completed==true) {
			completeMission(t.getParent());
		}
	}
	
}
