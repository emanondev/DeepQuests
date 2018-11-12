package emanondev.quests.interfaces.player;

import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.SerializableAs;

import emanondev.quests.interfaces.AQuest;

@SerializableAs(value="PlayerQuest")
public class PlayerQuest extends AQuest<QuestPlayer> {

	public PlayerQuest(Map<String, Object> map) {
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
