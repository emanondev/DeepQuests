package emanondev.quests.newgui;

import java.util.EnumMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Quests;
import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.Utils;

public class GuiConfig {
	public static final String PAGE_HOLDER = "%page%";
	public static final String TARGET_PAGE_HOLDER = "%target_page%";
	public static final String AMOUNT_HOLDER = "%amount%";
	public static final String PERMISSION_HOLDER = "%permission%";

	private static YMLConfig guiConfig = new YMLConfig(Quests.get(), "guiconfig");

	public static void reload() {

		guiConfig.reload();
		Generic.reload();
		PlayerQuests.reload();
	}

	private static List<String> getStringList(String path) {
		try {
			return guiConfig.getStringList(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getString(String path) {
		try {
			return guiConfig.getString(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ItemStack getItem(String path) {
		try {
			if (guiConfig.isItemStack(path))
				return guiConfig.getItemStack(path);
			if (guiConfig.isString(path)) {
				String value = guiConfig.getString(path).toUpperCase();
				ItemStack item;
				short damage = 0;
				if (value.contains(":")) {
					item = new ItemStack(Material.valueOf(value.split(":")[0]));
					damage = Short.valueOf(value.split(":")[1]);
					if (damage != 0)
						item.setDurability(damage);
				} else {
					item = new ItemStack(Material.valueOf(value));
				}

				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
						ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
				item.setItemMeta(meta);
				return item;
			}
			throw new Exception("Path " + path + " not found on file " + guiConfig.getFile().getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(meta);
		return item;
	}

	public static class Generic {
		private static List<String> getStringList(String path) {
			return GuiConfig.getStringList(BASE_PATH + path);
		}

		private static String getString(String path) {
			return GuiConfig.getString(BASE_PATH + path);
		}

		private static ItemStack getItem(String path) {
			return GuiConfig.getItem(BASE_PATH + path);
		}

		private static final String BASE_PATH = "generic.";

		private static void reload() {

			NEXT_PAGE = getStringList("next_page");
			NEXT_PAGE_ITEM = getItem("next_page_item");

			PREVIUS_PAGE = getStringList("previus_page");
			PREVIUS_PAGE_ITEM = getItem("previus_page_item");

			EMPTY_BUTTON_ITEM = getItem("empty_button_item");

			BACK_INVENTORY = getStringList("back_inventory");
			BACK_INVENTORY_ITEM = getItem("back_inventory_item");

			CLOSE_INVENTORY = getStringList("close_inventory");
			CLOSE_INVENTORY_ITEM = getItem("close_inventory_item");

			COMMAND_FOR_PLAYERS_ONLY = Utils.fixString(getString("command_for_players_only"), null, true);

			LACK_OF_PERMISSION = Utils.fixString(getString("lack_of_permission"), null, true);

			AMOUNT_SELECTOR_SHOW = getStringList("amount_selector_show");
			AMOUNT_SELECTOR_ADD = getStringList("amount_selector_add");
			AMOUNT_SELECTOR_REMOVE = getStringList("amount_selector_remove");

			WHITELIST_DESCRIPTION = getStringList("whitelist_description");
			// WHITELIST_ITEM = ;
			BLACKLIST_DESCRIPTION = getStringList("blacklist_description");
			// BLACKLIST_ITEM = ;
			NULL_ELEMENT = getStringList("null_element");

			NO_VALUE_SET = Utils.fixString(getString("no_value_set"), null, true);

			INVALID_NUMBER = Utils.fixString(getString("invalid_number"), null, true);

			NOT_A_NUMBER = Utils.fixString(getString("not_a_number"), null, true);

			INVALID_INPUT = Utils.fixString(getString("invalid_input"), null, true);

			NO_TEXT_CHANGES = Utils.fixString(getString("no_text_changes"), null, true);
			CONFIRM_CLICK_GUI_TITLE = Utils.fixString(getString("confirm_click_gui_title"), null, true);
			
			CONFIRM_BUTTON_DESCRIPTION = getStringList("confirm_button_description");
			UNCONFIRM_BUTTON_DESCRIPTION = getStringList("unconfirm_button_description");
			CONFIRM_BUTTON_ITEM = getItem("confirm_button_item");
			UNCONFIRM_BUTTON_ITEM = getItem("unconfirm_button_item");
		}
		
		public static List<String> AMOUNT_SELECTOR_ADD;
		public static List<String> AMOUNT_SELECTOR_REMOVE;
		public static List<String> AMOUNT_SELECTOR_SHOW;
		public static List<String> NEXT_PAGE;
		public static ItemStack NEXT_PAGE_ITEM;
		public static List<String> PREVIUS_PAGE;
		public static ItemStack PREVIUS_PAGE_ITEM;
		public static List<String> BACK_INVENTORY;
		public static ItemStack BACK_INVENTORY_ITEM;
		public static List<String> CLOSE_INVENTORY;
		public static ItemStack CLOSE_INVENTORY_ITEM;
		public static ItemStack EMPTY_BUTTON_ITEM;
		public static String COMMAND_FOR_PLAYERS_ONLY;
		public static String LACK_OF_PERMISSION;
		public static List<String> WHITELIST_DESCRIPTION;
		public static ItemStack WHITELIST_ITEM;
		public static List<String> BLACKLIST_DESCRIPTION;
		public static ItemStack BLACKLIST_ITEM;
		public static List<String> NULL_ELEMENT;
		public static String NO_VALUE_SET;
		public static String INVALID_NUMBER;
		public static String INVALID_INPUT;
		public static String NOT_A_NUMBER;
		public static String NO_TEXT_CHANGES;
		public static String CONFIRM_CLICK_GUI_TITLE;
		

		public static List<String> CONFIRM_BUTTON_DESCRIPTION;
		public static List<String> UNCONFIRM_BUTTON_DESCRIPTION;
		public static ItemStack CONFIRM_BUTTON_ITEM;
		public static ItemStack UNCONFIRM_BUTTON_ITEM;

		public static ItemStack getConfirmButtonItem(Player p) {
			return Utils.setDescription(CONFIRM_BUTTON_ITEM, CONFIRM_BUTTON_DESCRIPTION, p, true);
		}
		public static ItemStack getUnconfirmButtonItem(Player p) {
			return Utils.setDescription(UNCONFIRM_BUTTON_ITEM, UNCONFIRM_BUTTON_DESCRIPTION, p, true);
		}
	}

	public static class PlayerQuests {
		private static List<String> getStringList(String path) {
			return GuiConfig.getStringList(BASE_PATH + path);
		}

		@SuppressWarnings("unused")
		private static String getString(String path) {
			return GuiConfig.getString(BASE_PATH + path);
		}

		private static ItemStack getItem(String path) {
			return GuiConfig.getItem(BASE_PATH + path);
		}

		private static final String BASE_PATH = "playerquests.";

		private static void reload() {
			for (DisplayState state : DisplayState.values()) {
				QUEST_ACTIVE_DISPLAY_FLAG.put(state, Utils.setDescription(
						getItem("questshowflag.active." + state.toString().toLowerCase() + ".item"),
						getStringList("questshowflag.active." + state.toString().toLowerCase() + ".desc"), null, true));
				QUEST_INACTIVE_DISPLAY_FLAG.put(state,
						Utils.setDescription(
								getItem("questshowflag.inactive." + state.toString().toLowerCase() + ".item"),
								getStringList("questshowflag.inactive." + state.toString().toLowerCase() + ".desc"),
								null, true));
				MISSION_ACTIVE_DISPLAY_FLAG.put(state,
						Utils.setDescription(
								getItem("missionshowflag.active." + state.toString().toLowerCase() + ".item"),
								getStringList("missionshowflag.active." + state.toString().toLowerCase() + ".desc"),
								null, true));
				MISSION_INACTIVE_DISPLAY_FLAG.put(state,
						Utils.setDescription(
								getItem("missionshowflag.inactive." + state.toString().toLowerCase() + ".item"),
								getStringList("missionshowflag.inactive." + state.toString().toLowerCase() + ".desc"),
								null, true));
			}
		}

		public static ItemStack getQuestDisplayFlagItem(DisplayState state, boolean active) {
			if (active)
				return QUEST_ACTIVE_DISPLAY_FLAG.get(state);
			return QUEST_INACTIVE_DISPLAY_FLAG.get(state);
		}

		public static ItemStack getMissionDisplayFlagItem(DisplayState state, boolean active) {
			if (active)
				return MISSION_ACTIVE_DISPLAY_FLAG.get(state);
			return MISSION_INACTIVE_DISPLAY_FLAG.get(state);
		}

		public static EnumMap<DisplayState, ItemStack> QUEST_ACTIVE_DISPLAY_FLAG = new EnumMap<DisplayState, ItemStack>(
				DisplayState.class);
		public static EnumMap<DisplayState, ItemStack> QUEST_INACTIVE_DISPLAY_FLAG = new EnumMap<DisplayState, ItemStack>(
				DisplayState.class);
		public static EnumMap<DisplayState, ItemStack> MISSION_ACTIVE_DISPLAY_FLAG = new EnumMap<DisplayState, ItemStack>(
				DisplayState.class);
		public static EnumMap<DisplayState, ItemStack> MISSION_INACTIVE_DISPLAY_FLAG = new EnumMap<DisplayState, ItemStack>(
				DisplayState.class);

	}
}
