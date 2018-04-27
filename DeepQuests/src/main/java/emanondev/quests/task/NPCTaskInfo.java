package emanondev.quests.task;

import java.util.HashSet;
import org.bukkit.configuration.MemorySection;
import net.citizensnpcs.api.npc.NPC;

public class NPCTaskInfo {
	public final static String PATH_NPC_ID = "npc-id";
	public final static String PATH_NPC_ID_AS_WHITELIST = "npc-id-is-whitelist";
	public final static String PATH_NPC_NAME = "npc-name";
	public final static String PATH_NPC_NAME_CONTAINS = "npc-name-contains";

	private final HashSet<Integer> ids = new HashSet<Integer>();
	private final boolean idsWhitelist;
	private final String name;
	private final String nameContains;
	
	public NPCTaskInfo(MemorySection m) {
		if (m.isInt(PATH_NPC_ID))
			ids.add(m.getInt(PATH_NPC_ID));
		else
			try {
				if (m.isList(PATH_NPC_ID))
					ids.addAll(m.getIntegerList(PATH_NPC_ID));
			} catch (Exception e) {}
		
		if (ids.size()==0)
			idsWhitelist = false;
		else
			idsWhitelist = m.getBoolean(PATH_NPC_ID_AS_WHITELIST,true);

		name = m.getString(PATH_NPC_NAME,null);
		
		nameContains = m.getString(PATH_NPC_NAME_CONTAINS,null);
		
	}
	
	private boolean checkID(NPC npc) {
		if (idsWhitelist) {
			if (!ids.contains(npc.getId()))
				return false;
		}
		else {
			if (ids.contains(npc.getId()))
				return false;
		}
		return true;
	}
	private boolean checkName(NPC npc) {
		if (name == null || npc.getFullName().equals(name))
			return true;
		return false;
	}
	
	private boolean checkNameContains(NPC npc) {
		if (nameContains==null  || npc.getFullName().equals(nameContains))
			return true;
		return false;
	}

	public boolean isValidNPC(NPC npc) {
		return checkID(npc)&&checkName(npc)&&checkNameContains(npc);
	}

}
