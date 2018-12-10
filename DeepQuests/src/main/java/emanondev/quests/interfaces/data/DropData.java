package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import emanondev.quests.interfaces.Paths;

public class DropData  extends QuestComponentData {

	private boolean removeDrops = false;
	private boolean removeExp = false;
	
	public DropData(Map<String,Object> map) {
		if (map==null)
			return;
		removeDrops = (boolean) map.getOrDefault(Paths.DATA_DENY_ITEM_DROPS, false);
		removeExp = (boolean) map.getOrDefault(Paths.DATA_DENY_EXP_DROPS, false);
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_DENY_ITEM_DROPS, removeDrops);
		map.put(Paths.DATA_DENY_EXP_DROPS, removeExp);
		return map;
	}

	public boolean removeItemDrops() {
		return removeDrops;
	}
	public boolean removeExpDrops() {
		return removeExp;
	}

	public boolean setRemoveItemDrops(boolean value) {
		if (removeDrops==value)
			return false;
		removeDrops = value;
		return true;
	}
	public boolean setRemoveExpDrops(boolean value) {
		if (removeExp==value)
			return false;
		removeExp = value;
		return true;
	}
}