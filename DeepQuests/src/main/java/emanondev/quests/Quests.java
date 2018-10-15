package emanondev.quests;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import emanondev.quests.LoggerManager.Logger;
import emanondev.quests.bossbar.BossBarManager;
import emanondev.quests.citizenbinds.CitizenBindManager;
import emanondev.quests.command.CmdManager;
import emanondev.quests.command.CommandQuestItem;
import emanondev.quests.command.CommandQuestText;
import emanondev.quests.command.CommandQuests;
import emanondev.quests.command.CommandQuestsAdmin;
import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.gui.CustomGuiHandler;
import emanondev.quests.inventory.QuestPlayerGuiManager;
import emanondev.quests.mission.MissionManager;
import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.GuiHandler;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.PlayerManager;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.RequireManager;
import emanondev.quests.require.type.JobsLvRequireType;
import emanondev.quests.require.type.McmmoLvRequireType;
import emanondev.quests.require.type.NeedCompletedMissionRequireType;
import emanondev.quests.require.type.NeedPermissionType;
import emanondev.quests.reward.RewardManager;
import emanondev.quests.reward.type.CompleteMissionRewardType;
import emanondev.quests.reward.type.ConsoleCommandRewardType;
import emanondev.quests.reward.type.FailMissionRewardType;
import emanondev.quests.reward.type.ItemStackRewardType;
import emanondev.quests.reward.type.JobsExpRewardType;
import emanondev.quests.reward.type.McmmoExpRewardType;
import emanondev.quests.reward.type.SoundRewardType;
import emanondev.quests.reward.type.StartMissionRewardType;
import emanondev.quests.task.TaskManager;
import emanondev.quests.task.type.BreakBlockTaskType;
import emanondev.quests.task.type.BreedMobTaskType;
import emanondev.quests.task.type.EnterRegionTaskType;
import emanondev.quests.task.type.FarmerTaskType;
import emanondev.quests.task.type.FishingTaskType;
import emanondev.quests.task.type.InteractAtTaskType;
import emanondev.quests.task.type.KillMobTaskType;
import emanondev.quests.task.type.LeaveRegionTaskType;
import emanondev.quests.task.type.MythicMobKillTaskType;
import emanondev.quests.task.type.NPCDeliverTaskType;
import emanondev.quests.task.type.NPCKillTaskType;
import emanondev.quests.task.type.NPCTalkTaskType;
import emanondev.quests.task.type.PlaceBlockTaskType;
import emanondev.quests.task.type.ShearSheepTaskType;
import emanondev.quests.task.type.TameMobTaskType;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author emanon
 *
 *         main class
 */
public class Quests extends JavaPlugin {
	private final String consoleMexBase = "[" + this.getName() + "] ";
	private YMLConfig config;
	private QuestManager questManager;
	private TaskManager taskManager;
	private MissionManager missionManager;
	private PlayerManager playerManager;
	private RewardManager rewardManager;
	private RequireManager requireManager;
	private LoggerManager loggerManager;
	private QuestPlayerGuiManager guiManager;
	private ConfigManager configManager;
	private static Quests instance;

	/**
	 * return the config.yml
	 */
	public YMLConfig getConfig() {
		return config;
	}

	/**
	 * LoggerManager is used to get loggers see {@link #getLogger(String)}
	 * 
	 * @return loggerManager
	 */
	public LoggerManager getLoggerManager() {
		return loggerManager;
	}

	/**
	 * 
	 * @param fileName
	 *            - the file associated to the logger <br>
	 *            it may ends with ".log" else ".log" will be added<br>
	 *            to select a file in a sub folder use "/" in the name<br>
	 *            example "myfolder/mysubfolder/mylogger.log" <br>
	 *            to log on Console see {@link #consoleLog(String)}
	 * @return the logger associated with selected file
	 */
	public static Logger getLogger(String fileName) {
		return get().loggerManager.getLogger(fileName);
	}

	/**
	 * logs msg on the console
	 * 
	 * @param msg
	 *            - console will display "[pluginname] msg"
	 */
	public void consoleLog(String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', consoleMexBase + msg));
	}

	/**
	 * utility: register the listener for this plugin
	 * 
	 * @param listener
	 */
	private void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	/**
	 * utility: register a command for this plugin
	 * 
	 * @param cmdManager
	 */
	private void registerCommand(CmdManager cmdManager) {
		PluginCommand cmd = getCommand(cmdManager.getName());
		cmd.setExecutor(cmdManager);
		cmd.setTabCompleter(cmdManager);

		if (cmdManager.getAliases() != null && !cmdManager.getAliases().isEmpty())
			cmd.setAliases(cmdManager.getAliases());
	}

	/**
	 * @return the QuestManager
	 */
	public QuestManager getQuestManager() {
		return questManager;
	}

	/**
	 * 
	 * @return the TaskManager
	 */
	public TaskManager getTaskManager() {
		return taskManager;
	}

	/**
	 * 
	 * @return the MissionManager
	 */
	public MissionManager getMissionManager() {
		return missionManager;
	}

	/**
	 * 
	 * @return an istance of the plugin
	 */
	public static Quests get() {
		return instance;
	}

	/**
	 * 
	 * @return the Player Manager
	 */
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	/**
	 * 
	 * @return the reward Manager
	 */
	public RewardManager getRewardManager() {
		return rewardManager;
	}

	/**
	 * 
	 * @return the require Manager
	 */
	public RequireManager getRequireManager() {
		return requireManager;
	}

	/**
	 * 
	 * @return the Gui Manager
	 */
	public QuestPlayerGuiManager getGuiManager() {
		return guiManager;
	}

	/**
	 * 
	 * @return the Config Manager
	 */
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onEnable() {
		GuiConfig.reload();

		questManager = new QuestManager(this, "quests");
		registerListener(new SpawnReasonTracker());
		Language.reload();
		Defaults.reload();
		loggerManager = new LoggerManager();
		config = new YMLConfig(this, "config");
		configManager = new ConfigManager();
		//
		registerCommand(new CommandQuests());
		registerCommand(new CommandQuestsAdmin());
		registerCommand(new CommandQuestText());
		registerCommand(new CommandQuestItem());
		registerListener(new GuiHandler());
		registerListener(new ProgressLogger());
		registerListener(new CustomGuiHandler());
		guiManager = new QuestPlayerGuiManager();
		bossBarManager = new BossBarManager();
		requireManager = new RequireManager();

		rewardManager = new RewardManager();

		taskManager = new TaskManager();

		missionManager = new MissionManager();

		requireManager.registerRequireType(new NeedPermissionType());
		requireManager.registerRequireType(new NeedCompletedMissionRequireType());

		rewardManager.registerRewardType(new ConsoleCommandRewardType());
		rewardManager.registerRewardType(new SoundRewardType());
		rewardManager.registerRewardType(new ItemStackRewardType());
		rewardManager.registerRewardType(new FailMissionRewardType());
		rewardManager.registerRewardType(new CompleteMissionRewardType());
		rewardManager.registerMissionRewardType(new StartMissionRewardType());
		
		taskManager.registerType(new BreakBlockTaskType());
		taskManager.registerType(new PlaceBlockTaskType());
		taskManager.registerType(new InteractAtTaskType());
		taskManager.registerType(new FarmerTaskType());
		taskManager.registerType(new BreedMobTaskType());
		taskManager.registerType(new FishingTaskType());
		taskManager.registerType(new KillMobTaskType());
		taskManager.registerType(new ShearSheepTaskType());
		taskManager.registerType(new TameMobTaskType());

		if (Bukkit.getPluginManager().isPluginEnabled("Jobs")) {
			this.consoleLog("Hooking into Jobs");

			requireManager.registerRequireType(new JobsLvRequireType());
			rewardManager.registerRewardType(new JobsExpRewardType());
		}
		if (Bukkit.getPluginManager().isPluginEnabled("Mcmmo")) {
			this.consoleLog("Hooking into Mcmmo");

			requireManager.registerRequireType(new McmmoLvRequireType());
			rewardManager.registerRewardType(new McmmoExpRewardType());
		}
		if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
			this.consoleLog("Hooking into Citizens");

			taskManager.registerType(new NPCKillTaskType());
			taskManager.registerType(new NPCTalkTaskType());
			taskManager.registerType(new NPCDeliverTaskType());

			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					citizenBindManager = new CitizenBindManager();
					registerListener(citizenBindManager);
				}
			}, 2);
		}
		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")
				&& Bukkit.getPluginManager().isPluginEnabled("WGRegionEvents")) {
			this.consoleLog("Hooking into WGRegionEvents");

			taskManager.registerType(new EnterRegionTaskType());
			taskManager.registerType(new LeaveRegionTaskType());
		}
		if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
			this.consoleLog("Hooking into MythicMobs");

			taskManager.registerType(new MythicMobKillTaskType());
		}
		// TODO implement a way to activate listeners only if required

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				questManager.reload();
				playerManager = new PlayerManager();
				bossBarManager.reload();
				registerListener(playerManager);
				if (citizenBindManager != null)
					citizenBindManager.reload();
			}
		}, 3);
	}

	public void reload() {
		GuiConfig.reload();

		Language.reload();
		Defaults.reload();
		loggerManager.reload();
		config.reload();

		questManager.reload();
		playerManager.reload();
		bossBarManager.reload();
		if (citizenBindManager != null)
			citizenBindManager.reload();
	}

	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder() != null)
				if (inv.getHolder() instanceof Gui)
					p.closeInventory();
		}
		questManager.save();
		playerManager.saveAll();
	}

	private CitizenBindManager citizenBindManager = null;

	/**
	 * 
	 * @return the Citizen Binds Manager or null if Citizens is disabled
	 */
	public CitizenBindManager getCitizenBindManager() {
		return citizenBindManager;
	}

	private BossBarManager bossBarManager;

	/**
	 * 
	 * @return the BossBar Manager
	 */
	public BossBarManager getBossBarManager() {
		return bossBarManager;
	}

}
