package emanondev.quests.interfaces.player;

import emanondev.quests.interfaces.ARequireManager;

public class PlayerRequireManager extends ARequireManager<QuestPlayer> {
	
	private static final PlayerRequireManager instance = new PlayerRequireManager();

	public static PlayerRequireManager get() {
		return instance;
	}

}
