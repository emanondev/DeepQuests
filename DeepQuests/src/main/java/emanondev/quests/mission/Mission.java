package emanondev.quests.mission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.MemoryUtils;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadableWithDisplay;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Mission extends YmlLoadableWithDisplay{
	private final Quest parent;
	public static final String PATH_TASKS = "tasks";
	public static final String PATH_REQUIRES = "requires";
	private static final String PATH_START_REWARDS = "start-rewards";
	private static final String PATH_COMPLETE_REWARDS = "complete-rewards";
	private static final String PATH_START_TEXT = "start-text";
	private static final String PATH_COMPLETE_TEXT = "complete-text";
	private static final String PATH_PAUSE_TEXT = "pause-text";
	private static final String PATH_UNPAUSE_TEXT = "unpause-text";
	private static final String PATH_FAIL_TEXT = "fail-text";

	
	public Mission(MemorySection m,Quest parent) {
		super(m);
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		LinkedHashMap<String,Task> tasks = loadTasks((MemorySection) m.get(PATH_TASKS));
		if (tasks!=null)
			this.tasks.putAll(tasks);
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
		if (displayInfo.shouldSave())
			shouldSave = true;
		
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
	
	public Collection<Task> getTasks(){
		return Collections.unmodifiableCollection(tasks.values());
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
	
	protected LinkedHashMap<String, Task> loadTasks(MemorySection m) {
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
				shouldSave = shouldSave || task.shouldSave();
			} catch (Exception e) {
				Quests.getInstance().getLoggerManager().getLogger("errors")
				.log("Error while loading Mission on file quests.yml '"
						+m.getCurrentPath()+"."+key+"' could not be read as valid task"
						,ExceptionUtils.getStackTrace(e));
			}
		});
		return map;
	}
	public Quest getParent() {
		return parent;
	}
	private final List<MissionReward> completeRewards = new ArrayList<MissionReward>();
	private final List<MissionReward> startRewards = new ArrayList<MissionReward>();
	private final List<MissionRequire> requires = new ArrayList<MissionRequire>();
	private final LinkedHashMap<String,Task> tasks = new LinkedHashMap<String,Task>();
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
	public List<MissionReward> getStartRewards(){
		return Collections.unmodifiableList(startRewards);
	}
	public List<MissionReward> getCompleteRewards(){
		return Collections.unmodifiableList(completeRewards);
	}
	
	@Override
	protected MissionDisplayInfo loadDisplayInfo(MemorySection m) {
		return new MissionDisplayInfo(m,this);
	}
	@Override
	public MissionDisplayInfo getDisplayInfo() {
		return displayInfo;
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
	protected int getDefaultCooldownMinutes() {
		return Defaults.MissionDef.getDefaultCooldownMinutes();
	}
	@Override
	protected String getDisplayNameDefaultPrefix() {
		return Defaults.MissionDef.getDisplayNameDefaultPrefix();
	}
	public List<MissionRequire> getRequires() {
		return Collections.unmodifiableList(requires);
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
	private final MissionDisplayInfo displayInfo;

	public BaseComponent[] toComponent() {
		ComponentBuilder comp = new ComponentBuilder(ChatColor.DARK_AQUA+"ID: "
				+ChatColor.AQUA+this.getNameID()+"\n");
		comp.append(ChatColor.DARK_AQUA+"DisplayName: "
				+ChatColor.AQUA+this.getDisplayName()+"\n");
		comp.append(ChatColor.DARK_AQUA+"CoolDown: ");
		
		if (!this.isRepetable())
			comp.append(ChatColor.RED+"Disabled\n");
		else
			comp.append(ChatColor.YELLOW+""+this.getCooldownTime()+" minutes\n");
		if (tasks.size() > 0) {
			comp.append(ChatColor.DARK_AQUA+"Tasks:\n");
			for (Task task : tasks.values()) {
				comp.append(ChatColor.AQUA+" - "+task.getNameID()+"\n")
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/qa quest "+this.parent.getNameID()+" mission "+this.getNameID()
							+" task "+task.getNameID()+ " info"))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(ChatColor.YELLOW+"Click for details")
							.create()));
			}
		}
		if (requires.size() > 0) {
			comp.append(ChatColor.DARK_AQUA+"Requires:\n");
			for (MissionRequire require : requires) {
				comp.append(ChatColor.AQUA+" - "+require.toText()+"\n");
			}
		}
		
		return comp.create();
	}
}
