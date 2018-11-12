package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserQuestData extends UserComplexData {
	private UserData<?> parent = null;
	
	@SuppressWarnings("unchecked")
	public UserQuestData(Map<String, Object> map) {
		super(map);
		missionPoints = map.get(Paths.USERDATA_MISSION_POINTS) == null ? 0 : (int) map.get(Paths.USERDATA_MISSION_POINTS);
		List<UserMissionData> list;
		try{
			list = (List<UserMissionData>) map.get(Paths.USERDATA_MISSION_DATA_LIST);
		}catch (Error e) {
			e.printStackTrace();
			list = new ArrayList<>();
		}
		for (UserMissionData missionData:list) {
			if (missionDatas.put(missionData.getKey(),missionData)!=null);
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			missionData.setParent(this);
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String, Object> map = super.serialize();
		if (missionPoints!=0)
			map.put(Paths.USERDATA_MISSION_POINTS,missionPoints);
		List<UserMissionData> list = new ArrayList<>();
		for (Mission<?> mission:getQuest().getMissions()) {
			UserMissionData data = missionDatas.get(mission.getKey());
			if (data!=null && data.serialize().size()>1)
				list.add(data);
		}
		if (!list.isEmpty())
			map.put(Paths.USERDATA_MISSION_DATA_LIST,list);
		return map;
	}

	private int missionPoints;
	private Map<String,UserMissionData> missionDatas = new HashMap<>();
	
	
	public int getMissionsPoints() {
		return missionPoints;
	}
	public void setMissionsPoints(int amount) {
		missionPoints = amount;
	}
	public UserMissionData getMissionData(String key) {
		if (missionDatas.containsKey(key))
			return missionDatas.get(key);
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.KEY,key);
		UserMissionData missionData = new UserMissionData(map);
		missionData.setParent(this);
		missionDatas.put(missionData.getKey(),missionData);
		return missionData;
	}
	public void addMissionData(UserMissionData data) {
		data.setParent(this);
		missionDatas.put(data.getKey(),data);
	}
	public void removeMissionData(String key) {
		missionDatas.remove(key);
	}
	
	public Quest<?> getQuest(){
		return getParent().getQuestManager().getQuestContainer().getQuest(key);
	}
	
	protected Quest<?> getQuestComponent(){
		return getQuest();
	}

	public UserData<?> getParent() {
		return parent;
	}
	public void setParent(UserData<?> userData) {
		if (parent != null)
			throw new IllegalStateException();
		parent = userData;
	}

	@Override
	public void erase() {
		getParent().removeQuestData(this.getKey());
	}
	
	@Override
	public void reset() {
		//super.reset();
		for (UserMissionData missionData:missionDatas.values()) {
			missionData.reset();
		}
	}
	

}
