package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import emanondev.quests.interfaces.Paths;

public class PermissionData extends QuestComponentData {
	
	private String permission = null;
	
	public PermissionData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			permission = (String) map.getOrDefault(Paths.DATA_PERMISSION, null);
			if (permission!=null && (permission.contains(" ")||permission.isEmpty()))
				throw new IllegalArgumentException("Illegal permission value");
		} catch (Exception e) {
			e.printStackTrace();
			permission = null;
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (permission !=null)
			map.put(Paths.DATA_PERMISSION,permission);
		return map;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean setPermission(String value) {
		if (this.permission==value)
			return false;
		if (value!=null && (value.contains(" ")||value.isEmpty()))
			return false;
		if (this.permission!=null && this.permission.equals(value)) 
			return false;
		this.permission = value;
		return true;
	}

	public boolean hasPermission(Player player) {
		if (permission==null || player == null)
			return false;
		return player.hasPermission(permission);
	}
}