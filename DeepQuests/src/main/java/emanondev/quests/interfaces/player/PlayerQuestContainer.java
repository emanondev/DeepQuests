package emanondev.quests.interfaces.player;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;

import emanondev.quests.interfaces.AQuestContainer;
import emanondev.quests.interfaces.QuestManager;

@SerializableAs(value="PlayerQuestContainer")
public class PlayerQuestContainer extends AQuestContainer<QuestPlayer> {

	public PlayerQuestContainer(Map<String, Object> map) {
		super(map);
	}
	
	private PlayerQuestManager manager= null;
	
	public void setQuestManager(PlayerQuestManager manager) {
		if (this.manager==null)
			this.manager = manager;
	}

	@Override
	public QuestManager<QuestPlayer> getQuestManager() {
		return manager;
	}

}
