package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import emanondev.quests.interfaces.Mission;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Quest;

public class TargetMissionData extends QuestComponentData {
	
	private String missionKey = null;
	private String questKey = null;
	
	public TargetMissionData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			questKey = (String) map.getOrDefault(Paths.DATA_TARGET_QUEST_KEY, null);
			missionKey = (String) map.getOrDefault(Paths.DATA_TARGET_MISSION_KEY, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (questKey!=null)
			map.put(Paths.DATA_TARGET_QUEST_KEY,questKey);
		if (missionKey!=null)
			map.put(Paths.DATA_TARGET_MISSION_KEY,missionKey);
		return map;
	}
	
	public Mission<?> getMission(){
		if (missionKey==null || questKey==null)
			return null;
		Quest<?> quest = getQuestManager().getQuestContainer().getQuest(questKey);
		if (quest==null)
			return null;
		return quest.getMission(missionKey);
	}

}
