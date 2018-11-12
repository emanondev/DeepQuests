package emanondev.quests.interfaces.player;

import java.util.Map;
import java.util.Set;

import emanondev.quests.interfaces.ATask;

public class PlayerTask extends ATask<QuestPlayer> {

	public PlayerTask(Map<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
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
