package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectQuestElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.NoLoopQC;

public class TargetQuestData extends QCData {
	private final static String PATH_TARGET_QUEST_ID = "target-quest";

	private String targetQuestID;

	public TargetQuestData(ConfigSection section, NoLoopQC parent) {
		super(section, parent);
		this.targetQuestID = getSection().getString(PATH_TARGET_QUEST_ID, null);
	}

	public NoLoopQC getParent() {
		return (NoLoopQC) super.getParent();
	}

	public boolean setTargetQuest(Quest quest) {
		if (!getParent().isLoopSafe(quest))
			return false;

		if (quest == null) {
			targetQuestID = null;
		} else {
			targetQuestID = quest.getID();
		}
		getSection().set(PATH_TARGET_QUEST_ID, targetQuestID);
		setDirty(true);

		return true;
	}

	public Quest getTargetQuest() {
		if (targetQuestID == null)
			return null;
		return getQuestManager().getQuestByID(targetQuestID);
	}

	public Button getQuestSelectorButton(Gui gui) {
		return new QuestSelectorButton(gui);
	}

	private Collection<Quest> getAllowedQuests() {
		Collection<Quest> result = new HashSet<Quest>();
		for (Quest quest : getQuestManager().getQuests())
			if (TargetQuestData.this.getParent().isLoopSafe(quest))
				result.add(quest);
		return result;
	}

	public class QuestSelectorButton extends SelectQuestElementButton<Quest> {

		public QuestSelectorButton(Gui parent) {
			super("&9Quest Selector", new ItemBuilder(Material.BOOK).setGuiProperty().build(), parent,
					getAllowedQuests(), true, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lQuest Selector");
			desc.add("&7Click to Change");
			desc.add("");
			Quest q = getTargetQuest();
			if (q == null)
				desc.add("&7No quest selected");
			else
				desc.add("&6Current: &e" + q.getDisplayName());
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
			if (setTargetQuest(element)) {
				getParent().updateInventory();
				getTargetPlayer().openInventory(getParent().getInventory());
			}
		}

	}

}
