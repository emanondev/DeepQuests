package emanondev.quests.mission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.button.StringListEditorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.task.VoidTaskType;
import emanondev.quests.utils.AQuestComponent;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.QCWithCooldown;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Mission extends QCWithCooldown {

	private static final String PATH_TASKS = "tasks";
	private static final String PATH_REQUIRES = "requires";
	private static final String PATH_START_REWARDS = "start-rewards";
	private static final String PATH_COMPLETE_REWARDS = "complete-rewards";
	private static final String PATH_START_TEXT = "start-text";
	private static final String PATH_COMPLETE_TEXT = "complete-text";
	private static final String PATH_PAUSE_TEXT = "pause-text";
	private static final String PATH_UNPAUSE_TEXT = "unpause-text";
	private static final String PATH_FAIL_TEXT = "fail-text";
	private static final String PATH_CAN_PAUSE = "can-pause";
	private static final TaskType voidTaskType = new VoidTaskType();

	private final HashMap<String, Task> tasks = new HashMap<String, Task>();
	private final HashMap<String, Reward> completeRewards = new HashMap<String, Reward>();
	private final HashMap<String, Reward> startRewards = new HashMap<String, Reward>();
	private final HashMap<String, Require> requires = new HashMap<String, Require>();
	private final MissionDisplayInfo displayInfo;
	private ArrayList<String> onStartText;
	private ArrayList<String> onCompleteText;
	private ArrayList<String> onPauseText;
	private ArrayList<String> onUnpauseText;
	private ArrayList<String> onFailText;

	public Mission(ConfigSection m, Quest parent) {
		super(m, parent);
		mayBePaused = getSection().getBoolean(PATH_CAN_PAUSE, false);
		HashMap<String, Task> tasks = loadTasks(getSection().loadSection(PATH_TASKS));
		if (tasks != null)
			this.tasks.putAll(tasks);
		for (Task task : tasks.values()) {
			if (isDirty())
				break;
			if (task.isLoadDirty())
				setDirtyLoad();
		}
		HashMap<String, Require> req = loadRequires();
		if (req != null)
			this.requires.putAll(req);
		HashMap<String, Reward> rew = loadStartRewards();
		if (rew != null)
			this.startRewards.putAll(rew);
		rew = loadCompleteRewards();
		if (rew != null)
			this.completeRewards.putAll(rew);

		onStartText = loadStartText();
		onCompleteText = loadCompleteText();
		onPauseText = loadPauseText();
		onUnpauseText = loadUnpauseText();
		onFailText = loadFailText();

		this.displayInfo = loadDisplayInfo();
		if (displayInfo.isDirty())
			setDirtyLoad();
	}

	public Quest getParent() {
		return (Quest) super.getParent();
	}

	public ArrayList<String> getStartMessage() {
		return StringUtils.fixColorsAndHolders(onStartText, H.MISSION_NAME, getDisplayName());
	}

	public ArrayList<String> getCompleteMessage() {
		return StringUtils.fixColorsAndHolders(onCompleteText, H.MISSION_NAME, getDisplayName());
	}

	public ArrayList<String> getUnpauseMessage() {
		return StringUtils.fixColorsAndHolders(onUnpauseText, H.MISSION_NAME, getDisplayName());
	}

	public ArrayList<String> getPauseMessage() {
		return StringUtils.fixColorsAndHolders(onPauseText, H.MISSION_NAME, getDisplayName());
	}

	public ArrayList<String> getFailMessage() {
		return StringUtils.fixColorsAndHolders(onFailText, H.MISSION_NAME, getDisplayName());
	}

	public Collection<Task> getTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	public Task getTaskByID(String key) {
		return tasks.get(key);
	}

	public Collection<Reward> getStartRewards() {
		return Collections.unmodifiableCollection(startRewards.values());
	}

	public Collection<Reward> getCompleteRewards() {
		return Collections.unmodifiableCollection(completeRewards.values());
	}

	public Collection<Require> getRequires() {
		return Collections.unmodifiableCollection(requires.values());
	}

	@Override
	public MissionDisplayInfo getDisplayInfo() {
		return displayInfo;
	}
	public List<String> getInfo(){
		List<String> info = new ArrayList<String>();
		info.add("&9&lMission: &6"+ this.getDisplayName());
		info.add("&8ID: "+ this.getID());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		info.add("&9Quest: &e"+getParent().getDisplayName());

		if (!this.getCooldownData().isRepetable())
			info.add("&9Repeatable: &cFalse");
		else
			info.add("&9Repeatable: &aTrue");
		info.add("&9Cooldown: &e" + StringUtils.getStringCooldown(this.getCooldownData().getCooldownTime()));
		if (tasks.size() > 0) {
			info.add("&9Missions:");
			for (Task task : tasks.values()) {
				info.add("&9 - &e"+task.getDisplayName());
				info.add("   &7Type: "+task.getTaskType().getKey());
			}
		}
		if (this.getWorldsList().size() > 0) {
			if (this.isWorldListBlacklist()) {
				info.add("&9Blacklisted Worlds:");
				for (String world : this.getWorldsList())
					info.add("&9 - &c" + world);
			}
			else {
				info.add("&9WhiteListed Worlds:");
				for (String world : this.getWorldsList())
					info.add("&9 - &a" + world);
			}
		}
		if (requires.size() > 0) {
			info.add("&9Requires:");
			for (Require require : requires.values()) {
				info.add("&9 - &e" + require.getDescription());
				info.add("   &7" + require.getType().getKey());
			}
		}
		return info;
	}

	public void reloadDisplay() {
		displayInfo.reloadDisplay();
	}

	private HashMap<String, Task> loadTasks(ConfigSection m) {
		if (m == null)
			return new HashMap<String, Task>();
		Set<String> s = m.getKeys(false);
		HashMap<String, Task> map = new HashMap<String, Task>();
		s.forEach((key) -> {
			try {
				Task task = Quests.get().getTaskManager().readTask(m.getString(key + "." + AbstractTask.PATH_TASK_TYPE),
						m.loadSection(key), this);
				map.put(task.getID(), task);
			} catch (Exception e) {
				e.printStackTrace();
				Quests.get().getLoggerManager().getLogger("errors")
						.log("Error while loading Mission on file quests.yml '" + m.getCurrentPath() + "." + key
								+ "' could not be read as valid task", ExceptionUtils.getStackTrace(e));
				Task task = voidTaskType.getTaskInstance(m, this);
				map.put(task.getID(), task);
			}
		});
		return map;
	}

	private ArrayList<String> loadStartText() {
		List<String> list = getSection().getStringList(PATH_START_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultStartText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private ArrayList<String> loadCompleteText() {
		List<String> list = getSection().getStringList(PATH_COMPLETE_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultCompleteText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private ArrayList<String> loadPauseText() {
		List<String> list = getSection().getStringList(PATH_PAUSE_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultPauseText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private ArrayList<String> loadUnpauseText() {
		List<String> list = getSection().getStringList(PATH_UNPAUSE_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultUnpauseText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private ArrayList<String> loadFailText() {
		List<String> list = getSection().getStringList(PATH_FAIL_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultFailText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private MissionDisplayInfo loadDisplayInfo() {
		return new MissionDisplayInfo(getSection(), this);
	}

	private HashMap<String, Require> loadRequires() {
		return Quests.get().getRequireManager().loadRequires(this, getSection().loadSection(PATH_REQUIRES));
	}

	private HashMap<String, Reward> loadStartRewards() {
		return Quests.get().getRewardManager().loadRewards(this, getSection().loadSection(PATH_START_REWARDS));
	}

	private HashMap<String, Reward> loadCompleteRewards() {
		return Quests.get().getRewardManager().loadRewards(this, getSection().loadSection(PATH_COMPLETE_REWARDS));
	}

	@Override
	protected List<String> getWorldsListDefault() {
		return Defaults.MissionDef.getWorldsListDefault();
	}

	@Override
	protected boolean shouldWorldsAutogen() {
		return Defaults.MissionDef.shouldWorldsAutogen();
	}

	@Override
	protected boolean getUseWorldsAsBlacklistDefault() {
		return Defaults.MissionDef.getUseWorldsAsBlacklistDefault();
	}

	@Override
	protected boolean getDefaultCooldownUse() {
		return Defaults.MissionDef.getDefaultCooldownUse();
	}

	@Override
	protected long getDefaultCooldownMinutes() {
		return Defaults.MissionDef.getDefaultCooldownMinutes();
	}

	/**
	 * 
	 * @param displayName
	 * @param taskType
	 * @return the id of the task or null if the task has failed to be added
	 */
	public String addTask(String displayName, TaskType taskType) {
		return this.addTask(null, displayName, taskType);
	}

	public String addTask(String id, String displayName, TaskType taskType) {
		if (taskType == null)
			return null;
		
		if (id==null)
			id = getQuestManager().getNewTaskID(Mission.this);
		if (id.isEmpty() || id.contains(" ") || id.contains(".") || id.contains(":"))
			return null;
		if (displayName == null)
			displayName = id.replace("_", " ");
		id = id.toLowerCase();
		if (tasks.containsKey(id))
			return null;
		getSection().set(PATH_TASKS + "." + id + "." + AQuestComponent.PATH_DISPLAY_NAME, displayName);
		getSection().set(PATH_TASKS + "." + id + "." + AbstractTask.PATH_TASK_TYPE, taskType.getKey());
		getSection().set(PATH_TASKS + "." + id + "." + AbstractTask.PATH_TASK_MAX_PROGRESS, 1);
		Task t = Quests.get().getTaskManager().readTask(taskType.getKey(), getSection(), this);
		tasks.put(t.getID(), t);
		getQuestManager().save();
		getQuestManager().reload();
		Quests.get().getPlayerManager().reload();
		return id;
	}

	public boolean deleteTask(Task task) {
		if (task == null || !tasks.containsKey(task.getID()))
			return false;
		getSection().set(PATH_TASKS + "." + task.getID(), null);
		tasks.remove(task.getID());
		getParent().getParent().save();
		getParent().getParent().reload();
		Quests.get().getPlayerManager().reload();
		return true;
	}

	private final static BaseComponent[] setDisplayNameDescription = new ComponentBuilder(
			ChatColor.GOLD + "Click suggest the command\n\n" + ChatColor.GOLD + "Set the display name for the task\n"
					+ ChatColor.YELLOW + "/questtext <display name>").create();

	private final static String PATH_REQUIRE_TYPE = "type";
	private final static String PATH_REWARD_TYPE = "type";

	public Require addRequire(RequireType type) {
		if (type == null)
			return null;
		String key = null;
		int i = 0;
		do {
			key = "rq" + i;
			i++;
		} while (requires.containsKey(key));
		getSection().set(PATH_REQUIRES + "." + key + "." + PATH_REQUIRE_TYPE, type.getKey());
		Require req = type.getInstance(getSection().loadSection(PATH_REQUIRES + "." + key), this);
		requires.put(req.getID(), req);
		setDirty(true);
		return req;
	}

	public boolean deleteRequire(Require req) {
		if (req == null || !requires.containsKey(req.getID()) || !requires.get(req.getID()).equals(req))
			return false;
		getSection().set(PATH_REQUIRES + "." + req.getID(), null);
		requires.remove(req.getID());
		setDirty(true);
		return true;
	}

	public Reward addCompleteReward(RewardType type) {
		if (type == null)
			return null;
		String key = null;
		int i = 0;
		do {
			key = "crw" + i;
			i++;
		} while (completeRewards.containsKey(key));
		getSection().set(PATH_COMPLETE_REWARDS + "." + key + "." + PATH_REWARD_TYPE, type.getKey());
		Reward rew = type.getInstance(getSection().loadSection(PATH_COMPLETE_REWARDS + "." + key), this);
		completeRewards.put(rew.getID(), rew);
		setDirty(true);
		return rew;
	}

	public boolean deleteCompleteReward(Reward rew) {
		if (rew == null || !completeRewards.containsKey(rew.getID()) || !completeRewards.get(rew.getID()).equals(rew))
			return false;
		getSection().set(PATH_COMPLETE_REWARDS + "." + rew.getID(), null);
		completeRewards.put(rew.getID(), rew);
		setDirty(true);
		return true;
	}

	private boolean mayBePaused;

	public boolean mayBePaused() {
		return mayBePaused;
	}

	public boolean setStartMessage(List<String> value) {
		if (value == null)
			return false;
		this.onStartText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_START_TEXT, onStartText);
		this.setDirty(true);
		return true;
	}

	public boolean setCompleteMessage(List<String> value) {
		if (value == null)
			return false;
		this.onCompleteText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_COMPLETE_TEXT, onCompleteText);
		this.setDirty(true);
		return true;
	}

	public boolean setUnpauseMessage(List<String> value) {
		if (value == null)
			return false;
		this.onUnpauseText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_UNPAUSE_TEXT, onUnpauseText);
		this.setDirty(true);
		return true;
	}

	public boolean setPauseMessage(List<String> value) {
		if (value == null)
			return false;
		this.onPauseText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_PAUSE_TEXT, onPauseText);
		this.setDirty(true);
		return true;
	}

	public boolean setFailMessage(List<String> value) {
		if (value == null)
			return false;
		this.onFailText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_FAIL_TEXT, onFailText);
		this.setDirty(true);
		return true;
	}

	private final static List<String> startButtonDesc = Arrays.asList("&6&lStart Message Editor",
			"&6The message sended when the mission is started", "&6Click to edit", "&6Current value:");
	private final static List<String> completeButtonDesc = Arrays.asList("&6&lComplete Message Editor",
			"&6The message sended when the mission is completed", "&6Click to edit", "&6Current value:");
	/*private final static List<String> pauseButtonDesc = Arrays.asList("&6&lPause Message Editor",
			"&6The message sended when the mission is paused", "&6Click to edit", "&6Current value:");
	private final static List<String> unpauseButtonDesc = Arrays.asList("&6&lUnpause Message Editor",
			"&6The message sended when the mission is unpaused", "&6Click to edit", "&6Current value:");
	*/
	private final static List<String> failButtonDesc = Arrays.asList("&6&lFail Message Editor",
			"&6The message sended when the mission is failed", "&6Click to edit", "&6Current value:");

	@Override
	public Gui createEditorGui(Player p, Gui previusHolder) {
		return new MissionEditor(p, previusHolder);
	}

	protected class MissionEditor extends QCWithCooldownEditor {

		public MissionEditor(Player p, Gui previusHolder) {
			super("&8Mission: &9" + Mission.this.getDisplayName(), p, previusHolder);
			this.putButton(33, new StringListEditorButton("&8Start Message editor",
					new ItemBuilder(Material.STAINED_CLAY).setDamage(5).setGuiProperty().build(),MissionEditor.this) {
				public List<String> getCurrentList() {
					return onStartText;
				}

				public boolean onStringListChange(List<String> list) {
					return setStartMessage(list);
				}

				@Override
				public List<String> getButtonDescription() {
					return startButtonDesc;
				}
				
			});
			this.putButton(34, new StringListEditorButton("&8Complete Message editor",
					new ItemBuilder(Material.STAINED_CLAY).setDamage(13).setGuiProperty().build(),MissionEditor.this) {
				public List<String> getCurrentList() {
					return onCompleteText;
				}

				public boolean onStringListChange(List<String> list) {
					return setCompleteMessage(list);
				}

				@Override
				public List<String> getButtonDescription() {
					return completeButtonDesc;
				}
				
			});
			/*this.putButton(34, new StringListEditorButton("&8Pause Message editor",
					new ItemBuilder(Material.STAINED_CLAY, 1, (short) 4),MissionEditor.this) {
				public List<String> getCurrentList() {
					return onPauseText;
				}

				public boolean onStringListChange(List<String> list) {
					return setPauseMessage(list);
				}

				@Override
				public List<String> getButtonDescription() {
					return pauseButtonDesc;
				}
			});
			this.putButton(35, new StringListEditorButton("&8Unpause Message editor",
					new ItemBuilder(Material.STAINED_CLAY, 1, (short) 1),MissionEditor.this) {
				public List<String> getCurrentList() {
					return onUnpauseText;
				}

				public boolean onStringListChange(List<String> list) {
					return setUnpauseMessage(list);
				}

				@Override
				public List<String> getButtonDescription() {
					return unpauseButtonDesc;
				}
			});*/
			this.putButton(35, new StringListEditorButton("&8Fail Message editor",
					new ItemBuilder(Material.STAINED_CLAY).setDamage(14).setGuiProperty().build(),MissionEditor.this) {
				public List<String> getCurrentList() {
					return onFailText;
				}

				public boolean onStringListChange(List<String> list) {
					return setFailMessage(list);
				}
				@Override
				public List<String> getButtonDescription() {
					return failButtonDesc;
				}
			});
			this.putButton(0, new TaskExplorerButton());
			this.putButton(1, new AddTaskButton());
			this.putButton(2, new DeleteTaskButton());
			this.putButton(18, new RequireExplorerButton());
			this.putButton(19, new AddRequireButton());
			this.putButton(20, new DeleteRequireButton());
			this.putButton(27, new CompleteRewardExplorerButton());
			this.putButton(28, new AddCompleteRewardButton());
			this.putButton(29, new DeleteCompleteRewardButton());

		}

		private class DeleteTaskButton extends SelectOneElementButton<Task> {

			public DeleteTaskButton() {
				super("&cSelect Task to delete", new ItemBuilder(Material.NETHERRACK).setGuiProperty().build(), MissionEditor.this,
						Mission.this.getTasks(), false, true, true);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDelete Task", "&6Click to select a Task");
			}

			@Override
			public List<String> getElementDescription(Task task) {
				return Arrays.asList("&6Task: '&e" + task.getDisplayName() + "&6'",
						"&7(" + task.getTaskType().getKey() + ")");
			}

			@Override
			public ItemStack getElementItem(Task element) {
				return new ItemBuilder(Material.PAPER).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Task task) {
				if (Mission.this.deleteTask(task)) {
					MissionEditor.this.putButton(0, new TaskExplorerButton());
					MissionEditor.this.putButton(2, new DeleteTaskButton());
					getTargetPlayer().openInventory(MissionEditor.this.getInventory());
				}
			}
		}

		private class TaskExplorerButton extends SelectOneElementButton<Task> {

			public TaskExplorerButton() {
				super("&cSelect Task to open", new ItemBuilder(Material.PAINTING).setGuiProperty().build(), MissionEditor.this,
						Mission.this.getTasks(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lOpen Task", "&6Click to select a Task");
			}

			@Override
			public List<String> getElementDescription(Task task) {
				return task.getInfo();
			}

			@Override
			public ItemStack getElementItem(Task element) {
				return new ItemBuilder(Material.PAPER).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Task task) {
				this.getTargetPlayer()
						.openInventory(task.createEditorGui(getTargetPlayer(), MissionEditor.this).getInventory());
			}
		}

		private class AddTaskButton extends SelectOneElementButton<TaskType> {
			public AddTaskButton() {
				super("&cSelect TaskType to create", new ItemBuilder(Material.GLOWSTONE).setGuiProperty().build(), MissionEditor.this,
						Quests.get().getTaskManager().getTaskTypes(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lAdd Task", "&6Click to create a new Task");
			}

			@Override
			public List<String> getElementDescription(TaskType taskType) {
				List<String> list = new ArrayList<String>();
				list.add("&6" + taskType.getKey());
				list.addAll(taskType.getDescription());
				return list;
			}

			@Override
			public ItemStack getElementItem(TaskType taskType) {
				return new ItemBuilder(taskType.getGuiItemMaterial()).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(TaskType taskType) {
				new TaskNameEditor(taskType).onClick(getTargetPlayer(), ClickType.LEFT);
			}

			private class TaskNameEditor extends emanondev.quests.newgui.button.TextEditorButton {
				private final TaskType taskType;

				public TaskNameEditor(TaskType taskType) {
					super(null, MissionEditor.this);
					this.taskType = taskType;
				}

				@Override
				public List<String> getButtonDescription() {
					return null;
				}

				@Override
				public void onReicevedText(String text) {
					if (text == null) {
						getTargetPlayer().openInventory(MissionEditor.this.getInventory());
						return;
					}
					String taskId = Mission.this.addTask(text, taskType);
					if (taskId != null) {
						String questId = Mission.this.getParent().getID();
						String missionId = Mission.this.getID();
						Quest quest = Mission.this.getParent().getParent().getQuestByID(questId);
						Mission mission = quest.getMissionByID(missionId);
						Task task = mission.getTaskByID(taskId);

						getTargetPlayer().openInventory(
							task.createEditorGui(getTargetPlayer(),
							mission.createEditorGui(getTargetPlayer(),
							quest.createEditorGui(getTargetPlayer(), 
								MissionEditor.this.getPreviusGui().getPreviusGui())))
								.getInventory());
					}

				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					this.requestText(clicker, null, setDisplayNameDescription);
				}
			}
		}

		private class DeleteRequireButton extends SelectOneElementButton<Require> {

			public DeleteRequireButton() {
				super("&cSelect Require to delete", new ItemBuilder(Material.NETHERRACK).setGuiProperty().build(), MissionEditor.this,
						Mission.this.getRequires(), false, true, true);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDelete Require", "&6Click to select a Require");
			}

			@Override
			public List<String> getElementDescription(Require require) {
				return Arrays.asList("&6Require:", "&6" + require.getInfo());
			}

			@Override
			public ItemStack getElementItem(Require require) {
				return new ItemBuilder(Material.PAPER).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Require require) {
				if (Mission.this.deleteRequire(require)) {
					MissionEditor.this.putButton(18, new RequireExplorerButton());
					MissionEditor.this.putButton(20, new DeleteRequireButton());
				}
			}
		}

		private class RequireExplorerButton extends SelectOneElementButton<Require> {

			public RequireExplorerButton() {
				super("&cSelect Require to open", new ItemBuilder(Material.PAINTING).setGuiProperty().build(), MissionEditor.this,
						Mission.this.getRequires(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lOpen require", "&6Click to select a require");
			}

			@Override
			public List<String> getElementDescription(Require require) {
				return Arrays.asList("&6Require:", "&6" + require.getInfo());
			}

			@Override
			public ItemStack getElementItem(Require require) {
				return new ItemBuilder(Material.PAPER).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Require require) {
				this.getTargetPlayer()
						.openInventory(require.createEditorGui(getTargetPlayer(), MissionEditor.this).getInventory());
			}
		}

		private class AddRequireButton extends SelectOneElementButton<RequireType> {
			public AddRequireButton() {
				super("&cSelect RequireType to create", new ItemBuilder(Material.GLOWSTONE).setGuiProperty().build(), MissionEditor.this,
						Quests.get().getRequireManager().getMissionRequiresTypes(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lAdd Require", "&6Click to create a new Require");
			}

			@Override
			public List<String> getElementDescription(RequireType requireType) {
				List<String> list = new ArrayList<String>();
				list.add("&6" + requireType.getKey());
				list.addAll(requireType.getDescription());
				return list;
			}

			@Override
			public ItemStack getElementItem(RequireType requireType) {
				return new ItemBuilder(requireType.getGuiItemMaterial()).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(RequireType requireType) {
				Require require = Mission.this.addRequire(requireType);
				if (require != null)
					this.getTargetPlayer().openInventory(
							require.createEditorGui(getTargetPlayer(), MissionEditor.this).getInventory());
			}
		}

		private class DeleteCompleteRewardButton extends SelectOneElementButton<Reward> {

			public DeleteCompleteRewardButton() {
				super("&cSelect Complete Reward to delete", new ItemBuilder(Material.NETHERRACK).setGuiProperty().build(), MissionEditor.this,
						Mission.this.getCompleteRewards(), false, true, true);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDelete Reward", "&6Click to select a Reward");
			}

			@Override
			public List<String> getElementDescription(Reward reward) {
				return Arrays.asList("&6Require:", "&6" + reward.getInfo());
			}

			@Override
			public ItemStack getElementItem(Reward reward) {
				return new ItemBuilder(Material.PAPER).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Reward reward) {
				if (Mission.this.deleteCompleteReward(reward)) {
					MissionEditor.this.putButton(27, new CompleteRewardExplorerButton());
					MissionEditor.this.putButton(29, new DeleteCompleteRewardButton());
				}
			}
		}

		private class CompleteRewardExplorerButton extends SelectOneElementButton<Reward> {

			public CompleteRewardExplorerButton() {
				super("&cSelect Complete Reward to open", new ItemBuilder(Material.PAINTING).setGuiProperty().build(), MissionEditor.this,
						Mission.this.getCompleteRewards(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lOpen complete reward", "&6Click to select a complete reward");
			}

			@Override
			public List<String> getElementDescription(Reward reward) {
				return Arrays.asList("&6Reward:", "&6" + reward.getInfo());
			}

			@Override
			public ItemStack getElementItem(Reward reward) {
				return new ItemBuilder(Material.PAPER).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Reward reward) {
				this.getTargetPlayer()
						.openInventory(reward.createEditorGui(getTargetPlayer(), MissionEditor.this).getInventory());
			}
		}

		private class AddCompleteRewardButton extends SelectOneElementButton<RewardType> {
			public AddCompleteRewardButton() {
				super("&cSelect RewardType to create", new ItemBuilder(Material.GLOWSTONE).setGuiProperty().build(), MissionEditor.this,
						Quests.get().getRewardManager().getMissionRewardsTypes(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lAdd reward when mission is completed",
						"&6Click to create a new reward when mission is completed");
			}

			@Override
			public List<String> getElementDescription(RewardType rewardType) {
				List<String> list = new ArrayList<String>();
				list.add("&6" + rewardType.getKey());
				list.addAll(rewardType.getDescription());
				return list;
			}

			@Override
			public ItemStack getElementItem(RewardType rewardType) {
				return new ItemBuilder(rewardType.getGuiItemMaterial()).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(RewardType rewardType) {
				Reward reward = Mission.this.addCompleteReward(rewardType);
				if (reward != null)
					this.getTargetPlayer().openInventory(
							reward.createEditorGui(getTargetPlayer(), MissionEditor.this).getInventory());
			}
		}
	}

	@Override
	public QuestManager getQuestManager() {
		return getParent().getQuestManager();
	}
}
