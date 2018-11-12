package emanondev.quests.interfaces.player;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import emanondev.quests.interfaces.ATaskManager;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.TaskType;


@SerializableAs("PlayerTask")
public class PlayerTaskManager extends ATaskManager<QuestPlayer> implements ConfigurationSerializable {
	
	private static PlayerTaskManager instance = new PlayerTaskManager();
	
	public static Task<QuestPlayer> deserialize(Map<String,Object> map) {
		try {
			if (!map.containsKey(Paths.TYPE_NAME))
				throw new IllegalStateException("invalid task Key");
			TaskType<QuestPlayer> type = instance.getType((String) map.get(Paths.TYPE_NAME));
			return type.getInstance(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO
		return null;
	}
	
	public static PlayerTaskManager get() {
		return instance;
	}

	@Override
	public Map<String, Object> serialize() {
		return null;
	}
	

}
