package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.NoLoopQC;

public class TargetMissionData extends QCData {
	private final static String PATH_TARGET_MISSION_ID = "target-mission";
	private final static String PATH_TARGET_QUEST_ID = "target-quest";

	private String targetMissionID;
	private String targetQuestID;

	public TargetMissionData(ConfigSection section, NoLoopQC parent) {
		super(section, parent);
		this.targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID, null);
		this.targetQuestID = getSection().getString(PATH_TARGET_QUEST_ID, null);
	}

	public NoLoopQC getParent() {
		return (NoLoopQC) super.getParent();
	}

	public boolean setTargetMission(Mission mission) {
		if (!getParent().isLoopSafe(mission))
			return false;

		if (mission == null) {
			targetMissionID = null;
			targetQuestID = null;
		} else {
			targetMissionID = mission.getID();
			targetQuestID = mission.getParent().getID();
		}

		getSection().set(PATH_TARGET_MISSION_ID, targetMissionID);
		getSection().set(PATH_TARGET_QUEST_ID, targetQuestID);
		getParent().setDirty(true);

		return true;
	}

	public Mission getTargetMission() {
		if (targetQuestID == null || targetMissionID == null)
			return null;
		Quest q = getQuestManager().getQuestByID(targetQuestID);
		if (q == null)
			return null;
		return q.getMissionByID(targetMissionID);
	}

	public Button getMissionSelectorButton(Gui gui) {
		return new QuestSelectorButton(gui);
	}

	public class QuestSelectorButton extends SelectOneElementButton<Quest> {

		public QuestSelectorButton(Gui parent) {
			super("&9Quest Selector", new ItemBuilder(Material.BOOK).setGuiProperty().build(), parent,
					getQuestManager().getQuests(), true, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lQuest Selector");
			desc.add("&7Click to Change");
			desc.add("");
			Mission m = getTargetMission();
			if (m == null)
				desc.add("&7No mission selected");
			else
				desc.add("&6Current: &e" + m.getDisplayName() + "&6 of quest &e" + m.getParent().getDisplayName());
			return desc;
		}

		@Override
		public List<String> getElementDescription(Quest quest) {
			List<String> desc = new ArrayList<String>();
			desc.add("&6Quest: '&e" + quest.getDisplayName() + "&6'");
			desc.add("&6Click to Select");
			desc.add("");
			desc.add("&7Contains &e" + quest.getMissions().size() + " &7missions");
			for (Mission mission : quest.getMissions()) {
				desc.add("&7 - &e" + mission.getDisplayName());
			}
			if (quest.getWorldsList().size() == 0)
				desc.add("&7Enabled in any World");
			else {
				if (quest.isWorldListBlacklist()) {
					desc.add("&7Disabled on Worlds:");
					for (String world : quest.getWorldsList())
						desc.add("&7 - &c" + world);
				} else {
					desc.add("&7Enbled on Worlds:");
					for (String world : quest.getWorldsList())
						desc.add("&7 - &a" + world);
				}
			}
			return desc;
		}

		@Override
		public ItemStack getElementItem(Quest element) {
			return new ItemBuilder(Material.BOOK).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(Quest element) {
			new MissionSelectorButton(this.getParent(),element).onClick(getTargetPlayer(),ClickType.LEFT);
		}

	}

	private Collection<Mission> getAllowedMissions(Quest quest) {
		Collection<Mission> result = new HashSet<Mission>();
		for (Mission mission : quest.getMissions())
			if (TargetMissionData.this.getParent().isLoopSafe(mission))
				result.add(mission);
		return result;
	}

	public class MissionSelectorButton extends SelectOneElementButton<Mission> {

		public MissionSelectorButton(Gui parent, Quest quest) {
			super("&9Mission Selector (Quest:" + quest.getDisplayName() + ")",
					new ItemBuilder(Material.PAPER).setGuiProperty().build(), parent, getAllowedMissions(quest), true,
					true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			return null;
		}

		@Override
		public List<String> getElementDescription(Mission mission) {
			List<String> desc = new ArrayList<String>();
			desc.add("&6Mission: '&e" + mission.getDisplayName() + "&6'");
			desc.add("&6Click to Select");
			desc.add("");
			desc.add("&7Contains &e" + mission.getTasks().size() + " &7tasks");
			for (Task task : mission.getTasks()) {
				desc.add("&7 - &e" + task.getDisplayName());
			}
			if (mission.getWorldsList().size() == 0)
				desc.add("&7Enabled in any World");
			else {
				if (mission.isWorldListBlacklist()) {
					desc.add("&7Disabled on Worlds:");
					for (String world : mission.getWorldsList())
						desc.add("&7 - &c" + world);
				} else {
					desc.add("&7Enbled on Worlds:");
					for (String world : mission.getWorldsList())
						desc.add("&7 - &a" + world);
				}
			}
			return desc;
		}

		@Override
		public ItemStack getElementItem(Mission mission) {
			return new ItemBuilder(Material.PAPER).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(Mission mission) {
			if (setTargetMission(mission)) {
				getParent().updateInventory();
				getTargetPlayer().openInventory(getParent().getInventory());
			}
		}

	}

}
