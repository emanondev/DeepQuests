package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gmail.nossr50.datatypes.skills.SkillType;

import emanondev.quests.interfaces.Paths;

public class McMMOSkillTypeData extends QuestComponentData {
	
	private SkillType skillType = null;
	
	public McMMOSkillTypeData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			String skillTypeName = (String) map.getOrDefault(Paths.DATA_JOB_TYPE, null);
			if (skillTypeName!=null)
				skillType = SkillType.valueOf(skillTypeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (skillType !=null)
			map.put(Paths.DATA_JOB_TYPE,skillType.getName());
		return map;
	}
	
	public SkillType getSkillType() {
		return skillType;
	}
	
	public boolean setSkillType(SkillType skillType) {
		if (this.skillType==skillType)
			return false;
		if (this.skillType!=null && this.skillType.equals(skillType)) 
			return false;
		this.skillType = skillType;
		return true;
	}
}
