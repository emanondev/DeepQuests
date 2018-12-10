package emanondev.quests.interfaces.player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import emanondev.quests.interfaces.ATask;

public abstract class PlayerTask extends ATask<QuestPlayer> {

	public PlayerTask(Map<String, Object> map) {
		super(map);
	}

	@Override
	protected Set<String> getDefaultWorldsList() {
		return new HashSet<>();
	}

	@Override
	protected boolean getDefaultWorldsAreWhitelist() {
		return false;
	}
}
