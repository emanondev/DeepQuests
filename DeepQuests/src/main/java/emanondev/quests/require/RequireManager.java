package emanondev.quests.require;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.WithGui;

public class RequireManager {
	private final static HashMap<String,RequireType> requiresType = 
			new HashMap<String,RequireType>();
	private final static HashMap<String,MissionRequireType> missionRequiresType =
			new HashMap<String,MissionRequireType>();
	private final static HashMap<String,QuestRequireType> questRequiresType =
			new HashMap<String,QuestRequireType>();
			

	public void registerRequireType(RequireType type) {
		requiresType.put(type.getKey(),type);
		missionRequiresType.put(type.getKey(),type);
		questRequiresType.put(type.getKey(),type);
	}
	public void registerMissionRequireType(MissionRequireType type) {
		missionRequiresType.put(type.getKey(),type);
	}
	public void registerQuestRequireType(QuestRequireType type) {
		questRequiresType.put(type.getKey(),type);
	}
	public LinkedHashMap<String,MissionRequire> loadRequires(Mission m,MemorySection section){
		LinkedHashMap<String,MissionRequire> requires = new LinkedHashMap<String,MissionRequire>();
		if (section!=null) {
			Set<String> keys = section.getKeys(false);
			if (keys!=null)
				keys.forEach((id)->{
					try {
						String key = section.getString(id+".type");
						if (key== null || !missionRequiresType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						MissionRequire rew = missionRequiresType.get(key.toUpperCase()).getRequireInstance((MemorySection) section.get(id), m);
						if (rew!=null)
							requires.put(rew.getNameID(),rew);
					} catch (Exception e) {
						
					}
				});
		}
		return requires;
	}
	public LinkedHashMap<String,QuestRequire> loadRequires(Quest q,MemorySection section){
		LinkedHashMap<String,QuestRequire> requires = new LinkedHashMap<String,QuestRequire>();
		if (section!=null) {
			Set<String> keys = section.getKeys(false);
			if (keys!=null)
				keys.forEach((id)->{
					try {
						String key = section.getString(id+".type");
						if (key== null || !questRequiresType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						QuestRequire rew = questRequiresType.get(key.toUpperCase()).getRequireInstance((MemorySection) section.get(id), q);
						if (rew!=null)
							requires.put(rew.getNameID(),rew);
					} catch (Exception e) {
						
					}
				});
		}
		return requires;
	}
	public LinkedHashMap<String,Require> loadRequires(WithGui gui,MemorySection section){
		LinkedHashMap<String,Require> requires  = new LinkedHashMap<String,Require>();
		if (section!=null) {
			Set<String> keys = section.getKeys(false);
			if (keys!=null)
				keys.forEach((id)->{
					try {
						String key = section.getString(id+".type");
						if (key== null || !requiresType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Require rew = requiresType.get(key.toUpperCase()).getRequireInstance((MemorySection) section.get(id), gui);
						if (rew!=null)
							requires.put(rew.getNameID(),rew);
					} catch (Exception e) {
						
					}
				});
		}
		return requires;
	}
	public boolean isAllowed(QuestPlayer p,List<Require> list) {
		if (list == null || list.isEmpty())
			return true;
		for (Require req : list) {
			if (!req.isAllowed(p))
				return false;
		}
		return true;
	}
	public Collection<QuestRequireType> getQuestRequiresTypes() {
		return Collections.unmodifiableCollection(questRequiresType.values());
	}
	public Collection<MissionRequireType> getMissionRequiresTypes() {
		return Collections.unmodifiableCollection(missionRequiresType.values());
	}
}
