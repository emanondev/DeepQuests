package emanondev.quests.interfaces;

import java.util.Map;

public class UserTaskData extends UserQuestComponentData{
	private int progress;
	private UserMissionData parent = null;

	public UserTaskData(Map<String, Object> map) {
		super(map);
		progress = map.get(Paths.USERDATA_TASK_PROGRESS)==null ? 0 : Math.max(0,((int) map.get(Paths.USERDATA_TASK_PROGRESS)));  
	}
	
	public Map<String,Object> serialize(){
		Map<String,Object> map = super.serialize();
		if (progress!=0) 
			map.put(Paths.USERDATA_TASK_PROGRESS,progress);
		return map;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public int addProgress(int amount) {
		int old = progress;
		setProgress(progress+amount);
		return progress-old;
	}
	public void setProgress(int amount) {
		progress = Math.min(Math.max(0,amount),getTask().getMaxProgress());
	}
	
	public Task<?> getTask(){
		return getParent().getMission().getTask(getKey());
	}

	@Override
	public void reset() {
		progress = 0;
	}

	@Override
	public void erase() {
		getParent().removeTaskData(getKey());
	}

	@Override
	protected Task<?> getQuestComponent() {
		return getTask();
	}
	
	public UserMissionData getParent() {
		return parent;
	}
	public void setParent(UserMissionData missionData) {
		if (parent!=null)
			throw new IllegalStateException();
		parent = missionData;
	}
	public boolean isCompleted() {
		return getTask().getMaxProgress()<=progress;
	}

}
