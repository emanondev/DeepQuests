package emanondev.quests;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.StringUtils.CooldownFormat;

public class Language {
	
	private static final YMLConfig config = new YMLConfig(Quests.get(), "language");
	private static final String PATH_TIME = "time.";
	private static final String PATH_SINGLE_TIME = ".single";
	private static final String PATH_MULTIPLE_TIME = ".multi";
	private static final String PATH_CONJUNCTIONS = "conjunctions.";
	private static final String PATH_TASK_ACTION = "action.";

	/**
	 * reload configuration file (language.yml)
	 */
	public static void reload() {
		config.reload();
		Gui.refresh();
	}

	public static class Time {
		public static String getSingleTime(CooldownFormat time) {
			return config.getString(PATH_TIME + time.toString() + PATH_SINGLE_TIME, time.toString().toLowerCase());
		}

		public static String getMultiTime(CooldownFormat time) {
			return config.getString(PATH_TIME + time.toString() + PATH_MULTIPLE_TIME, time.toString().toLowerCase());
		}
	}

	public static class Conjunctions {
		public static String getConjOr() {
			return config.getString(PATH_CONJUNCTIONS + "OR", "or");
		}

		public static String getConjAnd() {
			return config.getString(PATH_CONJUNCTIONS + "AND", "and");
		}

		public static String getConjNot() {
			return config.getString(PATH_CONJUNCTIONS + "NOT", "not");
		}
	}

	public static class Gui {
		private static ItemStack getItem(Player p, ItemStack item, ArrayList<String> desc, String... holders) {
			ItemStack result = new ItemStack(item);
			StringUtils.setDescription(result, p, desc, holders);
			return result;
		}

		private static void refresh() {
			questsMenuTitle = StringUtils
					.fixColorsAndHolders(config.getString("gui.quests-menu.title", "&9Quests Menu"));
			missionsMenuTitle = StringUtils
					.fixColorsAndHolders(config.getString("gui.missions-menu.title", "&9Quests Menu"));

			missionsMenuCloseItem = config.getItemStack("gui.missions-menu.close.item",
					new ItemStack(Material.IRON_DOOR));
			missionsMenuCloseDesc = config.getStringList("gui.missions-menu.close.desc",
					new ArrayList<String>(Arrays.asList("&c&lClose Gui")));
		}

		private static String questsMenuTitle;

		private static String missionsMenuTitle;

		private static ItemStack missionsMenuCloseItem;
		private static ArrayList<String> missionsMenuCloseDesc;

		public static String getQuestsMenuTitle(Player p, int page) {
			return StringUtils.convertText(p, questsMenuTitle, H.PAGE_HOLDER, String.valueOf(page));
		}

		public static String getMissionsMenuTitle(Player p, Quest q, int page) {
			return StringUtils.convertText(p, missionsMenuTitle, H.PAGE_HOLDER, String.valueOf(page), H.QUEST_NAME,
					q.getDisplayName());
		}

		public static ItemStack getMissionsMenuCloseItem(Player p, int page) {
			return getItem(p, missionsMenuCloseItem, missionsMenuCloseDesc, H.PAGE_HOLDER, String.valueOf(page));
		}

	}

	public static String getTaskActionName(String taskName) {
		return config.getString(PATH_TASK_ACTION + taskName, taskName.toLowerCase());
	}
}
