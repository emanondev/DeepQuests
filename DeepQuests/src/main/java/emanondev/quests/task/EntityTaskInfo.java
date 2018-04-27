package emanondev.quests.task;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import emanondev.quests.Quests;
import emanondev.quests.SpawnReasonTracker;
import emanondev.quests.utils.MemoryUtils;

public class EntityTaskInfo {
	public final static String PATH_ENTITY = "entity";
	public final static String PATH_ENTITY_AS_WHITELIST = "entity-is-whitelist";
	public final static String PATH_ENTITY_NAME = "entity-name";
	public final static String PATH_IGNORE_NPC = "ignore-npc";
	public final static String PATH_ENTITY_SPAWNREASON = "spawn-reason.list";
	public final static String PATH_ENTITY_SPAWNREASON_AS_WHITELIST = "spawn-reason.is-whitelist";
	private final EnumSet<EntityType> entity = EnumSet.noneOf(EntityType.class);
	private final EnumSet<SpawnReason> spawnReasons = EnumSet.noneOf(SpawnReason.class);
	private final boolean spawnReasonsWhitelist;
	private final boolean entityWhitelist;
	private final boolean ignoreNPC;
	private final String entityName;
	public EntityTaskInfo(MemorySection m) {
		List<String> list = MemoryUtils.getStringList(m, PATH_ENTITY);
		if (list!=null)
			for (String value : list) {
				try {
					entity.add(EntityType.valueOf(value));
				} catch (Exception e) {
					Quests.getLogger("errors").log("error on Path "+m.getCurrentPath()+"."+m.getName()
						+" "+e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		if (entity.isEmpty())
			entityWhitelist = false;
		else
			entityWhitelist = m.getBoolean(PATH_ENTITY_AS_WHITELIST,true);
		
		entityName = m.getString(PATH_ENTITY_NAME,null);
		
		
		list = MemoryUtils.getStringList(m, PATH_ENTITY_SPAWNREASON);
		if (list!=null)
			for (String value : list) {
				try {
					spawnReasons.add(SpawnReason.valueOf(value));
				} catch (Exception e) {
					Quests.getLogger("errors").log("error on Path "+m.getCurrentPath()+"."+m.getName()
						+" "+e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		if (spawnReasons.isEmpty())
			spawnReasonsWhitelist = false;
		else
			spawnReasonsWhitelist = m.getBoolean(PATH_ENTITY_SPAWNREASON_AS_WHITELIST,false);
		
		
		ignoreNPC = m.getBoolean(PATH_IGNORE_NPC,false);
	}
	
	private boolean checkEntityType(Entity e) {
		if (entityWhitelist) {
			if (!entity.contains(e.getType()))
				return false;
		}
		else {
			if (entity.contains(e.getType()))
				return false;
		}
		return true;
	}
	private boolean checkSpawnReason(Entity e) {
		SpawnReason reason = SpawnReasonTracker.getSpawnReason(e);
		if (spawnReasonsWhitelist) {
			if (!spawnReasons.contains(reason))
				return false;
		}
		else {
			if (spawnReasons.contains(reason))
				return false;
		}
		return true;
	}
	
	private boolean checkEntityName(Entity e) {
		if (entityName==null)
			return true;
		
		return entityName.equals(e.getName());
	}
	private boolean checkNPC(Entity e) {
		if (e.hasMetadata("NPC")&&ignoreNPC)
			return false;
		return true;
	}

	public boolean isValidEntity(Entity e) {
		return checkEntityType(e)&&checkSpawnReason(e)&&checkEntityName(e)&&checkNPC(e);
	}
	
}
