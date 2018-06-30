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
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomGuiHandler;
import emanondev.quests.inventory.QuestPlayerGuiManager;
import emanondev.quests.mission.MissionManager;
import emanondev.quests.player.PlayerManager;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.RequireManager;
import emanondev.quests.require.type.JobsLvRequireType;
import emanondev.quests.require.type.McmmoLvRequireType;
import emanondev.quests.require.type.NeedAnotherQuestMissionType;
import emanondev.quests.require.type.NeedMissionType;
import emanondev.quests.require.type.NeedPermissionType;
import emanondev.quests.reward.RewardManager;
import emanondev.quests.reward.type.ConsoleCommandRewardType;
import emanondev.quests.reward.type.FailMissionRewardType;
import emanondev.quests.reward.type.ForceStartAnotherQuestMissionRewardType;
import emanondev.quests.reward.type.ForceStartMissionRewardType;
import emanondev.quests.reward.type.ItemStackRewardType;
import emanondev.quests.reward.type.JobsExpRewardType;
import emanondev.quests.reward.type.McmmoExpRewardType;
import emanondev.quests.task.TaskManager;
import emanondev.quests.task.type.BreakBlockTaskType;
import emanondev.quests.task.type.BreedMobTaskType;
import emanondev.quests.task.type.EnterRegionTaskType;
import emanondev.quests.task.type.FishingTaskType;
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
	private QuestPlayerGuiManager guiManager;
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

	/**
	 * utility: register a command for this plugin
	 * @param name - command name used for /commandname
	 * @param tabExe - the class responsible for the command and the completer
	 * @param aliases - aliases of the command should be also saved on plugin.yml
	 */
	public void registerCommand(CmdManager cmdManager){
		PluginCommand cmd = getCommand(cmdManager.getName());
		cmd.setExecutor(cmdManager);
		cmd.setTabCompleter(cmdManager);
		
		if (cmdManager.getAliases()!=null && !cmdManager.getAliases().isEmpty())
			cmd.setAliases(cmdManager.getAliases());
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
	public QuestPlayerGuiManager getGuiManager() {
		return guiManager;
	}
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		questManager = new QuestManager(this,"quests");
		new SpawnReasonTracker();
		Defaults.reload();
		loggerManager = new LoggerManager();
		config = new YMLConfig(this,"config");
		configManager = new ConfigManager();
		//
		new CommandQuests();
		new CommandQuestsAdmin();
		new CommandQuestText();
		new CommandQuestItem();
		registerListener(new CustomGuiHandler());
		guiManager = new QuestPlayerGuiManager();
		bossBarManager = new BossBarManager();
		requireManager = new RequireManager();
		
		rewardManager = new RewardManager();
		
		taskManager = new TaskManager();
		
		missionManager = new MissionManager();

		requireManager.registerRequireType(new NeedPermissionType());
		requireManager.registerMissionRequireType(new NeedMissionType());
		requireManager.registerRequireType(new NeedAnotherQuestMissionType());
		rewardManager.registerRewardType(new ConsoleCommandRewardType());
		rewardManager.registerRewardType(new ItemStackRewardType());
		rewardManager.registerRewardType(new FailMissionRewardType());
		rewardManager.registerMissionRewardType(new ForceStartMissionRewardType());
		rewardManager.registerMissionRewardType(new ForceStartAnotherQuestMissionRewardType());
		taskManager.registerType(new BreakBlockTaskType());
		taskManager.registerType(new PlaceBlockTaskType());
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
			Bukkit.getScheduler().scheduleSyncDelayedTask(Quests.getInstance(), new Runnable() {
				@Override
				public void run() {
					citizenBindManager = new CitizenBindManager();
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
		//TODO implement a way to activate listeners only if required
		questManager.reload();
		playerManager = new PlayerManager();
		bossBarManager.reload();
		if (citizenBindManager!=null)
			citizenBindManager.reload();
	}
	
	public void reload(){
		Language.reload();
		Defaults.reload();
		loggerManager.reload();
		config.reload();
		
		questManager.reload();
		playerManager.reload();
		bossBarManager.reload();
		if (citizenBindManager!=null)
			citizenBindManager.reload();
	}
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder()!=null)
				if (inv.getHolder() instanceof CustomGui)
					p.closeInventory();
		}
		playerManager.saveAll();
	}
	private CitizenBindManager citizenBindManager = null;
	public CitizenBindManager getCitizenBindManager() {
		return citizenBindManager;
	}
	private BossBarManager bossBarManager;
	public BossBarManager getBossBarManager() {
		return bossBarManager;
	}
	
	

}
