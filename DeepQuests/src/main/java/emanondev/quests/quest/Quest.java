package emanondev.quests.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.require.QuestRequire;
import emanondev.quests.utils.MemoryUtils;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import emanondev.quests.utils.YmlLoadableWithCooldown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Quest extends YmlLoadableWithCooldown{
	public static final String HOLDER_MISSION_NUMBER = "{mission-number}";
	public static final String HOLDER_QUEST_NAME = "{quest-name}";
	public static final String HOLDER_COMPLETED_MISSION_NUMBER = "{completed-mission-number}";
	public static final String PATH_MISSIONS = "missions";
	public static final String PATH_REQUIRES = "requires";
	
	private final LinkedHashMap<String,Mission> missions = new LinkedHashMap<String,Mission>();
	
	@Override
	public void setDirty(boolean value) {
		super.setDirty(value);
		if (this.isDirty()==false) {
			for (Mission mission : missions.values())
				mission.setDirty(false);
			this.displayInfo.setDirty(false);
		}
		else {
			getParent().setDirty(true);
		}
	}
	private final QuestManager parent;
	public QuestManager getParent(){
		return parent;
	}
	private final List<QuestRequire> requires = new ArrayList<QuestRequire>();
	public List<QuestRequire> getRequires(){
		return Collections.unmodifiableList(requires);
	}
	public Mission getMissionByNameID(String key) {
		return missions.get(key);
	}
	public Collection<Mission> getMissions(){
		return Collections.unmodifiableCollection(missions.values());
	}
	public Quest(MemorySection m,QuestManager parent) {
		super(m);
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		LinkedHashMap<String,Mission> map = this.loadMissions((MemorySection) m.get(PATH_MISSIONS));
		if (map!=null)
			missions.putAll(map);
		for (Mission mission : missions.values()) {
			if (isDirty())
				break;
			if (mission.isDirty())
				setDirty(true);
		}
			
		List<QuestRequire> req= loadRequires(m);
		if (req!=null)
			requires.addAll(req);		
		displayInfo = loadDisplayInfo(m);
		if (displayInfo.isDirty())
			this.setDirty(true);
		
		this.addToEditor(new SubExplorerFactory<Mission>(getMissions()));
	}
	
	private final QuestDisplayInfo displayInfo;
	@Override
	public QuestDisplayInfo getDisplayInfo() {
		return displayInfo;
	}

	public BaseComponent[] toComponent() {
		ComponentBuilder comp = new ComponentBuilder(""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Quest Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		comp.append("\n"+ChatColor.DARK_AQUA+"ID: "
				+ChatColor.AQUA+this.getNameID());
		comp.append("\n"+ChatColor.DARK_AQUA+"DisplayName: "
				+ChatColor.AQUA+this.getDisplayName());
		
		if (!this.isRepetable())
			comp.append("\n"+ChatColor.DARK_AQUA+"Repeatable: "+ChatColor.RED+"Disabled");
		else
			comp.append("\n"+ChatColor.DARK_AQUA+"Repeatable: "+ChatColor.GREEN+"Enabled");
		comp.append("\n"+ChatColor.DARK_AQUA+"Cooldown: "+ChatColor.YELLOW+(this.getCooldownTime()/60/1000)+" minutes");
		if (missions.size() > 0) {
			comp.append("\n"+ChatColor.DARK_AQUA+"Missions:");
			for (Mission mission : missions.values()) {
				comp.append("\n"+ChatColor.AQUA+" - "+mission.getNameID())
					.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
							"/qa quest "+this.getNameID()+" mission "
							+mission.getNameID()+ " info"))
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
						"/qa quest "+this.getNameID()+" worlds remove "+world))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(ChatColor.YELLOW+"Click to remove")
						.create()));
		}
		if (requires.size() > 0) {
			comp.append("\n"+ChatColor.DARK_AQUA+"Requires:");
			for (QuestRequire require : requires) {
				comp.append("\n"+ChatColor.AQUA+" - "+require.toText());
				
			}
		}
		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Quest Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		return comp.create();
	}
	private QuestDisplayInfo loadDisplayInfo(MemorySection m) {
		return new QuestDisplayInfo(m,this);
	}
	
	private List<QuestRequire> loadRequires(MemorySection m) {
		List<String> l = MemoryUtils.getStringList(m, PATH_REQUIRES);
		return Quests.getInstance().getRequireManager().convertQuestRequires(l);
	}
	private LinkedHashMap<String, Mission> loadMissions(MemorySection m) {
		if (m == null)
			return new LinkedHashMap<String, Mission>();
		Set<String> s = m.getKeys(false);
		LinkedHashMap<String,Mission> map = new LinkedHashMap<String,Mission>();
		s.forEach((key)->{
			try {
				Mission mission = new Mission((MemorySection) m.get(key),this);
				map.put(mission.getNameID(), mission);
			} catch (Exception e) {
				e.printStackTrace();
				Quests.getInstance().getLoggerManager().getLogger("errors")
				.log("Error while loading Mission on file quests.yml '"
						+m.getCurrentPath()+"."+key+"' could not be read as valid mission"
						,ExceptionUtils.getStackTrace(e));
			}
		});
		return map;
	}
	
	@Override
	protected boolean getDefaultCooldownUse() {
		return Defaults.QuestDef.getDefaultCooldownUse();
	}
	@Override
	protected boolean shouldCooldownAutogen() {
		return Defaults.QuestDef.shouldCooldownAutogen();
	}
	@Override
	protected long getDefaultCooldownMinutes() {
		return Defaults.QuestDef.getDefaultCooldownMinutes();
	}
	@Override
	protected List<String> getWorldsListDefault() {
		return Defaults.QuestDef.getWorldsListDefault();
	}
	@Override
	protected boolean shouldWorldsAutogen() {
		return  Defaults.QuestDef.shouldWorldsAutogen();
	}
	@Override
	protected boolean getUseWorldsAsBlackListDefault() {
		return  Defaults.QuestDef.getUseWorldsAsBlackListDefault();
	}
	@Override
	protected boolean shouldAutogenDisplayName() {
		return Defaults.QuestDef.shouldAutogenDisplayName();
	}
	
	public boolean addMission(String id,String displayName) {
		if (id == null || id.isEmpty() || 
				id.contains(" ")||id.contains(".")||id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_"," ");
		id = id.toLowerCase();
		if (missions.containsKey(id))
			return false;
		getSection().set(PATH_MISSIONS+"."+id+"."+YmlLoadable.PATH_DISPLAY_NAME,displayName);
		Mission m = new Mission((MemorySection) getSection().get(PATH_MISSIONS+"."+id),this);
		missions.put(m.getNameID(), m);
		parent.save();
		parent.reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public boolean deleteMission(Mission mission) {
		if (mission == null || !missions.containsKey(mission.getNameID()) )
			return false;
		getSection().set(PATH_MISSIONS+"."+mission.getNameID(),null);
		missions.remove(mission.getNameID());
		parent.save();
		parent.reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public String getGuiTitle() {
		return StringUtils.fixColorsAndHolders(
				"&8"+StringUtils.withoutColor(getDisplayName()));
	}
}
