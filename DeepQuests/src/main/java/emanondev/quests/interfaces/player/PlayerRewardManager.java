package emanondev.quests.interfaces.player;

import emanondev.quests.interfaces.ARewardManager;

public class PlayerRewardManager extends ARewardManager<QuestPlayer> {
	
	private static final PlayerRewardManager instance = new PlayerRewardManager();

	public static PlayerRewardManager get() {
		return instance;
	}

}
