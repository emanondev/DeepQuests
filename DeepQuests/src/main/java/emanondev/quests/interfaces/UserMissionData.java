package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserMissionData extends UserComplexData {

	private UserQuestData parent = null;

	@SuppressWarnings("unchecked")
	public UserMissionData(Map<String, Object> map) {
		super(map);
		List<UserTaskData> list;
		try {
			list = (List<UserTaskData>) map.get(Paths.USERDATA_TASK_DATA_LIST);
		} catch (Error e) {
			e.printStackTrace();
			list = new ArrayList<>();
		}
		for (UserTaskData taskData : list) {
			if (taskDatas.put(taskData.getKey(), taskData) != null)
				;
			new IllegalArgumentException("Not Unique key found").printStackTrace();
			taskData.setParent(this);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		List<UserTaskData> list = new ArrayList<>();
		for (Task<?> task : getMission().getTasks()) {
			UserTaskData data = taskDatas.get(task.getKey());
			if (data != null && data.serialize().size() > 1)
				list.add(data);
		}
		if (!list.isEmpty())
			map.put(Paths.USERDATA_MISSION_DATA_LIST, list);
		return map;
	}

	private Map<String, UserTaskData> taskDatas = new HashMap<>();

	public UserTaskData getTaskData(String key) {
		if (taskDatas.containsKey(key))
			return taskDatas.get(key);
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(Paths.KEY, key);
		UserTaskData taskData = new UserTaskData(map);
		taskData.setParent(this);
		taskDatas.put(taskData.getKey(), taskData);
		return taskData;
	}

	public void addTaskData(UserTaskData data) {
		data.setParent(this);
		taskDatas.put(data.getKey(), data);
	}

	public void removeTaskData(String key) {
		taskDatas.remove(key);
	}

	public void setParent(UserQuestData questData) {
		if (parent != null)
			throw new IllegalStateException();
		parent = questData;
	}

	public UserQuestData getParent() {
		return parent;
	}

	public Mission<?> getMission() {
		return getParent().getQuest().getMission(getKey());
	}

	@Override
	protected Mission<?> getQuestComponent() {
		return getMission();
	}

	@Override
	public void erase() {
		getParent().removeMissionData(this.getKey());
	}

	@Override
	public void start() {
		super.start();
		reset();
	}

	public void reset() {
		//super.reset();
		Mission<?> mission = getMission();
		for (Task<?> task : mission.getTasks())
			getTaskData(task.getKey()).reset();
	}
}
