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
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.gui.CustomGuiHolder;
import emanondev.quests.gui.CustomGuiItem;
import emanondev.quests.gui.CustomLinkedGuiHolder;
import emanondev.quests.gui.CustomMultiPageGuiHolder;
import emanondev.quests.gui.EditorGuiItemFactory;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.task.VoidTaskType;
import emanondev.quests.utils.DisplayState;
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
	
	
	protected static final String PATH_TASKS = "tasks";
	protected static final String PATH_REQUIRES = "requires";
	protected static final String PATH_START_REWARDS = "start-rewards";
	protected static final String PATH_COMPLETE_REWARDS = "complete-rewards";
	protected static final String PATH_START_TEXT = "start-text";
	protected static final String PATH_COMPLETE_TEXT = "complete-text";
	protected static final String PATH_PAUSE_TEXT = "pause-text";
	protected static final String PATH_UNPAUSE_TEXT = "unpause-text";
	protected static final String PATH_FAIL_TEXT = "fail-text";
	protected static final TaskType voidTaskType = new VoidTaskType();

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

	private final List<String> onStartText;
	private final List<String> onCompleteText;
	private final List<String> onPauseText;
	private final List<String> onUnpauseText;
	private final List<String> onFailText;

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
			if (this.isWorldListBlackList())
				comp.append("\n"+ChatColor.RED+"BlackListed "+ChatColor.DARK_AQUA+"Worlds:");
			else
				comp.append("\n"+ChatColor.GREEN+"WhiteListed "+ChatColor.DARK_AQUA+"Worlds:");
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
		fixDisplays();
		holders = setupHolders();
		if (displayInfo.isDirty())
			setDirty(true);
		this.addToEditor(new SubExplorerFactory<Task>(Task.class,getTasks()));
		this.addToEditor(new AddTaskFactory());
	}
	private class AddTaskFactory implements EditorGuiItemFactory {

		private class AddTaskButton extends CustomGuiItem {

			public AddTaskButton(CustomGuiHolder parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lAdd Task"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to add a new Task"));
				meta.setLore(lore);
				item.setItemMeta(meta);
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
			private class CreateTaskGui extends CustomLinkedGuiHolder<CustomGuiItem>{
				private TaskType taskType = null;
				public CreateTaskGui(Player p, CustomGuiHolder previusHolder) {
					super(p, previusHolder, 6);
					this.setFromEndCloseButtonPosition(8);
					this.items.put(20, new SelectTaskTypeButton());
					this.items.put(24, new CreateTaskButton());
					reloadInventory();
				}
				private class CreateTaskButton extends CustomGuiItem {
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
						ItemMeta meta = item.getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						if (taskType==null) {
							meta.setDisplayName(StringUtils.fixColorsAndHolders(
									"&cYou need to Select a Task Type"));
							item.setDurability((short) 14); 
						}
						else {
							meta.setDisplayName(StringUtils.fixColorsAndHolders(
									"&6&lClick to Create a new Task"));
							lore.add(StringUtils.fixColorsAndHolders(
									"&6Task Type: '&f"+taskType.getKey()));
							item.setDurability((short) 5); 
						}
						meta.setLore(lore);
						item.setItemMeta(meta);
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						if (taskType == null)
							return;
						String key = null;
						boolean found = false;
						int i = 1;
						do {
							key = "mission";
							if (i<10)
								key = key+"000"+i;
							else if (i<100)
								key = key+"00"+i;
							else if (i<1000)
								key = key+"0"+i;
							else
								key = key+i;
							if (!tasks.containsKey(key))
								found = true;
							i++;
						}while (i<10000 && found == false);
						if (found == false) {
							//TODO
							return;
						}
						if (!addTask(key,"New Task",taskType)) {
							//TODO
							return;
						}
						clicker.performCommand("questadmin quest "+Mission.this.getParent().getNameID()
								+" mission "+Mission.this.getNameID()+" task "+key+" editor");
					}
					
				}
				
				private class SelectTaskTypeButton extends CustomGuiItem {
					
					public SelectTaskTypeButton() {
						super(CreateTaskGui.this);
						update();
					}
					public void update() {
						ItemMeta meta = item.getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						if (taskType==null) {
							meta.setDisplayName(StringUtils.fixColorsAndHolders(
									"&6Click to select Task Type"));
						}
						else {
							meta.setDisplayName(StringUtils.fixColorsAndHolders(
									"&6Click to change Task Type"));
							lore.add(StringUtils.fixColorsAndHolders(
									"&7(CurrentTask Type: '"+taskType.getKey()+"'"));
						}
						meta.setLore(lore);
						item.setItemMeta(meta);
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
					
					private class TaskTypeSelectorGui extends CustomMultiPageGuiHolder<CustomGuiItem> {

						public TaskTypeSelectorGui(Player p, CustomGuiHolder previusHolder) {
							super(p, previusHolder, 6, 1);
							for (TaskType type : Quests.getInstance().getTaskManager().getTaskTypes()) {
								this.addButton(new TaskTypeButton(type));
							}
							reloadInventory();
						}
						private class TaskTypeButton extends CustomGuiItem {
							private TaskType type;
							public TaskTypeButton(TaskType type) {
								super(TaskTypeSelectorGui.this);
								this.type = type;
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(StringUtils.fixColorsAndHolders(
										"&6"+type.getKey()));
								item.setItemMeta(meta);
							}
							private ItemStack item = new ItemStack(Material.FIREBALL);
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
		public CustomGuiItem getCustomGuiItem(CustomGuiHolder parent) {
			return new AddTaskButton(parent);
		}
		
		
		
	}
	
	public String[] getHolders(Player p,DisplayState state) {
		String[] s;
		if (state!=DisplayState.COOLDOWN)
			s = new String[holders.size()*2];
		else {
			s = new String[holders.size()*2+2];
			s[s.length-2] = H.MISSION_COOLDOWN_LEFT;
			s[s.length-1] = StringUtils.getStringCooldown(Quests.getInstance().getPlayerManager()
					.getQuestPlayer(p).getCooldown(this));
		}
		for (int i =0; i < holders.size();i++) {
			s[i*2] = holders.get(i).getHolder();
			s[i*2+1] = holders.get(i).getReplacer(p);
		}
		
		return s;
	}
	
	protected ArrayList<ProgressHolder> setupHolders(){
		ArrayList<ProgressHolder> holders = new ArrayList<ProgressHolder>();
		ArrayList<Task> list = new ArrayList<Task>(tasks.values());
		list.forEach((task)->{holders.add( new ProgressHolder(task)); });
		return holders;
	}
	
	private ArrayList<ProgressHolder> holders;
	public class ProgressHolder {
		Task t;
		public ProgressHolder(Task task){
			this.t = task;
		}
		public String getHolder() {
			return H.MISSION_GENERIC_TASK_PROGRESS.replace("<task>", t.getNameID());
		}
		public String getReplacer(Player p) {
			return ""+Quests.getInstance().getPlayerManager().getQuestPlayer(p)
					.getTaskProgress(t);
		}
	}
	private void fixDisplays() {
		String[] holders = new String[tasks.size()*5*2+2];
		ArrayList<Task> list = new ArrayList<Task>(tasks.values());
		for (int i = 0; i < list.size(); i++) {
			holders[i*10] = H.MISSION_GENERIC_TASK_PROGRESS_DESCRIPTION.replace("<task>", list.get(i).getNameID());
			holders[i*10+1] = list.get(i).getProgressDescription();
			holders[i*10+2] = H.MISSION_GENERIC_TASK_UNSTARTED_DESCRIPTION.replace("<task>", list.get(i).getNameID());
			holders[i*10+3] = list.get(i).getUnstartedDescription();
			holders[i*10+4] = H.MISSION_GENERIC_TASK_NAME.replace("<task>", list.get(i).getNameID());
			holders[i*10+5] = list.get(i).getDisplayName();
			holders[i*10+6] = H.MISSION_GENERIC_TASK_TYPE.replace("<task>", list.get(i).getNameID());
			holders[i*10+7] = list.get(i).getTaskType().getKey();
			holders[i*10+8] = H.MISSION_GENERIC_TASK_MAX_PROGRESS.replace("<task>", list.get(i).getNameID());
			holders[i*10+9] = list.get(i).getMaxProgress()+"";
		}
		holders[holders.length-2] = H.MISSION_NAME;
		holders[holders.length-1] = getDisplayName();
		
		
		MissionDisplayInfo info = getDisplayInfo();
		for (DisplayState state : DisplayState.values()) {
			ArrayList<String> lore = new ArrayList<String>(info.getLore(state));
			for (int i = 0; i < lore.size(); i++) {
				if (lore.get(i).startsWith(H.MISSION_FOREACH_TASK)) {
					String text = lore.get(i).replace(H.MISSION_FOREACH_TASK,"");
					lore.remove(i);
					int j = 0;
					for (Task task:tasks.values()) {
						lore.add(i+j,text.replace("<task>",task.getNameID()));
						j++;
					}
					i = i+j-1;
				}
			}
			
			info.setLore(state, StringUtils.fixColorsAndHolders(
					lore,holders));
			info.setTitle(state, StringUtils.fixColorsAndHolders(
					info.getTitle(state),holders));
		}
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
	private List<String> loadStartText(MemorySection m){
		List<String> list = m.getStringList(PATH_START_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultStartText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private List<String> loadCompleteText(MemorySection m){
		List<String> list = m.getStringList(PATH_COMPLETE_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultCompleteText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private List<String> loadPauseText(MemorySection m){
		List<String> list = m.getStringList(PATH_PAUSE_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultPauseText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private List<String> loadUnpauseText(MemorySection m){
		List<String> list = m.getStringList(PATH_UNPAUSE_TEXT);
		if (list==null||list.isEmpty())
			list = Defaults.MissionDef.getDefaultUnpauseText();
		return StringUtils.fixColorsAndHolders(list,H.MISSION_NAME,getDisplayName());
	}
	private List<String> loadFailText(MemorySection m){
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
	protected boolean getUseWorldsAsBlackListDefault() {
		return  Defaults.MissionDef.getUseWorldsAsBlackListDefault();
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

	public String getGuiTitle() {
		return StringUtils.fixColorsAndHolders(
				"&8"+StringUtils.withoutColor(getDisplayName())+
				" <> "+StringUtils.withoutColor(parent.getDisplayName()));
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
	
}
