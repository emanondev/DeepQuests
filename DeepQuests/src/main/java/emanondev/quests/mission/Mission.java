package emanondev.quests.mission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.task.VoidTaskType;
import emanondev.quests.utils.MemoryUtils;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import emanondev.quests.utils.YmlLoadableWithCooldown;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Mission extends YmlLoadableWithCooldown{
	
	private static final String PATH_TASKS = "tasks";
	private static final String PATH_REQUIRES = "requires";
	private static final String PATH_START_REWARDS = "start-rewards";
	private static final String PATH_COMPLETE_REWARDS = "complete-rewards";
	private static final String PATH_START_TEXT = "start-text";
	private static final String PATH_COMPLETE_TEXT = "complete-text";
	private static final String PATH_PAUSE_TEXT = "pause-text";
	private static final String PATH_UNPAUSE_TEXT = "unpause-text";
	private static final String PATH_FAIL_TEXT = "fail-text";
	private static final TaskType voidTaskType = new VoidTaskType();

	private final Quest parent;
	
	public Quest getParent() {
		return parent;
	}
	@Override
	public void setDirty(boolean value) {
		super.setDirty(value);
		if (this.isDirty()==true)
			parent.setDirty(true);
		else if (this.isDirty()==false) {
			for (Task task : tasks.values())
				task.setDirty(false);
			this.displayInfo.setDirty(false);
		}
	}

	private ArrayList<String> onStartText;
	private ArrayList<String> onCompleteText;
	private ArrayList<String> onPauseText;
	private ArrayList<String> onUnpauseText;
	private ArrayList<String> onFailText;

	public BaseComponent[] getStartMessage(QuestPlayer p) {
		return YMLConfig.translateComponent(StringUtils
				.convertList(p.getPlayer(), onStartText));
	}
	public BaseComponent[] getCompleteMessage(QuestPlayer p) {
		return YMLConfig.translateComponent(StringUtils
				.convertList(p.getPlayer(), onCompleteText));
	}
	public BaseComponent[] getUnpauseMessage(QuestPlayer p) {
		return YMLConfig.translateComponent(StringUtils
				.convertList(p.getPlayer(), onUnpauseText));
	}
	public BaseComponent[] getPauseMessage(QuestPlayer p) {
		return YMLConfig.translateComponent(StringUtils
				.convertList(p.getPlayer(), onPauseText));
	}
	public BaseComponent[] getFailMessage(QuestPlayer p) {
		return YMLConfig.translateComponent(StringUtils
				.convertList(p.getPlayer(), onFailText));
	}


	private final LinkedHashMap<String,Task> tasks = new LinkedHashMap<String,Task>();
	public Collection<Task> getTasks(){
		return Collections.unmodifiableCollection(tasks.values());
	}
	public Task getTaskByNameID(String key) {
		return tasks.get(key);
	}


	private final List<MissionReward> completeRewards = new ArrayList<MissionReward>();
	private final List<MissionReward> startRewards = new ArrayList<MissionReward>();
	private final List<MissionRequire> requires = new ArrayList<MissionRequire>();
	public List<MissionReward> getStartRewards(){
		return Collections.unmodifiableList(startRewards);
	}
	public List<MissionReward> getCompleteRewards(){
		return Collections.unmodifiableList(completeRewards);
	}
	public List<MissionRequire> getRequires() {
		return Collections.unmodifiableList(requires);
	}


	private final MissionDisplayInfo displayInfo;

	@Override
	public MissionDisplayInfo getDisplayInfo() {
		return displayInfo;
	}
	public BaseComponent[] toComponent() {
		ComponentBuilder comp = new ComponentBuilder(
				""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Mission Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
				
		comp.append("\n"+ChatColor.DARK_AQUA+"ID: "
				+ChatColor.AQUA+this.getNameID()+"");
		comp.append("\n"+ChatColor.DARK_AQUA+"DisplayName: "
				+ChatColor.AQUA+this.getDisplayName());
		
		if (!this.isRepetable())
			comp.append("\n"+ChatColor.DARK_AQUA+"Repeatable: "+ChatColor.RED+"Disabled");
		else
			comp.append("\n"+ChatColor.DARK_AQUA+"Repeatable: "+ChatColor.GREEN+"Enabled");
		
		comp.append("\n"+ChatColor.DARK_AQUA+"Cooldown: "+ChatColor.YELLOW+(this.getCooldownTime()/60/1000)+" minutes\n");
		if (tasks.size() > 0) {
			comp.append("\n"+ChatColor.DARK_AQUA+"Tasks:");
			for (Task task : tasks.values()) {
				comp.append("\n"+ChatColor.AQUA+" - "+task.getNameID())
					.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
							"/qa quest "+this.parent.getNameID()+" mission "+this.getNameID()
							+" task "+task.getNameID()+ " info"))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(ChatColor.YELLOW+"Click for details")
							.create()));
			}
		}
		if (this.getWorldsList().size()>0) {
			if (this.isWorldListBlacklist())
				comp.append("\n"+ChatColor.RED+"Blacklisted "+ChatColor.DARK_AQUA+"Worlds:");
			else
				comp.append("\n"+ChatColor.GREEN+"Whitelisted "+ChatColor.DARK_AQUA+"Worlds:");
			for (String world : this.getWorldsList())
				comp.append("\n"+ChatColor.AQUA+" - "+world)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
						"/qa quest "+parent.getNameID()+" mission "
								+this.getNameID()+" worlds remove "+world))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(ChatColor.YELLOW+"Click to remove")
						.create()));
		}
		
		if (requires.size() > 0) {
			comp.append("\n"+ChatColor.DARK_AQUA+"Requires:");
			for (MissionRequire require : requires) {
				comp.append("\n"+ChatColor.AQUA+" - "+require.toText());
			}
		}

		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Mission Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		return comp.create();
	}
	public Mission(MemorySection m,Quest parent) {
		super(m);
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		LinkedHashMap<String,Task> tasks = loadTasks((MemorySection) m.get(PATH_TASKS));
		if (tasks!=null)
			this.tasks.putAll(tasks);
		for (Task task : tasks.values()) {
			if (isDirty())
				break;
			if (task.isDirty())
				setDirty(true);
		}
		List<MissionRequire> req = loadRequires(m);
		if (req !=null)
			this.requires.addAll(req);
		List<MissionReward> rew = loadStartRewards(m);
		if (rew !=null)
			this.startRewards.addAll(rew);
		rew = loadCompleteRewards(m);
		if (rew!=null)
			this.completeRewards.addAll(rew);

		onStartText = loadStartText(m);
		onCompleteText = loadCompleteText(m);
		onPauseText = loadPauseText(m);
		onUnpauseText = loadUnpauseText(m);
		onFailText = loadFailText(m);
		
		this.displayInfo = loadDisplayInfo(m);
		if (displayInfo.isDirty())
			setDirty(true);
		this.addToEditor(0,new SubExplorerFactory<Task>(Task.class,getTasks(),"&8Tasks List"));
		this.addToEditor(1,new AddTaskFactory());
		this.addToEditor(2,new DeleteTaskFactory());
	}
	public void reloadDisplay() {
		displayInfo.reloadDisplay();
	}
	
	
	
	private LinkedHashMap<String, Task> loadTasks(MemorySection m) {
		if (m==null)
			return new LinkedHashMap<String, Task>();
		Set<String> s = m.getKeys(false);
		LinkedHashMap<String,Task> map = new LinkedHashMap<String,Task>();
		s.forEach((key)->{
			try {
				Task task = Quests.getInstance().getTaskManager()
						.readTask(m.getString(key+"."+AbstractTask.PATH_TASK_TYPE),
								(MemorySection) m.get(key), this);
				map.put(task.getNameID(), task);
			} catch (Exception e) {
				e.printStackTrace();
				Quests.getInstance().getLoggerManager().getLogger("errors")
				.log("Error while loading Mission on file quests.yml '"
						+m.getCurrentPath()+"."+key+"' could not be read as valid task"
						,ExceptionUtils.getStackTrace(e));
				Task task = voidTaskType.getTaskInstance(m, this);
				map.put(task.getNameID(), task);
			}
		});
		return map;
	}
	private ArrayList<String> loadStartText(MemorySection m){
		List<String> list = m.getStringList(PATH_START_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultStartText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private ArrayList<String> loadCompleteText(MemorySection m){
		List<String> list = m.getStringList(PATH_COMPLETE_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultCompleteText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private ArrayList<String> loadPauseText(MemorySection m){
		List<String> list = m.getStringList(PATH_PAUSE_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultPauseText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private ArrayList<String> loadUnpauseText(MemorySection m){
		List<String> list = m.getStringList(PATH_UNPAUSE_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultUnpauseText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private ArrayList<String> loadFailText(MemorySection m){
		List<String> list = m.getStringList(PATH_FAIL_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultFailText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private MissionDisplayInfo loadDisplayInfo(MemorySection m) {
		return new MissionDisplayInfo(m,this);
	}
	private List<MissionRequire> loadRequires(MemorySection m) {
		List<String> l = MemoryUtils.getStringList(m, PATH_REQUIRES);
		
		return Quests.getInstance().getRequireManager().convertMissionRequires(l);
	}
	private List<MissionReward> loadStartRewards(MemorySection m) {
		List<String> l = MemoryUtils.getStringList(m, PATH_START_REWARDS);
		return Quests.getInstance().getRewardManager().convertMissionRewards(l);
	}
	private List<MissionReward> loadCompleteRewards(MemorySection m) {
		List<String> l = MemoryUtils.getStringList(m, PATH_COMPLETE_REWARDS);
		return Quests.getInstance().getRewardManager().convertMissionRewards(l);
	}
	@Override
	protected List<String> getWorldsListDefault() {
		return Defaults.MissionDef.getWorldsListDefault();
	}
	@Override
	protected boolean shouldWorldsAutogen() {
		return  Defaults.MissionDef.shouldWorldsAutogen();
	}
	@Override
	protected boolean getUseWorldsAsBlacklistDefault() {
		return  Defaults.MissionDef.getUseWorldsAsBlacklistDefault();
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
	
	public boolean addTask(String id,String displayName,TaskType taskType) {
		if (taskType == null || id == null || id.isEmpty() || 
				id.contains(" ")||id.contains(".")||id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_"," ");
		id = id.toLowerCase();
		if (tasks.containsKey(id))
			return false;
		getSection().set(PATH_TASKS+"."+id+"."+YmlLoadable.PATH_DISPLAY_NAME,displayName);
		getSection().set(PATH_TASKS+"."+id+"."+AbstractTask.PATH_TASK_TYPE,taskType.getKey());
		getSection().set(PATH_TASKS+"."+id+"."+AbstractTask.PATH_TASK_MAX_PROGRESS,1);
		Task t = Quests.getInstance().getTaskManager().readTask(taskType.getKey(), getSection(), this);
		tasks.put(t.getNameID(), t);
		parent.getParent().save();
		parent.getParent().reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}
	public boolean deleteTask(Task task) {
		if (task == null || !tasks.containsKey(task.getNameID()) )
			return false;
		getSection().set(PATH_TASKS+"."+task.getNameID(),null);
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
				StringUtils.setDescription(item,desc);
			}
			private ItemStack item = new ItemStack(Material.GLOWSTONE);
			@Override
			public ItemStack getItem() {
				return item;
			}
	
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new CreateTaskGui(clicker,this.getParent()).getInventory());
			}
			private class CreateTaskGui extends CustomLinkedGui<CustomButton>{
				private TaskType taskType = null;
				public CreateTaskGui(Player p, CustomGui previusHolder) {
					super(p, previusHolder, 6);
					this.setFromEndCloseButtonPosition(8);
					this.addButton(20, new SelectTaskTypeButton());
					this.addButton(24, new CreateTaskButton());
					reloadInventory();
				}
				private class CreateTaskButton extends CustomButton {
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
						if (taskType==null) {
							desc.add("&cYou need to Select a Task Type");
							item.setDurability((short) 14); 
						}
						else {
							desc.add("&6&lClick to Create a new Task");
							desc.add("&6Task Type: '&f"+taskType.getKey()+"&6'");
							item.setDurability((short) 5); 
						}
						StringUtils.setDescription(item, desc);
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						if (taskType == null)
							return;
						String key = QuestManager.getNewTaskID(Mission.this);
						if (!addTask(key,"New Task",taskType)) {
							//TODO
							return;
						}
						clicker.performCommand("questadmin quest "+Mission.this.getParent().getNameID()
								+" mission "+Mission.this.getNameID()+" task "+key+" editor");
					}
				}
				private class SelectTaskTypeButton extends CustomButton {
					
					public SelectTaskTypeButton() {
						super(CreateTaskGui.this);
						update();
					}
					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						if (taskType==null) {
							desc.add("&6Click to select Task Type");
						}
						else {
							desc.add("&6Click to change Task Type");
							desc.add("&7(CurrentTask Type: '"+taskType.getKey()+"')");
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
						clicker.openInventory(new TaskTypeSelectorGui(clicker,this.getParent()).getInventory());
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
								desc.add("&6"+type.getKey());
								desc.add("");
								desc.addAll(type.getDescription());
								StringUtils.setDescription(item,desc);
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
				StringUtils.setDescription(item,desc);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new DeleteQuestSelectorGui(clicker,getParent()).getInventory());
			}
			private class DeleteQuestSelectorGui extends CustomMultiPageGui<CustomButton> {
	
				public DeleteQuestSelectorGui(Player p,CustomGui previusHolder) {
					super(p,previusHolder,6,1);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&cSelect Task to delete"));
					for (Task task : getTasks()) {
						this.addButton(new SelectQuestButton(task));
					}
					this.setFromEndCloseButtonPosition(8);
					this.reloadInventory();
				}
				private class SelectQuestButton extends CustomButton{
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
						desc.add("&6Task: '&e"+task.getDisplayName()+"&6'");
						desc.add("&7("+task.getTaskType().getKey()+")");
						StringUtils.setDescription(item,desc);
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						clicker.openInventory(new DeleteConfirmationGui(clicker,getParent()).getInventory());
					}
					private class DeleteConfirmationGui extends CustomLinkedGui<CustomButton> {
	
						public DeleteConfirmationGui(Player p, CustomGui previusHolder) {
							super(p, previusHolder, 6);
							this.addButton(22,new ConfirmationButton());
							this.setFromEndCloseButtonPosition(8);
							this.setTitle(null,StringUtils.fixColorsAndHolders("&cConfirm Delete?"));
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
								desc.add("&6Task: '&e"+task.getDisplayName()+"&6'");
								desc.add("&7("+task.getTaskType().getKey()+")");
								StringUtils.setDescription(item,desc);
								
							}
							@Override
							public ItemStack getItem() {
								return item;
							}
							@Override
							public void onClick(Player clicker, ClickType click) {
								deleteTask(task);
								clicker.performCommand("questadmin quest "+Mission.this.getParent().getNameID()+" mission "+Mission.this.getNameID()+" editor");
							}
						}
					}
				}
			}
		}
	
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			if (tasks.size() >0 )
				return new DeleteTaskButton(parent);
			return null;
		}
	}
	
}
