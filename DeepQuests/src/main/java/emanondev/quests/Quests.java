package emanondev.quests;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import emanondev.quests.LoggerManager.Logger;
import emanondev.quests.command.CommandQuests;
import emanondev.quests.command.CommandQuestsAdmin;
import emanondev.quests.inventory.GuiManager;
import emanondev.quests.mission.MissionManager;
import emanondev.quests.player.PlayerManager;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.RequireManager;
import emanondev.quests.reward.RewardManager;
import emanondev.quests.task.BreakBlockType;
import emanondev.quests.task.PlaceBlockType;
import emanondev.quests.task.TaskManager;
import net.md_5.bungee.api.ChatColor;

public class Quests extends JavaPlugin {
	private final String consoleMexBase = "["+this.getName()+"] ";
	private YMLConfig config;
	private QuestManager questManager;
	private TaskManager taskManager;
	private MissionManager missionManager;
	private PlayerManager playerManager;
	private RewardManager rewardManager;
	private RequireManager requireManager;
	private LoggerManager loggerManager;
	private GuiManager guiManager;
	private ConfigManager configManager;
	private static Quests instance;

	public YMLConfig getConfig() {
		return config;
	}
	/**
	 * 
	 * @return the file config of the loggers
	 */
	public LoggerManager getLoggerManager() {
		return loggerManager;
	}
	public static Logger getLogger(String name) {
		return getInstance().loggerManager.getLogger(name);
	}
	/**
	 * logs msg on the console
	 * @param msg - console will display "[pluginname] msg"
	 */
	public void consoleLog(String msg){
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				consoleMexBase+msg));
	}
	/**
	 * utility: register the listener for this plugin
	 * @param listener
	 */
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	public void registerCommand(String name,TabExecutor tabExe){
		registerCommand(name,tabExe,(String[]) null);
	}
	/**
	 * utility: register a command for this plugin
	 * @param name - command name used for /commandname
	 * @param tabExe - the class responsible for the command and the completer
	 * @param aliases - aliases of the command should be also saved on plugin.yml
	 */
	public void registerCommand(String name,TabExecutor tabExe,String... aliases){
		PluginCommand cmd = getCommand(name);
		cmd.setExecutor(tabExe);
		cmd.setTabCompleter(tabExe);
		
		if (aliases!=null) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i<aliases.length ; i++)
				if (aliases[i]!=null)
					list.add(aliases[i]);
			if (list.size()>0)
				cmd.setAliases(list);
		}
	}
	
	public QuestManager getQuestManager() {
		return questManager;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public MissionManager getMissionManager() {
		return missionManager;
	}
	public static Quests getInstance() {
		return instance;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public RewardManager getRewardManager() {
		return rewardManager;
	}

	public RequireManager getRequireManager() {
		return requireManager;
	}
	public GuiManager getGuiManager() {
		return guiManager;
	}
	public ConfigManager getConfigManager() {
		return configManager;
	}
	@Override
	public void onEnable() {
		instance = this;
		Defaults.reload();
		loggerManager = new LoggerManager();
		config = new YMLConfig(this,"config");
		configManager = new ConfigManager();
		//
		new CommandQuests();
		new CommandQuestsAdmin();
		guiManager = new GuiManager();
		
		requireManager = new RequireManager();
		
		rewardManager = new RewardManager();
		
		taskManager = new TaskManager();
		
		missionManager = new MissionManager();

		taskManager.registerType(new BreakBlockType());
		taskManager.registerType(new PlaceBlockType());
		//TODO
		questManager = new QuestManager();
		
		playerManager = new PlayerManager();
	}
	
	public void reload(){
		playerManager.saveAll();
		loggerManager = new LoggerManager();
		config.reload();
		Defaults.reload();
		guiManager = new GuiManager();
		
		requireManager = new RequireManager();
		
		rewardManager = new RewardManager();
		
		taskManager = new TaskManager();
		
		missionManager = new MissionManager();

		taskManager.registerType(new BreakBlockType());
		taskManager.registerType(new PlaceBlockType());
		//TODO
		questManager = new QuestManager();
		
		playerManager = new PlayerManager();
	}
	public void onDisable() {
		playerManager.saveAll();
	}

}
