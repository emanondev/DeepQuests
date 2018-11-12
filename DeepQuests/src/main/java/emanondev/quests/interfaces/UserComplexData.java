package emanondev.quests.interfaces;

import java.util.Date;
import java.util.Map;

public abstract class UserComplexData extends UserQuestComponentData{
	
	public UserComplexData(Map<String,Object> map) {
		super(map);
		lastStarted = map.get(Paths.USERDATA_LAST_STARTED) == null ? 0 : Math.max(0,(long) map.get(Paths.USERDATA_LAST_STARTED));
		//isStarted = map.get(Paths.USERDATA_IS_STARTED) == null ? false : (boolean) map.get(Paths.USERDATA_IS_STARTED);
		lastCompleted = map.get(Paths.USERDATA_LAST_COMPLETED) == null ? 0 : Math.max(0,(long) map.get(Paths.USERDATA_LAST_COMPLETED));
		completedTimes = map.get(Paths.USERDATA_COMPLETED_TIMES) == null ? 0 : Math.max(0,(int) map.get(Paths.USERDATA_COMPLETED_TIMES));
		lastFailed = map.get(Paths.USERDATA_FAILED_TIMES) == null ? 0 : Math.max(0,(long) map.get(Paths.USERDATA_FAILED_TIMES));
		//isFailed = map.get(Paths.USERDATA_FAILED) == null ? false : (boolean) map.get(Paths.USERDATA_IS_FAILED);
		failedTimes = map.get(Paths.USERDATA_LAST_FAILED) == null ? 0 : Math.max(0,(int) map.get(Paths.USERDATA_LAST_FAILED));
		//isCompleted = map.get(Paths.USERDATA_IS_COMPLETED) == null ? false : (boolean) map.get(Paths.USERDATA_IS_COMPLETED);
	}
	
	public Map<String,Object> serialize(){
		Map<String, Object> map = super.serialize();
		if (lastStarted!=0)
			map.put(Paths.USERDATA_LAST_STARTED,lastStarted);
		//if (isStarted!=false)
		//	map.put(Paths.USERDATA_IS_STARTED,isStarted);
		if (lastCompleted!=0)
			map.put(Paths.USERDATA_LAST_COMPLETED,lastCompleted);
		if (completedTimes!=0)
			map.put(Paths.USERDATA_COMPLETED_TIMES,completedTimes);
		//if (isFailed!=false)
		//	map.put(Paths.USERDATA_IS_FAILED,isFailed);
		if (failedTimes!=0)
			map.put(Paths.USERDATA_FAILED_TIMES,failedTimes);
		if (lastFailed!=0)
			map.put(Paths.USERDATA_LAST_FAILED,lastFailed);
		//if (isCompleted!=false)
		//	map.put(Paths.USERDATA_IS_COMPLETED,isCompleted);
		return map;
	}
	
	private long lastStarted;
	//private boolean isStarted;
	
	private long lastCompleted;
	//private boolean isCompleted;
	private int completedTimes;

	private long lastFailed;
	//private boolean isFailed;
	private int failedTimes;
	

	public boolean isFailed() {
		//return isFailed;
		if (lastFailed>lastCompleted && lastFailed>lastStarted)
			return true;
		return false;
	}
	public long getLastStarted() {
		return lastStarted;
	}
	public long getLastCompleted() {
		return lastCompleted;
	}
	public long getLastFailed() {
		return lastFailed;
	}
	public boolean hasStartedAtLeastOnce() {
		return lastStarted > 0;
	}
	public boolean hasCompletedAtLeastOnce() {
		return lastCompleted > 0;
	}
	public boolean hasFailedAtLeastOnce() {
		return lastFailed > 0;
	}
	public int successfullyCompletedTimes() {
		return completedTimes;
	}
	public int failedTimes() {
		return failedTimes;
	}
	public boolean isStarted() {
		if (lastStarted>=lastCompleted && lastStarted>=lastFailed && lastStarted>0)
			return true;
		return false;
	}
	public boolean isCompleted() {
		if (lastCompleted>lastStarted && lastCompleted>lastFailed)
			return true;
		return false;
	}
	
	public void complete() {
		/*isFailed = false;
		isStarted = false;
		isCompleted = true;*/
		completedTimes++;
		lastCompleted = new Date().getTime();
	}
	public void fail() {
		/*isFailed = true;
		isStarted = false;
		isCompleted = false;*/
		failedTimes++;
		lastFailed = new Date().getTime();
	}
	public void start() {
		/*isStarted = true;
		isFailed = false;
		isCompleted = false;*/
		lastStarted = new Date().getTime();
	}
	/*public void reset() {
		isStarted = false;
		isFailed = false;
		isCompleted = false;
	}*/
	
	/*
	protected void setLastStarted(long time) {
		lastStarted = time;
	}
	
	protected void setFailed(boolean value) {
		isFailed = value;
	}
	
	protected void setCompleted(boolean value) {
		isCompleted = value;
	}
	
	protected void setLastCompleted(long time) {
		lastCompleted = time;
	}
	
	protected void setLastFailed(long time) {
		lastFailed = time;
	}
	
	protected void setFailedTimes(int times) {
		failedTimes = times;
	}
	
	protected void setCompletedTimes(int times) {
		completedTimes = times;
	}*/
	protected abstract QuestComponentWithCooldown<?> getQuestComponent();
	
	public long getCooldownTimeLeft() {
		return Math.max(lastCompleted,lastFailed)+getQuestComponent().getCooldownTime()-new Date().getTime();
	}
	public boolean isOnCooldown() {
		return getQuestComponent().isRepeatable() && getCooldownTimeLeft()>0;
	}

}