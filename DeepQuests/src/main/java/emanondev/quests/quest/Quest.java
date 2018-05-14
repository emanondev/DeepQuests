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
import emanondev.quests.mission.Mission;
import emanondev.quests.require.QuestRequire;
import emanondev.quests.utils.MemoryUtils;
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
	private final List<QuestRequire> requires = new ArrayList<QuestRequire>();
	public Mission getMissionByNameID(String key) {
		return missions.get(key);
	}
	public Collection<Mission> getMissions(){
		return Collections.unmodifiableCollection(missions.values());
	}
	public Quest(MemorySection m) {
		super(m);
		LinkedHashMap<String,Mission> map = this.loadMissions((MemorySection) m.get(PATH_MISSIONS));
		if (map!=null)
			missions.putAll(map);
		List<QuestRequire> req= loadRequires(m);
		if (req!=null)
			requires.addAll(req);		
		displayInfo = loadDisplayInfo(m);
		if (displayInfo.isDirty())
			this.setDirty(true);
	}
	
	protected List<QuestRequire> loadRequires(MemorySection m) {
		List<String> l = MemoryUtils.getStringList(m, PATH_REQUIRES);
		return Quests.getInstance().getRequireManager().convertQuestRequires(l);
	}
	protected LinkedHashMap<String, Mission> loadMissions(MemorySection m) {
		if (m == null)
			return new LinkedHashMap<String, Mission>();
		Set<String> s = m.getKeys(false);
		LinkedHashMap<String,Mission> map = new LinkedHashMap<String,Mission>();
		s.forEach((key)->{
			try {
				Mission mission = new Mission((MemorySection) m.get(key),this);
				map.put(mission.getNameID(), mission);
				if( mission.isDirty() )
					this.setDirty(true);
			} catch (Exception e) {
				Quests.getInstance().getLoggerManager().getLogger("errors")
				.log("Error while loading Mission on file quests.yml '"
						+m.getCurrentPath()+"."+key+"' could not be read as valid mission"
						,ExceptionUtils.getStackTrace(e));
			}
		});
		return map;
	}
	@Override
	public QuestDisplayInfo getDisplayInfo() {
		return displayInfo;
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
	protected int getDefaultCooldownMinutes() {
		return Defaults.QuestDef.getDefaultCooldownMinutes();
	}

	private QuestDisplayInfo loadDisplayInfo(MemorySection m) {
		return new QuestDisplayInfo(m,this);
	}
	
	public List<QuestRequire> getRequires(){
		return Collections.unmodifiableList(requires);
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
	private final QuestDisplayInfo displayInfo;

	
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
		if (missions.size() > 0) {
			comp.append(ChatColor.DARK_AQUA+"Missions:\n");
			for (Mission mission : missions.values()) {
				comp.append(ChatColor.AQUA+" - "+mission.getNameID()+"\n")
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/qa quest "+this.getNameID()+" mission "
							+mission.getNameID()+ " info"))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(ChatColor.YELLOW+"Click for details")
							.create()));
				
			}
		}
		if (requires.size() > 0) {
			comp.append(ChatColor.DARK_AQUA+"Requires:\n");
			for (QuestRequire require : requires) {
				comp.append(ChatColor.AQUA+" - "+require.toText()+"\n");
				
			}
		}
		
		return comp.create();
	}
}
