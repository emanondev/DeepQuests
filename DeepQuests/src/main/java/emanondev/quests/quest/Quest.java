package emanondev.quests.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.AQuestComponent;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QCWithCooldown;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Quest extends QCWithCooldown {
	public static final String PATH_MISSIONS = "missions";
	public static final String PATH_REQUIRES = "requires";

	private final LinkedHashMap<String, Mission> missions = new LinkedHashMap<String, Mission>();
	private final LinkedHashMap<String, Require> requires = new LinkedHashMap<String, Require>();
	private final QuestDisplayInfo displayInfo;

	public Quest(ConfigSection section, QuestManager parent) {
		super(section, parent);
		LinkedHashMap<String, Mission> map = this.loadMissions((ConfigSection) section.get(PATH_MISSIONS));
		if (map != null)
			missions.putAll(map);
		for (Mission mission : missions.values()) {
			if (isDirty())
				break;
			if (mission.isDirty())
				setDirtyLoad();
		}

		LinkedHashMap<String, Require> req = loadRequires(section);
		if (req != null)
			requires.putAll(req);
		displayInfo = loadDisplayInfo(section);
		if (displayInfo.isDirty())
			setDirtyLoad();
	}

	public QuestManager getParent() {
		return (QuestManager) super.getParent();
	}

	public Collection<Require> getRequires() {
		return Collections.unmodifiableCollection(requires.values());
	}

	public Mission getMissionByID(String key) {
		return missions.get(key);
	}

	public Collection<Mission> getMissions() {
		return Collections.unmodifiableCollection(missions.values());
	}

	@Override
	public QuestDisplayInfo getDisplayInfo() {
		return displayInfo;
	}
	
	public List<String> getInfo(){
		List<String> info = new ArrayList<String>();
		info.add("&9&lQuest: &6"+ this.getDisplayName());
		info.add("&8ID: "+ this.getID());
		info.add("");
		info.add("&9Priority: &e"+getPriority());

		if (!this.getCooldownData().isRepetable())
			info.add("&9Repeatable: &cFalse");
		else
			info.add("&9Repeatable: &aTrue");
		info.add("&9Cooldown: &e" + StringUtils.getStringCooldown(this.getCooldownData().getCooldownTime()));
		if (missions.size() > 0) {
			info.add("&9Missions:");
			for (Mission mission : missions.values()) {
				info.add("&9 - &e"+mission.getDisplayName());
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
				info.add("   &8" + require.getType().getKey());
			}
		}
		return info;
	}

	private QuestDisplayInfo loadDisplayInfo(ConfigSection m) {
		return new QuestDisplayInfo(m, this);
	}

	private LinkedHashMap<String, Require> loadRequires(ConfigSection m) {
		ConfigSection m2 = (ConfigSection) m.get(PATH_REQUIRES);
		if (m2 == null)
			m2 = (ConfigSection) m.createSection(PATH_REQUIRES);
		return Quests.get().getRequireManager().loadRequires(this, m2);
	}

	private LinkedHashMap<String, Mission> loadMissions(ConfigSection m) {
		if (m == null)
			return new LinkedHashMap<String, Mission>();
		Set<String> s = m.getKeys(false);
		LinkedHashMap<String, Mission> map = new LinkedHashMap<String, Mission>();
		s.forEach((key) -> {
			try {
				Mission mission = new Mission(m.loadSection(key), this);
				map.put(mission.getID(), mission);
			} catch (Exception e) {
				e.printStackTrace();
				Quests.get().getLoggerManager().getLogger("errors")
						.log("Error while loading Mission on file quests.yml '" + m.getCurrentPath() + "." + key
								+ "' could not be read as valid mission", ExceptionUtils.getStackTrace(e));
			}
		});
		return map;
	}

	@Override
	protected boolean getDefaultCooldownUse() {
		return Defaults.QuestDef.getDefaultCooldownUse();
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
		return Defaults.QuestDef.shouldWorldsAutogen();
	}

	@Override
	protected boolean getUseWorldsAsBlacklistDefault() {
		return Defaults.QuestDef.getUseWorldsAsBlacklistDefault();
	}

	public String addMission(String displayName) {
		return addMission(Quest.this.getParent().getNewMissionID(Quest.this), displayName);
	}

	public String addMission(String id, String displayName) {
		if (id == null || id.isEmpty() || id.contains(" ") || id.contains(".") || id.contains(":"))
			return null;
		if (displayName == null)
			displayName = id.replace("_", " ");
		id = id.toLowerCase();
		if (missions.containsKey(id))
			return null;
		getSection().set(PATH_MISSIONS + "." + id + "." + AQuestComponent.PATH_DISPLAY_NAME, displayName);
		Mission m = new Mission(getSection().loadSection(PATH_MISSIONS + "." + id), this);
		missions.put(m.getID(), m);
		getParent().save();
		getParent().reload();
		Quests.get().getPlayerManager().reload();
		return id;
	}

	public boolean deleteMission(Mission mission) {
		if (mission == null || !missions.containsKey(mission.getID()))
			return false;
		getSection().set(PATH_MISSIONS + "." + mission.getID(), null);
		missions.remove(mission.getID());
		getParent().save();
		getParent().reload();
		Quests.get().getPlayerManager().reload();
		return true;
	}

	private final static String PATH_REQUIRE_TYPE = "type";

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

	@Override
	public Gui createEditorGui(Player p, Gui previusHolder) {
		return new QuestEditor(p, previusHolder);
	}

	protected class QuestEditor extends QCWithCooldownEditor {
		public QuestEditor(Player p, Gui previusHolder) {
			super("&8Quest: &9" + Quest.this.getDisplayName(), p, previusHolder);
			this.putButton(0, new MissionExplorerButton());
			this.putButton(1, new AddMissionButton());
			this.putButton(2, new DeleteMissionButton());
			this.putButton(18, new RequireExplorerButton());
			this.putButton(19, new AddRequireButton());
			this.putButton(20, new DeleteRequireButton());
		}

		private class DeleteMissionButton extends SelectOneElementButton<Mission> {

			public DeleteMissionButton() {
				super("&cSelect Mission to delete", new ItemStack(Material.NETHERRACK), QuestEditor.this,
						Quest.this.getMissions(), false, true, true);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDelete Mission", "&6Click to select a Mission");
			}

			@Override
			public List<String> getElementDescription(Mission mission) {
				return Arrays.asList("&6Mission: '&e" + mission.getDisplayName() + "&6'");
			}

			@Override
			public ItemStack getElementItem(Mission mission) {
				return new ItemStack(Material.PAPER);
			}

			@Override
			public void onElementSelectRequest(Mission mission) {
				if (Quest.this.deleteMission(mission)) {
					QuestEditor.this.putButton(0, new MissionExplorerButton());
					QuestEditor.this.putButton(2, new DeleteMissionButton());
					getTargetPlayer().openInventory(QuestEditor.this.getInventory());
				}
				
			}
		}

		private class MissionExplorerButton extends SelectOneElementButton<Mission> {

			public MissionExplorerButton() {
				super("&cSelect Mission to open", new ItemStack(Material.PAINTING), QuestEditor.this,
						Quest.this.getMissions(), false, true, false);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lOpen Mission", "&6Click to select a Mission");
			}

			@Override
			public List<String> getElementDescription(Mission mission) {
				return mission.getInfo();
			}

			@Override
			public ItemStack getElementItem(Mission element) {
				return new ItemStack(Material.PAPER);
			}

			@Override
			public void onElementSelectRequest(Mission task) {
				this.getTargetPlayer()
						.openInventory(task.createEditorGui(getTargetPlayer(), QuestEditor.this).getInventory());
			}
		}

		private class AddMissionButton extends emanondev.quests.newgui.button.TextEditorButton {
			public AddMissionButton() {
				super(new ItemBuilder(Material.GLOWSTONE).setGuiProperty().build(), QuestEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				List<String> desc = new ArrayList<String>();
				desc.add("&6&lCreate a new Mission");
				desc.add("&6Click to create a new Mission");
				return desc;
			}

			@Override
			public void onReicevedText(String text) {
				System.out.println("Text "+text+" player "+getTargetPlayer().getName());
				if (text == null) {
					getTargetPlayer().openInventory(QuestEditor.this.getInventory());
					return;
				}
				String missionId = Quest.this.addMission(text);
				if (missionId != null) {
					String questId = Quest.this.getID();
					Quest quest = getQuestManager().getQuestByID(questId);
					Mission mission = quest.getMissionByID(missionId);
					getTargetPlayer().openInventory(
							mission.createEditorGui(getTargetPlayer(),
							quest.createEditorGui(getTargetPlayer(),
							QuestEditor.this.getPreviusGui()))
							.getInventory());
				}

			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, null, setDisplayNameDescription);
			}
		}

		private class DeleteRequireButton extends SelectOneElementButton<Require> {

			public DeleteRequireButton() {
				super("&cSelect Require to delete", new ItemStack(Material.NETHERRACK), QuestEditor.this,
						Quest.this.getRequires(), false, true, true);
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
				return new ItemStack(Material.PAPER);
			}

			@Override
			public void onElementSelectRequest(Require require) {
				if (Quest.this.deleteRequire(require)) {
					QuestEditor.this.putButton(18, new RequireExplorerButton());
					QuestEditor.this.putButton(20, new DeleteRequireButton());
				}
			}
		}

		private class RequireExplorerButton extends SelectOneElementButton<Require> {

			public RequireExplorerButton() {
				super("&cSelect Require to open", new ItemStack(Material.PAINTING), QuestEditor.this,
						Quest.this.getRequires(), false, true, false);
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
				return new ItemStack(Material.PAPER);
			}

			@Override
			public void onElementSelectRequest(Require require) {
				this.getTargetPlayer()
						.openInventory(require.createEditorGui(getTargetPlayer(), QuestEditor.this).getInventory());
			}
		}

		private class AddRequireButton extends SelectOneElementButton<RequireType> {
			public AddRequireButton() {
				super("&cSelect RequireType to create", new ItemStack(Material.GLOWSTONE), QuestEditor.this,
						Quests.get().getRequireManager().getQuestRequiresTypes(), false, true, false);
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
				return new ItemStack(requireType.getGuiItemMaterial());
			}

			@Override
			public void onElementSelectRequest(RequireType requireType) {
				Require require = Quest.this.addRequire(requireType);
				if (require != null)
					this.getTargetPlayer()
							.openInventory(require.createEditorGui(getTargetPlayer(), QuestEditor.this).getInventory());
			}
		}

	}

	private final static BaseComponent[] setDisplayNameDescription = new ComponentBuilder(
			ChatColor.GOLD + "Click suggest the command\n\n" + ChatColor.GOLD + "Set the display name for the mission\n"
					+ ChatColor.YELLOW + "/questtext <display name>").create();

	@Override
	public QuestManager getQuestManager() {
		return getParent();
	}

}
