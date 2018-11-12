package emanondev.quests.interfaces.player;

import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.SerializableAs;

import emanondev.quests.interfaces.AMission;

@SerializableAs(value="PlayerMission")
public class PlayerMission extends AMission<QuestPlayer> {

	public PlayerMission(Map<String, Object> map) {
		super(map);
	}
	
	@Override
	protected Set<String> getDefaultWorldsList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected boolean getDefaultWorldsAreWhitelist() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
