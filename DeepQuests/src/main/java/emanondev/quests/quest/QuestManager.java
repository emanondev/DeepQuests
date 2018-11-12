package emanondev.quests.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.button.SelectQuestElementButton;
import emanondev.quests.newgui.gui.AdminPlayerManagerGui;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.utils.AQuestComponent;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;


public class QuestManager implements Savable {
	private final static String PATH_QUESTS = "quests";
	private final static String PATH_QUEST_COUNTER = "quest-counter";
	private final static String PATH_MISSION_COUNTER = "mission-counter";
	private final static String PATH_TASK_COUNTER = "task-counter";/*
	private final static String PATH_REWARD_COUNTER = "reward-counter";
	private final static String PATH_REQUIRE_COUNTER = "require-counter";
	private int questCounter = 0;
	private int missionCounter = 0;
	private int taskCounter = 0;
	private int rewardCounter = 0;
	private int requireCounter = 0;*/

	private final YMLConfig data;
	

	public String getNewTaskID(Mission m) {
		long i = data.getLong(PATH_TASK_COUNTER, 0);
		String key = null;
		boolean found = false;
		do {
			if (i < 10)
				key = "t000" + i;
			else if (i < 100)
				key = "t00" + i;
			else if (i < 1000)
				key = "t0" + i;
			else
				key = "t" + i;
			if (m.getTaskByID(key) == null)
				found = true;
			i++;
		} while (i < 10000 && found == false);
		if (found == false) {
			do {
				key = "t" + i;
				if (m.getTaskByID(key) == null)
					found = true;
				i++;
			} while (found == false);
		}
		data.set(PATH_TASK_COUNTER, i);
		return key;
	}

	public String getNewMissionID(Quest q) {
		long i = data.getLong(PATH_MISSION_COUNTER, 0);
		String key;
		boolean found = false;
		do {
			if (i < 10)
				key = "m000" + i;
			else if (i < 100)
				key = "m00" + i;
			else if (i < 1000)
				key = "m0" + i;
			else
				key = "m" + i;
			if (q.getMissionByID(key) == null)
				found = true;
			i++;
		} while (i < 10000 && found == false);
		if (found == false) {
			do {
				key = "m" + i;
				if (q.getMissionByID(key) == null)
					found = true;
				i++;
			} while (found == false);
		}
		data.set(PATH_MISSION_COUNTER, i);
		return key;
	}

	public String getNewQuestID() {
		long i = data.getLong(PATH_QUEST_COUNTER, 0);
		String key;
		boolean found = false;
		do {
			if (i < 10)
				key = "q000" + i;
			else if (i < 100)
				key = "q00" + i;
			else if (i < 1000)
				key = "q0" + i;
			else
				key = "q" + i;
			if (getQuestByID(key) == null)
				found = true;
			i++;
		} while (i < 10000 && found == false);
		if (found == false) {
			do {
				key = "q" + i;
				if (getQuestByID(key) == null)
					found = true;
				i++;
			} while (found == false);
		}
		data.set(PATH_QUEST_COUNTER, i);
		return key;
	}

	private static final HashMap<String, Quest> quests = new HashMap<String, Quest>();

	public QuestManager(JavaPlugin plugin, String filename) {
		data = new YMLConfig(plugin, filename);
	}

	public void save() {
		data.save();
	}

	public void reload() {
		quests.clear();
		data.reload();

		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder()!=null)
				if (inv.getHolder() instanceof Gui)
					p.closeInventory();
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder()!=null)
				if (inv.getHolder() instanceof Gui)
					p.closeInventory();
		}
		ConfigSection section = data.loadSection(PATH_QUESTS);
		boolean dirty = false;
		Set<String> s = section.getValues(false).keySet();
		for (String key: s) {
			try {
				Quest quest = new Quest(section.loadSection(key), this);
				quests.put(quest.getID(), quest);
				if (quest.isLoadDirty())
					dirty = true;
			} catch (Exception e) {
				e.printStackTrace();
				Quests.get().getLoggerManager().getLogger("errors").log(
						"Error while loading Quests on file quests.yml '" + key + "' could not be read as valid quest",
						ExceptionUtils.getStackTrace(e));
			}
		}

		if (dirty)
			setDirty(true);
	}

	public Quest getQuestByID(String key) {
		return quests.get(key);
	}

	public Collection<Quest> getQuests() {
		return Collections.unmodifiableCollection(quests.values());
	}

	@Override
	public boolean isDirty() {
		return data.isDirty();
	}

	@Override
	public void setDirty(boolean value) {
		data.setDirty(value);
	}

	public String addQuest(String displayName) {
		return this.addQuest(null, displayName);
	}

	public String addQuest(String id, String displayName) {
		if (id == null)
			id = getNewQuestID();
		if (id.isEmpty() || id.contains(" ") || id.contains(".") || id.contains(":"))
			return null;
		if (displayName == null)
			displayName = id.replace("_", " ");
		id = id.toLowerCase();
		if (quests.containsKey(id))
			return null;
		data.set(PATH_QUESTS + "." + id + "." + AQuestComponent.PATH_DISPLAY_NAME, displayName);
		Quest q = new Quest(data.loadSection(PATH_QUESTS + "." + id), this);
		quests.put(q.getID(), q);
		save();
		reload();
		Quests.get().getPlayerManager().reload();
		return id;
	}

	public boolean deleteQuest(Quest quest) {
		if (quest == null || !quests.containsKey(quest.getID()))
			return false;
		data.set(PATH_QUESTS + "." + quest.getID(), null);
		quests.remove(quest.getID());
		save();
		reload();
		Quests.get().getPlayerManager().reload();
		return true;
	}

	private final static BaseComponent[] setDisplayNameDescription = new ComponentBuilder(
			ChatColor.GOLD + "Click suggest the command\n\n" + ChatColor.GOLD + "Set the display name for the quest\n"
					+ ChatColor.YELLOW + "/questtext <display name>").create();

	public Gui getQuestsEditor(Player player, Gui parent) {
		return new QuestsEditor(player, parent);
	}

	private class QuestsEditor extends MapGui {

		public QuestsEditor(Player p, Gui previusHolder) {
			super("&8Quests Manager Editor", 6, p, previusHolder);
			this.putButton(53, new BackButton(this));
			this.putButton(2, new DeleteQuestButton());
			this.putButton(0, new QuestExplorerButton());
			this.putButton(1, new AddQuestButton());
			this.putButton(8, new PlayerButton());
		}

		private class DeleteQuestButton extends SelectOneElementButton<Quest> {

			public DeleteQuestButton() {
				super("&cSelect Quest to delete", new ItemStack(Material.NETHERRACK), QuestsEditor.this,
						QuestManager.this.getQuests(), false, true, true);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDelete Quest", "&6Click to select a Quest");
			}

			@Override
			public List<String> getElementDescription(Quest quest) {
				return Arrays.asList("&6Quest: '&e" + quest.getDisplayName() + "&6'");
			}

			@Override
			public ItemStack getElementItem(Quest quest) {
				return new ItemStack(Material.PAPER);
			}

			@Override
			public void onElementSelectRequest(Quest quest) {
				if (QuestManager.this.deleteQuest(quest)) {
					QuestsEditor.this.putButton(0, new QuestExplorerButton());
					QuestsEditor.this.putButton(2, new DeleteQuestButton());
					getTargetPlayer().openInventory(QuestsEditor.this.getInventory());
				}
			}
			
			@Override
			public void onClick(Player clicker, ClickType click) {
				if (!Utils.checkPermission(clicker,Perms.ADMIN_EDITOR_DELETE_QUEST))
					return;
				super.onClick(clicker, click);
			}
			
		}

		private class QuestExplorerButton extends SelectQuestElementButton<Quest> {

			public QuestExplorerButton() {
				super("&cSelect Quest to open", new ItemStack(Material.PAINTING), QuestsEditor.this,
						QuestManager.this.getQuests(), false, true, false);
			}

			@Override
			public ItemStack getItem() {
				ItemStack result = super.getItem();
				if (result!=null)
					result.setAmount(Math.min(Math.max(1,this.possibleValues.size()),127));
				return result;
			}
			
			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lOpen Quest", "&6Click to select a Quest");
			}

			@Override
			public List<String> getElementDescription(Quest quest) {
				return quest.getInfo();
			}

			@Override
			public ItemStack getElementItem(Quest element) {
				return new ItemStack(Material.PAPER);
			}

			@Override
			public void onElementSelectRequest(Quest quest) {
				this.getTargetPlayer()
						.openInventory(quest.createEditorGui(getTargetPlayer(), QuestsEditor.this).getInventory());
			}
		}

		private class AddQuestButton extends emanondev.quests.newgui.button.TextEditorButton {
			public AddQuestButton() {
				super(new ItemBuilder(Material.GLOWSTONE).setGuiProperty().build(), QuestsEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				List<String> desc = new ArrayList<String>();
				desc.add("&6&lCreate a new Quest");
				desc.add("&6Click to create a new Quest");
				return desc;
			}

			@Override
			public void onReicevedText(String text) {
				if (text == null) {
					getTargetPlayer().openInventory(QuestsEditor.this.getInventory());
					return;
				}
				String questId = QuestManager.this.addQuest(text);
				if (questId != null) {
					Quest quest = QuestManager.this.getQuestByID(questId);
					QuestsEditor.this.putButton(0, new QuestExplorerButton());
					QuestsEditor.this.putButton(2, new DeleteQuestButton());
					
					getTargetPlayer().openInventory(quest
							.createEditorGui(getTargetPlayer(),
									quest.createEditorGui(getTargetPlayer(), QuestsEditor.this))
							.getInventory());
				}
			}

			
			@Override
			public void onClick(Player clicker, ClickType click) {
				if (!Utils.checkPermission(clicker,Perms.ADMIN_EDITOR_ADD_QUEST))
					return;
				this.requestText(clicker, null, setDisplayNameDescription);
			}
		}
		private class PlayerButton extends SelectOneElementButton<Player> {

			public PlayerButton() {
				super("&8Select a Player", new ItemBuilder(Material.SKULL_ITEM).setGuiProperty().setDamage(3).build(),
						QuestsEditor.this, new HashSet<Player>( Bukkit.getOnlinePlayers()), false, true, false);
				
			}
			
			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lPlayer Manager","&6Click to select a player","&6and open his stats gui");
			}

			@Override
			public List<String> getElementDescription(Player element) {
				return Arrays.asList("&b"+element.getName(),"&7"+element.getUniqueId().toString());
			}

			@Override
			public ItemStack getElementItem(Player element) {
				return new ItemBuilder(Material.SKULL_ITEM).setGuiProperty().setDamage(3).build();
			}

			@Override
			public void onElementSelectRequest(Player element) {
				getTargetPlayer().openInventory(new AdminPlayerManagerGui(getTargetPlayer(),getParent(),element).getInventory());
			}
		}
	}
}
