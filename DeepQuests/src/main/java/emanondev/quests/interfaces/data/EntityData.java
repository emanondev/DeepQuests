package emanondev.quests.interfaces.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import emanondev.quests.SpawnReasonTracker;
import emanondev.quests.interfaces.Paths;

public class EntityData extends QuestComponentData {
	
	private final EnumSet<EntityType> entityType = EnumSet.noneOf(EntityType.class);
	private final EnumSet<SpawnReason> spawnReasons = EnumSet.noneOf(SpawnReason.class);
	private boolean spawnReasonsIsWhitelist = false;
	private boolean entityTypeIsWhitelist = false;
	
	private boolean ignoreNPC = true;
	private String entityName = null;
	

	@SuppressWarnings("unchecked")
	public EntityData(Map<String, Object> map) {
		if (map == null)
			return;
		
		List<String> list;
		if (map.get(Paths.DATA_ENTITYTYPE_LIST)!=null) {
			list = (List<String>) map.get(Paths.DATA_ENTITYTYPE_LIST);
			for (String type:list) {
				entityType.add(EntityType.valueOf(type));
			}
		}
		if (map.get(Paths.DATA_ENTITY_SPAWNREASON_LIST)!=null) {
			list = (List<String>) map.get(Paths.DATA_ENTITY_SPAWNREASON_LIST);
			for (String type:list) {
				spawnReasons.add(SpawnReason.valueOf(type));
			}
		}
		entityTypeIsWhitelist = (boolean) map.getOrDefault(Paths.DATA_ENTITYTYPE_IS_WHITELIST, false);
		spawnReasonsIsWhitelist = (boolean) map.getOrDefault(Paths.DATA_ENTITY_SPAWNREASON_IS_WHITELIST, false);
		ignoreNPC = (boolean) map.getOrDefault(Paths.DATA_IGNORE_NPC, true);
		entityName = (String) map.getOrDefault(Paths.DATA_ENTITY_NAME, null);
	}
	
	public Map<String,Object> serialize(){
		Map<String,Object> map = new LinkedHashMap<>();
		if (entityType.size()>0) {
			List<String> list = new ArrayList<>();
			for (EntityType type:entityType)
				list.add(type.toString());
			map.put(Paths.DATA_ENTITYTYPE_LIST, list);
		}
		if (spawnReasons.size()>0) {
			List<String> list = new ArrayList<>();
			for (SpawnReason type:spawnReasons)
				list.add(type.toString());
			map.put(Paths.DATA_ENTITYTYPE_LIST, list);
		}
		if (entityTypeIsWhitelist)
			map.put(Paths.DATA_ENTITYTYPE_IS_WHITELIST, entityTypeIsWhitelist);
		if (spawnReasonsIsWhitelist)
			map.put(Paths.DATA_ENTITY_SPAWNREASON_IS_WHITELIST, spawnReasonsIsWhitelist);
		if (!ignoreNPC)
			map.put(Paths.DATA_IGNORE_NPC, ignoreNPC);
		if (entityName!=null)
			map.put(Paths.DATA_ENTITY_NAME, entityName);
		return map;
	}
	
	public boolean isValidEntity(Entity e) {
		if (entityType.contains(e.getType())) {
			if (!entityTypeIsWhitelist)
				return false;
		}
		else
			if (entityTypeIsWhitelist)
				return false;
		
		SpawnReason reason = SpawnReasonTracker.getSpawnReason(e);
		if (spawnReasons.contains(reason)) {
			if (!spawnReasonsIsWhitelist)
				return false;
		}
		else
			if (spawnReasonsIsWhitelist)
				return false;
		
		if (ignoreNPC && e.hasMetadata("NPC"))
			return false;
		
		if (entityName == null)
			return true;
		return entityName.equals(e.getName());
	}
	
	public boolean toggleEntityType(EntityType type) {
		if (!type.isAlive())
			return false;
		if (entityType.contains(type))
			entityType.remove(type);
		else
			entityType.add(type);
		return true;
	}

}
