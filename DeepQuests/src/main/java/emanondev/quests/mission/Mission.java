package emanondev.quests.mission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.AddApplyableFactory;
import emanondev.quests.gui.ApplyableExplorerFactory;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.DeleteApplyableFactory;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.gui.button.StringListEditorButtonFactory;
import emanondev.quests.gui.button.TextEditorButton;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.task.VoidTaskType;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import emanondev.quests.utils.YmlLoadableWithCooldown;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Mission extends YmlLoadableWithCooldown {

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

	private final Quest parent;

	private final LinkedHashMap<String, Task> tasks = new LinkedHashMap<String, Task>();
	private final LinkedHashMap<String, Reward> completeRewards = new LinkedHashMap<String, Reward>();
	private final LinkedHashMap<String, Reward> startRewards = new LinkedHashMap<String, Reward>();
	private final LinkedHashMap<String, Require> requires = new LinkedHashMap<String, Require>();
	private final MissionDisplayInfo displayInfo;
	private ArrayList<String> onStartText;
	private ArrayList<String> onCompleteText;
	private ArrayList<String> onPauseText;
	private ArrayList<String> onUnpauseText;
	private ArrayList<String> onFailText;

	public Mission(ConfigSection m, Quest parent) {
		super(m);
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		mayBePaused = getSection().getBoolean(PATH_CAN_PAUSE, false);
		LinkedHashMap<String, Task> tasks = loadTasks(getSection().loadSection(PATH_TASKS));
		if (tasks != null)
			this.tasks.putAll(tasks);
		for (Task task : tasks.values()) {
			if (isDirty())
				break;
			if (task.isDirty())
				this.dirty = true;
		}
		LinkedHashMap<String, Require> req = loadRequires();
		if (req != null)
			this.requires.putAll(req);
		LinkedHashMap<String, Reward> rew = loadStartRewards();
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
			this.dirty = true;
		this.addToEditor(0, new SubExplorerFactory<Task>(Task.class, getTasks(), "&8Tasks List"));
		this.addToEditor(1, new AddTaskFactory());
		this.addToEditor(2, new DeleteTaskFactory());
		this.addToEditor(18, new RequireExplorerFactory());
		this.addToEditor(19, new AddRequireFactory());
		this.addToEditor(20, new DeleteRequireFactory());
		this.addToEditor(27, new CompleteRewardExplorerFactory());
		this.addToEditor(28, new AddCompleteRewardFactory());
		this.addToEditor(29, new DeleteCompleteRewardFactory());
		this.addToEditor(31, new StartMessageButtonFactory());
		this.addToEditor(32, new CompleteMessageButtonFactory());
		this.addToEditor(33, new PauseMessageButtonFactory());
		this.addToEditor(34, new UnpauseMessageButtonFactory());
		this.addToEditor(35, new FailMessageButtonFactory());
	}

	private class CompleteRewardExplorerFactory extends ApplyableExplorerFactory<Reward> {
		public CompleteRewardExplorerFactory() {
			super("&9Complete Rewards");
		}

		@Override
		protected ArrayList<String> getExplorerButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSelect/Show complete rewards");
			desc.add("&6Click to Select a reward to edit");
			if (completeRewards.size() > 0)
				for (Reward reward : completeRewards.values())
					desc.add("&7" + reward.getInfo());
			return desc;
		}

		@Override
		protected Collection<Reward> getCollection() {
			return getCompleteRewards();
		}
	}

	private class RequireExplorerFactory extends ApplyableExplorerFactory<Require> {
		public RequireExplorerFactory() {
			super("&9Requires");
		}

		@Override
		protected ArrayList<String> getExplorerButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSelect/Show requires");
			desc.add("&6Click to Select a require to edit");
			if (requires.size() > 0)
				for (Require require : requires.values())
					desc.add("&7" + require.getInfo());
			return desc;
		}

		@Override
		protected Collection<Require> getCollection() {
			return getRequires();
		}
	}

	public Quest getParent() {
		return parent;
	}

	@Override
	public void setDirty(boolean value) {
		super.setDirty(value);
		if (this.isDirty() == true)
			parent.setDirty(true);
		else if (this.isDirty() == false) {
			for (Task task : tasks.values())
				task.setDirty(false);
			this.displayInfo.setDirty(false);
		}
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

	public Task getTaskByNameID(String key) {
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

	public BaseComponent[] toComponent() {
		ComponentBuilder comp = new ComponentBuilder("" + ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH
				+ "-----" + ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "[--" + ChatColor.BLUE
				+ "   Mission Info   " + ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--]"
				+ ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-----");

		comp.append("\n" + ChatColor.DARK_AQUA + "ID: " + ChatColor.AQUA + this.getNameID() + "");
		comp.append("\n" + ChatColor.DARK_AQUA + "DisplayName: " + ChatColor.AQUA + this.getDisplayName());

		if (!this.isRepetable())
			comp.append("\n" + ChatColor.DARK_AQUA + "Repeatable: " + ChatColor.RED + "Disabled");
		else
			comp.append("\n" + ChatColor.DARK_AQUA + "Repeatable: " + ChatColor.GREEN + "Enabled");

		comp.append("\n" + ChatColor.DARK_AQUA + "Cooldown: " + ChatColor.YELLOW + (this.getCooldownTime() / 60 / 1000)
				+ " minutes\n");
		if (tasks.size() > 0) {
			comp.append("\n" + ChatColor.DARK_AQUA + "Tasks:");
			for (Task task : tasks.values()) {
				comp.append("\n" + ChatColor.AQUA + " - " + task.getNameID())
						.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
								"/qa quest " + this.parent.getNameID() + " mission " + this.getNameID() + " task "
										+ task.getNameID() + " info"))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder(ChatColor.YELLOW + "Click for details").create()));
			}
		}
		if (this.getWorldsList().size() > 0) {
			if (this.isWorldListBlacklist())
				comp.append("\n" + ChatColor.RED + "Blacklisted " + ChatColor.DARK_AQUA + "Worlds:");
			else
				comp.append("\n" + ChatColor.GREEN + "Whitelisted " + ChatColor.DARK_AQUA + "Worlds:");
			for (String world : this.getWorldsList())
				comp.append("\n" + ChatColor.AQUA + " - " + world)
						.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
								"/qa quest " + parent.getNameID() + " mission " + this.getNameID() + " worlds remove "
										+ world))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder(ChatColor.YELLOW + "Click to remove").create()));
		}

		if (requires.size() > 0) {
			comp.append("\n" + ChatColor.DARK_AQUA + "Requires:");
			for (Require require : requires.values()) {
				comp.append("\n" + ChatColor.AQUA + " - " + require.getDescription());
			}
		}

		comp.append("\n" + ChatColor.BLUE + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-----" + ChatColor.GRAY
				+ ChatColor.BOLD + ChatColor.STRIKETHROUGH + "[--" + ChatColor.BLUE + "   Mission Info   "
				+ ChatColor.GRAY + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--]" + ChatColor.BLUE + ChatColor.BOLD
				+ ChatColor.STRIKETHROUGH + "-----");
		return comp.create();
	}

	public void reloadDisplay() {
		displayInfo.reloadDisplay();
	}

	private LinkedHashMap<String, Task> loadTasks(ConfigSection m) {
		if (m == null)
			return new LinkedHashMap<String, Task>();
		Set<String> s = m.getKeys(false);
		LinkedHashMap<String, Task> map = new LinkedHashMap<String, Task>();
		s.forEach((key) -> {
			try {
				Task task = Quests.getInstance().getTaskManager().readTask(
						m.getString(key + "." + AbstractTask.PATH_TASK_TYPE), m.loadSection(key), this);
				map.put(task.getNameID(), task);
			} catch (Exception e) {
				e.printStackTrace();
				Quests.getInstance().getLoggerManager().getLogger("errors")
						.log("Error while loading Mission on file quests.yml '" + m.getCurrentPath() + "." + key
								+ "' could not be read as valid task", ExceptionUtils.getStackTrace(e));
				Task task = voidTaskType.getTaskInstance(m, this);
				map.put(task.getNameID(), task);
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

	private ArrayList<String> loadPauseText( ) {
		List<String> list = getSection().getStringList( PATH_PAUSE_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultPauseText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private ArrayList<String> loadUnpauseText( ) {
		List<String> list = getSection().getStringList( PATH_UNPAUSE_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultUnpauseText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private ArrayList<String> loadFailText( ) {
		List<String> list = getSection().getStringList( PATH_FAIL_TEXT);
		if (list == null)
			list = Defaults.MissionDef.getDefaultFailText();
		if (list == null)
			list = new ArrayList<String>();
		return StringUtils.fixColorsAndHolders(list);
	}

	private MissionDisplayInfo loadDisplayInfo( ) {
		return new MissionDisplayInfo(getSection(), this);
	}

	private LinkedHashMap<String, Require> loadRequires() {
		return Quests.getInstance().getRequireManager().loadRequires(this, 
				getSection().loadSection(PATH_REQUIRES));
	}

	private LinkedHashMap<String, Reward> loadStartRewards() {
		return Quests.getInstance().getRewardManager().loadRewards(this, 
				getSection().loadSection(PATH_START_REWARDS));
	}

	private LinkedHashMap<String, Reward> loadCompleteRewards() {
		return Quests.getInstance().getRewardManager().loadRewards(this, 
				getSection().loadSection(PATH_COMPLETE_REWARDS));
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
	protected boolean shouldAutogenDisplayName() {
		return Defaults.MissionDef.shouldAutogenDisplayName();
	}

	@Override
	protected boolean getDefaultCooldownUse() {
		return Defaults.MissionDef.getDefaultCooldownUse();
	}

	@Override
	protected boolean shouldCooldownAutogen() {
		return Defaults.MissionDef.shouldCooldownAutogen();
	}

	@Override
	protected long getDefaultCooldownMinutes() {
		return Defaults.MissionDef.getDefaultCooldownMinutes();
	}

	public boolean addTask(String id, String displayName, TaskType taskType) {
		if (taskType == null || id == null || id.isEmpty() || id.contains(" ") || id.contains(".") || id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_", " ");
		id = id.toLowerCase();
		if (tasks.containsKey(id))
			return false;
		getSection().set(PATH_TASKS + "." + id + "." + YmlLoadable.PATH_DISPLAY_NAME, displayName);
		getSection().set(PATH_TASKS + "." + id + "." + AbstractTask.PATH_TASK_TYPE, taskType.getKey());
		getSection().set(PATH_TASKS + "." + id + "." + AbstractTask.PATH_TASK_MAX_PROGRESS, 1);
		Task t = Quests.getInstance().getTaskManager().readTask(taskType.getKey(), getSection(), this);
		tasks.put(t.getNameID(), t);
		parent.getParent().save();
		parent.getParent().reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public boolean deleteTask(Task task) {
		if (task == null || !tasks.containsKey(task.getNameID()))
			return false;
		getSection().set(PATH_TASKS + "." + task.getNameID(), null);
		tasks.remove(task.getNameID());
		parent.getParent().save();
		parent.getParent().reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	private class AddTaskFactory implements EditorButtonFactory {

		private class AddTaskButton extends CustomButton {

			public AddTaskButton(CustomGui parent) {
				super(parent);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lAdd Task");
				desc.add("&6Click to add a new Task");
				StringUtils.setDescription(item, desc);
			}

			private ItemStack item = new ItemStack(Material.GLOWSTONE);

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new CreateTaskGui(clicker, this.getParent()).getInventory());
			}

			private class CreateTaskGui extends CustomLinkedGui<CustomButton> {
				private TaskType taskType = null;
				private String displayName = null;

				public CreateTaskGui(Player p, CustomGui previusHolder) {
					super(p, previusHolder, 6);
					this.setFromEndCloseButtonPosition(8);
					this.addButton(20, new SelectTaskTypeButton());
					this.addButton(24, new CreateTaskButton());
					reloadInventory();
				}

				private class CreateTaskButton extends TextEditorButton {
					private ItemStack item = new ItemStack(Material.WOOL);

					public CreateTaskButton() {
						super(CreateTaskGui.this);
						update();
					}

					@Override
					public ItemStack getItem() {
						return item;
					}

					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						if (taskType == null) {
							item.setDurability((short) 14);
							desc.add("&cYou need to Select a Task Type");
						} else {
							desc.add("&6&lClick Select a Display Name");
							desc.add("&6Task Type: '&f" + taskType.getKey() + "&6'");
							item.setDurability((short) 0);
							item.setType(Material.NAME_TAG);
						}
						StringUtils.setDescription(item, desc);
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						if (taskType == null)
							return;
						if (displayName == null) {
							this.requestText(clicker, null, setDisplayNameDescription);
							return;
						}
						clicker.performCommand("questadmin quest " + Mission.this.getParent().getNameID() + " mission "
								+ Mission.this.getNameID() + " task " + key + " editor");
					}

					private String key = null;

					@Override
					public void onReicevedText(String text) {
						if (text == null || text.isEmpty()) {
							CreateTaskGui.this.getPlayer()
									.sendMessage(StringUtils.fixColorsAndHolders("&cInvalid Name"));
							return;
						}
						displayName = text;
						key = Mission.this.getParent().getParent().getNewTaskID(Mission.this);
						if (!addTask(key, displayName, taskType)) {
							return;
						}
						Bukkit.getScheduler().runTaskLater(Quests.getInstance(), new Runnable() {
							@Override
							public void run() {
								CreateTaskGui.this.getPlayer()
										.performCommand("questadmin quest " + Mission.this.getParent().getNameID()
												+ " mission " + Mission.this.getNameID() + " task " + key + " editor");
							}
						}, 2);
					}
				}

				private class SelectTaskTypeButton extends CustomButton {

					public SelectTaskTypeButton() {
						super(CreateTaskGui.this);
						update();
					}

					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						if (taskType == null) {
							desc.add("&6Click to select Task Type");
						} else {
							desc.add("&6Click to change Task Type");
							desc.add("&7(CurrentTask Type: '" + taskType.getKey() + "')");
						}
						StringUtils.setDescription(item, desc);
					}

					private ItemStack item = new ItemStack(Material.FIREBALL);

					@Override
					public ItemStack getItem() {
						return item;
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						clicker.openInventory(new TaskTypeSelectorGui(clicker, this.getParent()).getInventory());
					}

					private class TaskTypeSelectorGui extends CustomMultiPageGui<CustomButton> {

						public TaskTypeSelectorGui(Player p, CustomGui previusHolder) {
							super(p, previusHolder, 6, 1);
							for (TaskType type : Quests.getInstance().getTaskManager().getTaskTypes()) {
								this.addButton(new TaskTypeButton(type));
							}
							reloadInventory();
						}

						private class TaskTypeButton extends CustomButton {
							private TaskType type;

							public TaskTypeButton(TaskType type) {
								super(TaskTypeSelectorGui.this);
								this.type = type;
								item = new ItemStack(type.getGuiItemMaterial());
								ArrayList<String> desc = new ArrayList<String>();
								desc.add("&6" + type.getKey());
								desc.add("");
								desc.addAll(type.getDescription());
								StringUtils.setDescription(item, desc);
							}

							private ItemStack item;

							@Override
							public ItemStack getItem() {
								return item;
							}

							@Override
							public void onClick(Player clicker, ClickType click) {
								CreateTaskGui.this.taskType = type;
								CreateTaskGui.this.update();
								clicker.openInventory(CreateTaskGui.this.getInventory());
							}
						}
					}
				}
			}
		}

		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new AddTaskButton(parent);
		}
	}

	private class DeleteTaskFactory implements EditorButtonFactory {
		private class DeleteTaskButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.NETHERRACK);

			public DeleteTaskButton(CustomGui parent) {
				super(parent);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lDelete Task");
				desc.add("&6Click to select a Task");
				StringUtils.setDescription(item, desc);
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new DeleteQuestSelectorGui(clicker, getParent()).getInventory());
			}

			private class DeleteQuestSelectorGui extends CustomMultiPageGui<CustomButton> {

				public DeleteQuestSelectorGui(Player p, CustomGui previusHolder) {
					super(p, previusHolder, 6, 1);
					this.setTitle(null, StringUtils.fixColorsAndHolders("&cSelect Task to delete"));
					for (Task task : getTasks()) {
						this.addButton(new SelectQuestButton(task));
					}
					this.setFromEndCloseButtonPosition(8);
					this.reloadInventory();
				}

				private class SelectQuestButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.BOOK);
					private Task task;

					public SelectQuestButton(Task task) {
						super(DeleteQuestSelectorGui.this);
						this.task = task;
						this.update();
					}

					@Override
					public ItemStack getItem() {
						return item;
					}

					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&6Task: '&e" + task.getDisplayName() + "&6'");
						desc.add("&7(" + task.getTaskType().getKey() + ")");
						StringUtils.setDescription(item, desc);
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						clicker.openInventory(new DeleteConfirmationGui(clicker, getParent()).getInventory());
					}

					private class DeleteConfirmationGui extends CustomLinkedGui<CustomButton> {

						public DeleteConfirmationGui(Player p, CustomGui previusHolder) {
							super(p, previusHolder, 6);
							this.addButton(22, new ConfirmationButton());
							this.setFromEndCloseButtonPosition(8);
							this.setTitle(null, StringUtils.fixColorsAndHolders("&cConfirm Delete?"));
							reloadInventory();
						}

						private class ConfirmationButton extends CustomButton {
							private ItemStack item = new ItemStack(Material.WOOL);

							public ConfirmationButton() {
								super(DeleteConfirmationGui.this);
								this.item.setDurability((short) 14);
								ArrayList<String> desc = new ArrayList<String>();
								desc.add("&cClick to Confirm quest Delete");
								desc.add("&cQuest delete can't be undone");
								desc.add("");
								desc.add("&6Task: '&e" + task.getDisplayName() + "&6'");
								desc.add("&7(" + task.getTaskType().getKey() + ")");
								StringUtils.setDescription(item, desc);

							}

							@Override
							public ItemStack getItem() {
								return item;
							}

							@Override
							public void onClick(Player clicker, ClickType click) {
								deleteTask(task);
								clicker.performCommand("questadmin quest " + Mission.this.getParent().getNameID()
										+ " mission " + Mission.this.getNameID() + " editor");
							}
						}
					}
				}
			}
		}

		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			if (tasks.size() > 0)
				return new DeleteTaskButton(parent);
			return null;
		}
	}

	private final static BaseComponent[] setDisplayNameDescription = new ComponentBuilder(
			ChatColor.GOLD + "Click suggest the command\n\n" + ChatColor.GOLD + "Set the display name for the task\n"
					+ ChatColor.YELLOW + "/questtext <display name>").create();

	private class AddRequireFactory extends AddApplyableFactory<RequireType> implements EditorButtonFactory {
		public AddRequireFactory() {
			super("&8Select a Require Type");
		}

		@Override
		protected Collection<RequireType> getCollection() {
			return Quests.getInstance().getRequireManager().getMissionRequiresTypes();
		}

		@Override
		protected ArrayList<String> getAddButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&a&lAdd &6&lNew Require");
			desc.add("&6Click to create new Require");
			return desc;
		}

		@Override
		protected ArrayList<String> getTypeButtonDescription(RequireType type) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Click to add a require of type:");
			desc.add("&e" + type.getKey());
			desc.addAll(type.getDescription());
			return desc;
		}

		@Override
		protected ItemStack getTypeButtonItemStack(RequireType type) {
			ItemStack item = new ItemStack(type.getGuiItemMaterial());
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		protected void onAdd(RequireType type) {
			addRequire(type);
		}
	}

	private class DeleteRequireFactory extends DeleteApplyableFactory<Require> implements EditorButtonFactory {
		public DeleteRequireFactory() {
			super("&cSelect Require to delete", "&cConfirm Delete?");
		}

		@Override
		protected Collection<Require> getCollection() {
			return requires.values();
		}

		@Override
		protected ArrayList<String> getDeleteButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&c&lDelete &6&lRequire");
			desc.add("&6Click to select and delete a Require");
			return desc;
		}

		@Override
		protected ArrayList<String> getSelectButtonDescription(Require req) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Require:");
			desc.add("&6" + req.getInfo());
			return desc;
		}

		@Override
		protected ItemStack getSelectedButtonItemStack(Require req) {
			ItemStack item = new ItemStack(req.getType().getGuiItemMaterial());
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		protected ArrayList<String> getConfirmationButtonDescription(Require req) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&cClick to Confirm quest Delete");
			desc.add("&cRequire delete can't be undone");
			desc.add("");
			desc.add("&6Require:");
			desc.add("&6" + req.getInfo());
			return desc;
		}

		@Override
		protected void onDelete(Require req) {
			deleteRequire(req);
		}
	}

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
		requires.put(req.getNameID(), req);
		setDirty(true);
		return req;
	}

	public boolean deleteRequire(Require req) {
		if (req == null || !requires.containsKey(req.getNameID()) || !requires.get(req.getNameID()).equals(req))
			return false;
		getSection().set(PATH_REQUIRES + "." + req.getNameID(), null);
		requires.remove(req.getNameID());
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
		completeRewards.put(rew.getNameID(), rew);
		setDirty(true);
		return rew;
	}

	public boolean deleteCompleteReward(Reward rew) {
		if (rew == null || !completeRewards.containsKey(rew.getNameID())
				|| !completeRewards.get(rew.getNameID()).equals(rew))
			return false;
		getSection().set(PATH_COMPLETE_REWARDS + "." + rew.getNameID(), null);
		completeRewards.put(rew.getNameID(), rew);
		setDirty(true);
		return true;
	}

	private class AddCompleteRewardFactory extends AddApplyableFactory<RewardType> implements EditorButtonFactory {

		public AddCompleteRewardFactory() {
			super("&8Select a CompleteReward Type");
		}

		@Override
		protected Collection<RewardType> getCollection() {
			return Quests.getInstance().getRewardManager().getMissionRewardsTypes();
		}

		@Override
		protected ArrayList<String> getAddButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&a&lAdd &6&lNew CompleteReward");
			desc.add("&6Click to create new CompleteReward");
			return desc;
		}

		@Override
		protected ArrayList<String> getTypeButtonDescription(RewardType type) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Click to add a reward of type:");
			desc.add("&e" + type.getKey());
			desc.addAll(type.getDescription());
			return desc;
		}

		@Override
		protected ItemStack getTypeButtonItemStack(RewardType type) {
			ItemStack item = new ItemStack(type.getGuiItemMaterial());
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		protected void onAdd(RewardType type) {
			addCompleteReward(type);
		}
	}

	private class DeleteCompleteRewardFactory extends DeleteApplyableFactory<Reward> implements EditorButtonFactory {
		public DeleteCompleteRewardFactory() {
			super("&cSelect Reward to delete", "&cConfirm Delete?");
		}

		@Override
		protected Collection<Reward> getCollection() {
			return completeRewards.values();
		}

		@Override
		protected ArrayList<String> getDeleteButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&c&lDelete &6&lReward");
			desc.add("&6Click to select and delete a Reward");
			return desc;
		}

		@Override
		protected ArrayList<String> getSelectButtonDescription(Reward rew) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Reward:");
			desc.add("&6" + rew.getInfo());
			return desc;
		}

		@Override
		protected ItemStack getSelectedButtonItemStack(Reward rew) {
			ItemStack item = new ItemStack(rew.getType().getGuiItemMaterial());
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		protected ArrayList<String> getConfirmationButtonDescription(Reward rew) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&cClick to Confirm Delete");
			desc.add("&cReward delete can't be undone");
			desc.add("");
			desc.add("&6Reward:");
			desc.add("&6" + rew.getInfo());
			return desc;
		}

		@Override
		protected void onDelete(Reward rew) {
			deleteCompleteReward(rew);
		}
	}

	private boolean mayBePaused;

	public boolean mayBePaused() {
		return mayBePaused;
	}

	public boolean setStartMessage(ArrayList<String> value) {
		if (value == null)
			return false;
		this.onStartText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_START_TEXT, onStartText);
		this.setDirty(true);
		return true;
	}

	public boolean setCompleteMessage(ArrayList<String> value) {
		if (value == null)
			return false;
		this.onCompleteText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_COMPLETE_TEXT, onCompleteText);
		this.setDirty(true);
		return true;
	}

	public boolean setUnpauseMessage(ArrayList<String> value) {
		if (value == null)
			return false;
		this.onUnpauseText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_UNPAUSE_TEXT, onUnpauseText);
		this.setDirty(true);
		return true;
	}

	public boolean setPauseMessage(ArrayList<String> value) {
		if (value == null)
			return false;
		this.onPauseText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_PAUSE_TEXT, onPauseText);
		this.setDirty(true);
		return true;
	}

	public boolean setFailMessage(ArrayList<String> value) {
		if (value == null)
			return false;
		this.onFailText = StringUtils.fixColorsAndHolders(value);
		getSection().set(PATH_FAIL_TEXT, onFailText);
		this.setDirty(true);
		return true;
	}

	private class StartMessageButtonFactory extends StringListEditorButtonFactory {
		public StartMessageButtonFactory() {
			super(startButtonDesc, "&8Start Message editor", Material.STAINED_CLAY, 5);
		}

		@Override
		protected List<String> getStringList() {
			return onStartText;
		}

		@Override
		protected void onChange(ArrayList<String> newList) {
			setStartMessage(newList);
		}
	}

	private class CompleteMessageButtonFactory extends StringListEditorButtonFactory {
		public CompleteMessageButtonFactory() {
			super(completeButtonDesc, "&8Complete Message editor", Material.STAINED_CLAY, 13);
		}

		@Override
		protected List<String> getStringList() {
			return onCompleteText;
		}

		@Override
		protected void onChange(ArrayList<String> newList) {
			setCompleteMessage(newList);
		}
	}

	private class PauseMessageButtonFactory extends StringListEditorButtonFactory {
		public PauseMessageButtonFactory() {
			super(pauseButtonDesc, "&8Pause Message editor", Material.STAINED_CLAY, 4);
		}

		@Override
		protected List<String> getStringList() {
			return onPauseText;
		}

		@Override
		protected void onChange(ArrayList<String> newList) {
			setPauseMessage(newList);
		}
	}

	private class UnpauseMessageButtonFactory extends StringListEditorButtonFactory {
		public UnpauseMessageButtonFactory() {
			super(unpauseButtonDesc, "&8Unpause Message editor", Material.STAINED_CLAY, 1);
		}

		@Override
		protected List<String> getStringList() {
			return onUnpauseText;
		}

		@Override
		protected void onChange(ArrayList<String> newList) {
			setUnpauseMessage(newList);
		}
	}

	private class FailMessageButtonFactory extends StringListEditorButtonFactory {
		public FailMessageButtonFactory() {
			super(failButtonDesc, "&8Fail Message editor", Material.STAINED_CLAY, 14);
		}

		@Override
		protected List<String> getStringList() {
			return onFailText;
		}

		@Override
		protected void onChange(ArrayList<String> newList) {
			setFailMessage(newList);
		}
	}

	private final static List<String> startButtonDesc = Arrays.asList("&6&lStart Message Editor",
			"&6The message sended when the mission is started", "&6Click to edit", "&6Current value:");
	private final static List<String> completeButtonDesc = Arrays.asList("&6&lComplete Message Editor",
			"&6The message sended when the mission is completed", "&6Click to edit", "&6Current value:");
	private final static List<String> pauseButtonDesc = Arrays.asList("&6&lPause Message Editor",
			"&6The message sended when the mission is paused", "&6Click to edit", "&6Current value:");
	private final static List<String> unpauseButtonDesc = Arrays.asList("&6&lUnpause Message Editor",
			"&6The message sended when the mission is unpaused", "&6Click to edit", "&6Current value:");
	private final static List<String> failButtonDesc = Arrays.asList("&6&lFail Message Editor",
			"&6The message sended when the mission is failed", "&6Click to edit", "&6Current value:");

}
