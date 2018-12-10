package emanondev.quests.interfaces.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.AQuestManager;
import emanondev.quests.interfaces.Paths;


public class PlayerQuestManager extends AQuestManager<QuestPlayer>{
	
	private PlayerTaskManager taskManager;
	private PlayerRequireManager requireManager;
	private PlayerRewardManager rewardManager;
	
	private PlayerQuestContainer questContainer = null;
	private PlayerUserManager userManager;
	private PlayerBossBarManager bossBarManager;

	public PlayerQuestManager(String name) {
		super(name);
		ConfigurationSerialization.registerClass(PlayerTaskManager.class);
		taskManager = PlayerTaskManager.get();
		requireManager = PlayerRequireManager.get();
		rewardManager = PlayerRewardManager.get();
		userManager = new PlayerUserManager(this);
		bossBarManager = new PlayerBossBarManager(this);
		//TODO types
		
		
		//delay for custom types
		Bukkit.getScheduler().runTaskLater(Quests.get(),new Runnable() {
			public void run() {
				loadQuestContainer();
				userManager.loadAll();
			}
		},5L);
	}

	@Override
	public PlayerQuestContainer getQuestContainer() {
		if (questContainer==null)
			loadQuestContainer();
		return questContainer;
	}
	
	

	private void loadQuestContainer() {
		if (questContainer==null) {
			questContainer = this.getQuestContainerConfig().get(Paths.QUESTCONTAINER_BASE);
			if (questContainer==null) {
				Map<String,Object> map = new HashMap<>();
				map.put(Paths.QUESTCONTAINER_BASE,this.getName());
				questContainer = new PlayerQuestContainer(map);
			}
			questContainer.setQuestManager(this);
			userManager.loadAll();
		}
	}

	@Override
	public PlayerRequireManager getRequireManager() {
		return requireManager;
	}

	@Override
	public PlayerRewardManager getRewardManager() {
		return rewardManager;
	}

	@Override
	public PlayerTaskManager getTaskManager() {
		return taskManager;
	}

	@Override
	public PlayerUserManager getUserManager() {
		return userManager;
	}
	
	@Override
	public PlayerBossBarManager getBossBarManager() {
		return bossBarManager;
	}

}
