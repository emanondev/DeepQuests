package emanondev.quests.interfaces.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import emanondev.quests.interfaces.Paths;
import net.citizensnpcs.api.npc.NPC;

public class NPCData extends QuestComponentData {
	
	private Set<Integer> ids = new HashSet<>();
	private boolean isListWhitelist = true;
	
	@SuppressWarnings("unchecked")
	public NPCData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			isListWhitelist = (boolean) map.getOrDefault(Paths.DATA_NPC_ID_LIST_IS_WHITELIST,true);
			List<Integer> idList = (List<Integer>) map.getOrDefault(Paths.DATA_NPC_ID_LIST, null);
			if (idList!=null)
				for (Integer value:idList)
					if (value!=null)
						ids.add(value);
				throw new IllegalArgumentException("Illegal command value");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_NPC_ID_LIST_IS_WHITELIST,isListWhitelist);
		if (!ids.isEmpty())
			map.put(Paths.DATA_NPC_ID_LIST,new ArrayList<Integer>(ids));
		return map;
	}
	public boolean toggleNPC(NPC npc) {
		if (npc==null)
			return false;
		return toggleNPC(npc.getId());
	}
	public boolean toggleNPC(int id) {
		if (id<0)
			return false;
		if (ids.contains(id))
			ids.remove(id);
		else
			ids.add(id);
		return true;
	}
	public boolean setWhitelist(boolean value) {
		if (value==isListWhitelist)
			return false;
		isListWhitelist = value;
		return true;
	}

	public boolean isValidNPC(NPC npc) {
		if (npc == null)
			return false;
		if (isListWhitelist)
			return ids.contains(npc.getId());
		return !ids.contains(npc.getId());
	}
}