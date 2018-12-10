package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.block.Block;

import emanondev.quests.hooks.Hooks;
import emanondev.quests.interfaces.Paths;

public class VirginBlockData extends QuestComponentData {
	private boolean checkVirgin;
	public VirginBlockData(Map<String,Object> map) {
		if (map == null)
			return;
		try {
			checkVirgin = (boolean) map.getOrDefault(Paths.DATA_CHECK_VIRGIN, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isValidBlock(Block block) {
		if (checkVirgin==false)
			return true;
		return Hooks.isBlockVirgin(block);
	}
	
	public boolean setVirginCheck(boolean value) {
		if (checkVirgin == value)
			return false;
		checkVirgin = value;
		return true;
	}

	public boolean isVirginCheckEnabled() {
		return checkVirgin;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_CHECK_VIRGIN, checkVirgin);
		return map;
	}
}
