package emanondev.quests.mission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
import emanondev.quests.gui.MissionRequireExplorerFactory;
import emanondev.quests.gui.TextEditorButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.require.MissionRequireType;
import emanondev.quests.reward.MissionReward;
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
	
	private final LinkedHashMap<String,Task> tasks = new LinkedHashMap<String,Task>();
	private final List<MissionReward> completeRewards = new ArrayList<MissionReward>();
	private final List<MissionReward> startRewards = new ArrayList<MissionReward>();
	private final LinkedHashMap<String,MissionRequire> requires = new LinkedHashMap<String,MissionRequire>();
	private final MissionDisplayInfo displayInfo;
	private ArrayList<String> onStartText;
	private ArrayList<String> onCompleteText;
	private ArrayList<String> onPauseText;
	private ArrayList<String> onUnpauseText;
	private ArrayList<String> onFailText;

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
		LinkedHashMap<String,MissionRequire> req = loadRequires(m);
		if (req !=null)
			this.requires.putAll(req);
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
		this.addToEditor(18, new MissionRequireExplorerFactory(requires.values(),"&8Requires"));
		this.addToEditor(19, new AddRequireFactory());
		this.addToEditor(20, new DeleteRequireFactory());
	}
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


	public Collection<Task> getTasks(){
		return Collections.unmodifiableCollection(tasks.values());
	}
	public Task getTaskByNameID(String key) {
		return tasks.get(key);
	}
	public List<MissionReward> getStartRewards(){
		return Collections.unmodifiableList(startRewards);
	}
	public List<MissionReward> getCompleteRewards(){
		return Collections.unmodifiableList(completeRewards);
	}
	public Collection<MissionRequire> getRequires() {
		return Collections.unmodifiableCollection(requires.values());
	}
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
			for (MissionRequire require : requires.values()) {
				comp.append("\n"+ChatColor.AQUA+" - "+require.getDescription());
			}
		}

		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Mission Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		return comp.create();
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
	private LinkedHashMap<String,MissionRequire> loadRequires(MemorySection m) {
		return Quests.getInstance().getRequireManager()
				.loadRequires(this, (MemorySection) m.get(PATH_REQUIRES));
	}
	private List<MissionReward> loadStartRewards(MemorySection m) {
		return Quests.getInstance().getRewardManager()
				.loadRewards(this, (MemorySection) m.get(PATH_START_REWARDS));
	}
	private List<MissionReward> loadCompleteRewards(MemorySection m) {
		return Quests.getInstance().getRewardManager()
				.loadRewards(this, (MemorySection) m.get(PATH_COMPLETE_REWARDS));
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
						if (taskType==null) {
							item.setDurability((short) 14); 
							desc.add("&cYou need to Select a Task Type");
						}
						else {
							desc.add("&6&lClick Select a Display Name");
							desc.add("&6Task Type: '&f"+taskType.getKey()+"&6'");
							item.setDurability((short) 0); 
							item.setType(Material.NAME_TAG); 
						}
						StringUtils.setDescription(item, desc);
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						if (taskType == null)
							return;
						if (displayName==null) {
							this.requestText(clicker, null, setDisplayNameDescription);
							return;
						}
						clicker.performCommand("questadmin quest " + Mission.this.getParent().getNameID() +" mission "+Mission.this.getNameID()+" task "+key+" editor");
					}
					private String key = null;

					@Override
					public void onReicevedText(String text) {
						if (text==null || text.isEmpty()) {
							CreateTaskGui.this.getPlayer().sendMessage(
									StringUtils.fixColorsAndHolders("&cInvalid Name"));
							return;
						}
						displayName = text;
						key = QuestManager.getNewTaskID(Mission.this);
						if (!addTask(key,displayName,taskType)) {
							return;
						}
						Bukkit.getScheduler().runTaskLater(Quests.getInstance(), new Runnable() {
							@Override
							public void run() {
								CreateTaskGui.this.getPlayer().performCommand("questadmin quest "+
											Mission.this.getParent().getNameID()+" mission "+
											Mission.this.getNameID()+" task "+
											key+" editor");
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

	private final static BaseComponent[] setDisplayNameDescription = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command\n\n"+
			ChatColor.GOLD+"Set the display name for the task\n"+
			ChatColor.YELLOW+"/questtext <display name>"
			).create();
	private class AddRequireFactory implements EditorButtonFactory{
		private class AddRequireGuiItem extends CustomButton {
			private ItemStack item = new ItemStack(Material.GLOWSTONE);
	
			public AddRequireGuiItem(CustomGui parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&a&lAdd &6&lNew Require"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to create new Require"));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new CreateRequireGui(clicker,this.getParent()).getInventory());
			}
			private class CreateRequireGui extends CustomMultiPageGui<CustomButton>{
				public CreateRequireGui(Player p, CustomGui previusHolder) {
					super(p, previusHolder, 6,1);
					this.setFromEndCloseButtonPosition(8);
					for (MissionRequireType type : Quests.getInstance().getRequireManager().getMissionRequiresTypes())
						this.addButton(new MissionRequireTypeButton(type));
					this.setTitle(null,StringUtils.fixColorsAndHolders("&8Select a Require Type"));
					reloadInventory();
				}
				private class MissionRequireTypeButton extends CustomButton {
					private final ItemStack item;
					private final MissionRequireType type;
					public MissionRequireTypeButton(MissionRequireType type) {
						super(CreateRequireGui.this.getPreviusHolder());
						item = new ItemStack(type.getGuiItemMaterial());
						this.type = type;
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&6Click to add a require of type:");
						desc.add("&e"+type.getKey());
						desc.addAll(type.getDescription());
						StringUtils.setDescription(item, desc);
					}
					
					@Override
					public ItemStack getItem() {
						return item;
					}
					
					@Override
					public void onClick(Player clicker, ClickType click) {
						MissionRequire req = Mission.this.addRequire(type);
						if (req==null) {
							//TODO
							return;
						}
						req.openEditorGui(clicker, AddRequireGuiItem.this.getParent());
					}
				}
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new AddRequireGuiItem(parent);
		}
	}
	private class DeleteRequireFactory implements EditorButtonFactory {
		private class DeleteRequireButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.NETHERRACK);
			public DeleteRequireButton(CustomGui parent) {
				super(parent);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&c&lDelete &6&lRequire");
				desc.add("&6Click to select and delete a Require");
				StringUtils.setDescription(item,desc);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new DeleteRequireSelectorGui(clicker,getParent()).getInventory());
			}
			private class DeleteRequireSelectorGui extends CustomMultiPageGui<CustomButton> {
	
				public DeleteRequireSelectorGui(Player p,CustomGui previusHolder) {
					super(p,previusHolder,6,1);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&cSelect Require to delete"));
					for (MissionRequire req : getRequires()) {
						this.addButton(new SelectRequireButton(req));
					}
					this.setFromEndCloseButtonPosition(8);
					this.reloadInventory();
				}
				private class SelectRequireButton extends CustomButton{
					private ItemStack item = new ItemStack(Material.BOOK);
					private MissionRequire req;
					
					public SelectRequireButton(MissionRequire req) {
						super(DeleteRequireSelectorGui.this);
						this.req = req;
						this.update();
					}
					@Override
					public ItemStack getItem() {
						return item;
					}
					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&6Require:");
						desc.add("&6"+req.getDescription());
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
							this.setTitle(null,StringUtils.fixColorsAndHolders("&cConfirm Delete?"));
							this.setFromEndCloseButtonPosition(8);
							reloadInventory();
						}
						
						private class ConfirmationButton extends CustomButton {
							private ItemStack item = new ItemStack(Material.WOOL);
							public ConfirmationButton() {
								super(DeleteConfirmationGui.this);
								this.item.setDurability((short) 14);
								ArrayList<String> desc = new ArrayList<String>();
								desc.add("&cClick to Confirm quest Delete");
								desc.add("&cRequire delete can't be undone");
								desc.add("");
								desc.add("&6Require:");
								desc.add("&6"+req.getDescription());
								StringUtils.setDescription(item,desc);
							}
							@Override
							public ItemStack getItem() {
								return item;
							}
							@Override
							public void onClick(Player clicker, ClickType click) {
								deleteRequire(req);
								clicker.openInventory(DeleteRequireButton.this.getParent().getInventory());
							}
						}
					}
				}
			}
		}
	
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			if (requires.size() >0)
				return new DeleteRequireButton(parent);
			return null;
		}
	}
	private final static String PATH_REQUIRE_TYPE = "type";
	public MissionRequire addRequire(MissionRequireType type) {
		if (type==null)
			return null;
		String key = null;
		int i = 0;
		do {
			key = "rq"+i;
			i++;
		} while (requires.containsKey(key));
		getSection().set(PATH_REQUIRES+"."+key+"."+PATH_REQUIRE_TYPE,type.getKey());
		MissionRequire req = type.getRequireInstance((MemorySection) getSection().get(PATH_REQUIRES+"."+key), this);
		setDirty(true);
		return req;
	}
	public boolean deleteRequire(MissionRequire req) {
		if (req == null)
			return false;
		getSection().set(PATH_REQUIRES+"."+req.getNameID(),null);
		setDirty(true);
		return true;
	}

}
