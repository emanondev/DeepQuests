package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import emanondev.quests.interfaces.Paths;

public class ExperienceData extends QuestComponentData {
	
	private long exp = 1L;
	
	public ExperienceData(Map<String,Object> map) {
		if (map == null)
			return;
		try {
			exp = (long) map.getOrDefault(Paths.DATA_EXPERIENCE, 1L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_EXPERIENCE,exp);
		return map;
	}
	
	public long getExperience() {
		return exp;
	}
	
	public boolean setExperience(long exp) {
		exp = Math.max(1L,exp);
		if (exp == this.exp)
			return false;
		this.exp = exp;
		return true;
	}
}
