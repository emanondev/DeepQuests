package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;

public abstract class AQuestComponentWithWorlds<T extends User<T>> extends AQuestComponent<T>
		implements QuestComponentWithWorlds<T> {
	
	private Set<String> worlds = new HashSet<>();
	private boolean isWhitelist = false;
	

	@SuppressWarnings("unchecked")
	public AQuestComponentWithWorlds(Map<String, Object> map) {
		super(map);
		Map<String,Object> result = (Map<String, Object>) map.get(Paths.WORLDS);
		if (result == null) {
			worlds = getDefaultWorldsList();
			isWhitelist = getDefaultWorldsAreWhitelist();
		}
		else {
			worlds.addAll(result.get(Paths.WORLDS_LIST)==null ? getDefaultWorldsList() 
					: (List<String>) result.get(Paths.WORLDS_LIST));
			isWhitelist = result.get(Paths.WORLDS_IS_WHITELIST)==null ? getDefaultWorldsAreWhitelist() 
					: (boolean) result.get(Paths.WORLDS_IS_WHITELIST);
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String, Object> map = super.serialize();
		Map<String, Object> worlds = new LinkedHashMap<>();
		worlds.put(Paths.WORLDS_IS_WHITELIST,isWhitelist);
		worlds.put(Paths.WORLDS_LIST,new ArrayList<String>(this.worlds));
		map.put(Paths.WORLDS,worlds);
		return map;
	}

	protected abstract Set<String> getDefaultWorldsList();
	protected abstract boolean getDefaultWorldsAreWhitelist();
	
	@Override
	public boolean isWorldAllowed(World world) {
		if (isWhitelist)
			return worlds.contains(world.getName());
		else
			return !worlds.contains(world.getName());
	}
	
	public boolean toggleWorld(World world) {
		if (world == null)
			return false;
		return toggleWorld(world.getName());
	}
	public boolean toggleWorld(String world) {
		if (world==null || world.isEmpty())
			return false;
		if (worlds.contains(world))
			worlds.remove(world);
		else
			worlds.add(world);
		return true;
	}
	public boolean toggleWorldWhitelist() {
		return setWorldWhitelist(isWorldListWhitelist());
	}
	public boolean setWorldWhitelist(boolean value) {
		isWhitelist = value;
		return true;
	}
	public boolean isWorldListWhitelist() {
		return isWhitelist;
	}

}
