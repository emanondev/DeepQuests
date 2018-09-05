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
	
	private static final YMLConfig config = new YMLConfig(Quests.getInstance(), "language");
	// private static final String PATH_BLOCKS = "blocks.";
	// private static final String PATH_ENTITY = "entity.";
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
			questsMenuPageItem = config.getItemStack("gui.quests-menu.page.item", new ItemStack(Material.NAME_TAG));
			questsMenuPageDesc = config.getStringList("gui.quests-menu.page.desc",
					new ArrayList<String>(Arrays.asList("&9&lPage <&5&l{page}&9&l>")));

			questsMenuPreviusPageItem = config.getItemStack("gui.quests-menu.previus-page.item",
					new ItemStack(Material.TRIPWIRE_HOOK));
			questsMenuPreviusPageDesc = config.getStringList("gui.quests-menu.previus-page.desc",
					new ArrayList<String>(Arrays.asList("&9&l<<<<<<<")));

			questsMenuNextPageItem = config.getItemStack("gui.quests-menu.next-page.item",
					new ItemStack(Material.TRIPWIRE_HOOK));
			questsMenuNextPageDesc = config.getStringList("gui.quests-menu.next-page.desc",
					new ArrayList<String>(Arrays.asList("&9&l>>>>>>>")));

			questsMenuBackItem = config.getItemStack("gui.quests-menu.back.item", new ItemStack(Material.WOOD_DOOR));
			questsMenuBackDesc = config.getStringList("gui.quests-menu.back.desc",
					new ArrayList<String>(Arrays.asList("&c&lGo Back")));

			questsMenuCloseItem = config.getItemStack("gui.quests-menu.close.item", new ItemStack(Material.IRON_DOOR));
			questsMenuCloseDesc = config.getStringList("gui.quests-menu.close.desc",
					new ArrayList<String>(Arrays.asList("&c&lClose Gui")));

			missionsMenuTitle = StringUtils
					.fixColorsAndHolders(config.getString("gui.missions-menu.title", "&9Quests Menu"));
			missionsMenuPageItem = config.getItemStack("gui.missions-menu.page.item", new ItemStack(Material.NAME_TAG));
			missionsMenuPageDesc = config.getStringList("gui.missions-menu.page.desc",
					new ArrayList<String>(Arrays.asList("&9&lPage <&5&l{page}&9&l>")));

			missionsMenuPreviusPageItem = config.getItemStack("gui.missions-menu.previus-page.item",
					new ItemStack(Material.TRIPWIRE_HOOK));
			missionsMenuPreviusPageDesc = config.getStringList("gui.missions-menu.previus-page.desc",
					new ArrayList<String>(Arrays.asList("&9&l<<<<<<<")));

			missionsMenuNextPageItem = config.getItemStack("gui.missions-menu.next-page.item",
					new ItemStack(Material.TRIPWIRE_HOOK));
			missionsMenuNextPageDesc = config.getStringList("gui.missions-menu.next-page.desc",
					new ArrayList<String>(Arrays.asList("&9&l>>>>>>>")));

			missionsMenuBackItem = config.getItemStack("gui.missions-menu.back.item",
					new ItemStack(Material.WOOD_DOOR));
			missionsMenuBackDesc = config.getStringList("gui.missions-menu.back.desc",
					new ArrayList<String>(Arrays.asList("&c&lGo Back")));

			missionsMenuCloseItem = config.getItemStack("gui.missions-menu.close.item",
					new ItemStack(Material.IRON_DOOR));
			missionsMenuCloseDesc = config.getStringList("gui.missions-menu.close.desc",
					new ArrayList<String>(Arrays.asList("&c&lClose Gui")));
		}

		private static String questsMenuTitle;

		private static ItemStack questsMenuPageItem;
		private static ArrayList<String> questsMenuPageDesc;

		private static ItemStack questsMenuPreviusPageItem;
		private static ArrayList<String> questsMenuPreviusPageDesc;

		private static ItemStack questsMenuNextPageItem;
		private static ArrayList<String> questsMenuNextPageDesc;

		private static ItemStack questsMenuBackItem;
		private static ArrayList<String> questsMenuBackDesc;

		private static ItemStack questsMenuCloseItem;
		private static ArrayList<String> questsMenuCloseDesc;

		private static String missionsMenuTitle;

		private static ItemStack missionsMenuPageItem;
		private static ArrayList<String> missionsMenuPageDesc;

		private static ItemStack missionsMenuPreviusPageItem;
		private static ArrayList<String> missionsMenuPreviusPageDesc;

		private static ItemStack missionsMenuNextPageItem;
		private static ArrayList<String> missionsMenuNextPageDesc;

		private static ItemStack missionsMenuBackItem;
		private static ArrayList<String> missionsMenuBackDesc;

		private static ItemStack missionsMenuCloseItem;
		private static ArrayList<String> missionsMenuCloseDesc;

		public static String getQuestsMenuTitle(Player p, int page) {
			return StringUtils.convertText(p, questsMenuTitle, H.PAGE_HOLDER, String.valueOf(page));
		}

		public static ItemStack getQuestsMenuPageItem(Player p, int page) {
			return getItem(p, questsMenuPageItem, questsMenuPageDesc, H.PAGE_HOLDER, String.valueOf(page));
		}

		public static ItemStack getQuestsMenuPreviusPageItem(Player p, int page) {
			return getItem(p, questsMenuPreviusPageItem, questsMenuPreviusPageDesc, H.PAGE_HOLDER,
					String.valueOf(page));
		}

		public static ItemStack getQuestsMenuNextPageItem(Player p, int page) {
			return getItem(p, questsMenuNextPageItem, questsMenuNextPageDesc, H.PAGE_HOLDER, String.valueOf(page));
		}

		public static ItemStack getQuestsMenuBackItem(Player p, int page) {
			return getItem(p, questsMenuBackItem, questsMenuBackDesc, H.PAGE_HOLDER, String.valueOf(page));
		}

		public static ItemStack getQuestsMenuCloseItem(Player p, int page) {
			return getItem(p, questsMenuCloseItem, questsMenuCloseDesc, H.PAGE_HOLDER, String.valueOf(page));
		}

		public static String getMissionsMenuTitle(Player p, Quest q, int page) {
			return StringUtils.convertText(p, missionsMenuTitle, H.PAGE_HOLDER, String.valueOf(page), H.QUEST_NAME,
					q.getDisplayName());
		}

		public static ItemStack getMissionsMenuPageItem(Player p, Quest q, int page) {
			return getItem(p, missionsMenuPageItem, missionsMenuPageDesc, 
					H.PAGE_HOLDER, String.valueOf(page),
					H.QUEST_NAME, q.getDisplayName());
		}

		public static ItemStack getMissionsMenuPreviusPageItem(Player p, Quest q, int page) {
			return getItem(p, missionsMenuPreviusPageItem, missionsMenuPreviusPageDesc, H.PAGE_HOLDER,
					String.valueOf(page), H.QUEST_NAME, q.getDisplayName());
		}

		public static ItemStack getMissionsMenuNextPageItem(Player p, Quest q, int page) {
			return getItem(p, missionsMenuNextPageItem, missionsMenuNextPageDesc, H.PAGE_HOLDER, String.valueOf(page),
					H.QUEST_NAME, q.getDisplayName());
		}

		public static ItemStack getMissionsMenuBackItem(Player p, Quest q, int page) {
			return getItem(p, missionsMenuBackItem, missionsMenuBackDesc, H.PAGE_HOLDER, String.valueOf(page),
					H.QUEST_NAME, q.getDisplayName());
		}

		public static ItemStack getMissionsMenuCloseItem(Player p, int page) {
			return getItem(p, missionsMenuCloseItem, missionsMenuCloseDesc, H.PAGE_HOLDER, String.valueOf(page));
		}

	}

	public static String getTaskActionName(String taskName) {
		return config.getString(PATH_TASK_ACTION + taskName, taskName.toLowerCase());
	}
}
