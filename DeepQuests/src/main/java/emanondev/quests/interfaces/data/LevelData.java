package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import emanondev.quests.interfaces.Paths;

public class LevelData extends QuestComponentData {
	
	private int level = 1;
	
	public LevelData(Map<String,Object> map) {
		if (map == null)
			return;
		try {
			level = Math.max(1,(int) map.getOrDefault(Paths.DATA_LEVEL, 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_LEVEL,level);
		return map;
	}
	
	public long getLevel() {
		return level;
	}
	
	public boolean setLevel(int level) {
		level = Math.max(1,level);
		if (level == this.level)
			return false;
		this.level = level;
		return true;
	}
}