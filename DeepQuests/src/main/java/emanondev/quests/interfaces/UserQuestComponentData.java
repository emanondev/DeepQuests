package emanondev.quests.interfaces;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class UserQuestComponentData implements ConfigurationSerializable{
	
	public final String key;
	
	public UserQuestComponentData(Map<String,Object> map) {
		key = (String) map.get(Paths.KEY);
		if (key==null || key.isEmpty())
			throw new NullPointerException("Invalid Key");
		
	}
	
	public String getKey() {
		return key;
	}
	
	public abstract void reset();
	public abstract void erase();
	
	public Map<String,Object> serialize(){
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.KEY, key);
		return map;
	}

	protected abstract QuestComponent<?> getQuestComponent();
	
	

}
