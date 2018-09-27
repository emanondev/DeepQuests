package emanondev.quests;

import java.util.EnumMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * 
 * @author emanon
 * 
 *
 */
public class SpawnReasonTracker implements Listener {
	SpawnReasonTracker() {
		Quests.get().registerListener(this);
	}
	private static final String metaName = "SpawnReason";
	private static final EnumMap<SpawnReason,FixedMetadataValue> fixedMetas = loadMetas();
	
	private static EnumMap<SpawnReason,FixedMetadataValue> loadMetas(){
		EnumMap<SpawnReason,FixedMetadataValue> map = new EnumMap<SpawnReason,FixedMetadataValue>(SpawnReason.class);
		for (SpawnReason reason : SpawnReason.values()) {
			map.put(reason, new FixedMetadataValue(Quests.get(),reason.toString()));
		}
		return map;
	}
	
	@EventHandler (ignoreCancelled=true,priority=EventPriority.MONITOR)
	private static void handler(CreatureSpawnEvent event) {
		event.getEntity().setMetadata(metaName, fixedMetas.get(event.getSpawnReason()));
	}
	
	
	/**
	 * 
	 * @param entity - target entity
	 * @return the reason why this entity was spawned, when there is no trace of spawning reason SpawnReason.DEFAULT is returned
	 */
	public static SpawnReason getSpawnReason(Entity entity) {
		try {
			if (!entity.hasMetadata(metaName))
				throw new NullPointerException();
			List<MetadataValue> list = entity.getMetadata(metaName);
			return SpawnReason.valueOf(list.get(0).asString());
		} catch (Exception e) {
			return SpawnReason.DEFAULT;
		}
	}

}
