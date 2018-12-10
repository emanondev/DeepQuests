package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Quest;

public class TargetQuestData extends QuestComponentData {
	
	private String questKey = null;
	
	public TargetQuestData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			questKey = (String) map.getOrDefault(Paths.DATA_TARGET_QUEST_KEY, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (questKey!=null)
			map.put(Paths.DATA_TARGET_QUEST_KEY,questKey);
		return map;
	}
	
	public Quest<?> getQuest(){
		if (questKey==null)
			return null;
		return getQuestManager().getQuestContainer().getQuest(questKey);
	}
	

}
