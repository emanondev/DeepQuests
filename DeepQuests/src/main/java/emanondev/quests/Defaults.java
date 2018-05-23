package emanondev.quests;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.MemoryUtils;

public class Defaults {
	
	
	private static YMLConfig data = new YMLConfig(Quests.getInstance(),"defaultConfig");
	
	
	public static boolean reload() {
		if (!data.reload())
			return false;
		
		
		
		return true;
	}
	public static boolean shouldGuiItemsBeUnbreakable() {
		return data.getBoolean("gui.unbreakable-items.default-value", true);
	}

	public static class QuestDef {
		private static final String BASE_PATH = "quest.";
	
		public static long getDefaultCooldownMinutes() {
			return data.getLong(BASE_PATH+"cooldown.minutes", 1440);
		}
	
		public static boolean shouldCooldownAutogen() {
			return data.getBoolean(BASE_PATH+"cooldown.autogenerate", false);
		}
	
		public static boolean getDefaultCooldownUse() {
			return data.getBoolean(BASE_PATH+"cooldown.enable", false);
		}

		public static boolean shouldHideAutogen(DisplayState state) {
			return data.getBoolean(BASE_PATH+"display."+state.toString()+".hide"
				+".autogenerate",false);
		}

		public static boolean shouldItemAutogen(DisplayState state) {
			switch (state) {
			case ONPROGRESS:
			case UNSTARTED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".item"
					+".autogenerate",true);
			case COMPLETED:
			case COOLDOWN:
			case LOCKED:
			case FAILED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".item"
					+".autogenerate",false);
			default:
				throw new IllegalArgumentException();
			}
		}

		public static boolean shouldLoreAutogen(DisplayState state) {
			switch (state) {
			case ONPROGRESS:
			case UNSTARTED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".lore"
					+".autogenerate",true);
			case COMPLETED:
			case COOLDOWN:
			case LOCKED:
			case FAILED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".lore"
					+".autogenerate",false);
			default:
				throw new IllegalArgumentException();
			}
		}

		public static boolean shouldTitleAutogen(DisplayState state) {
			return data.getBoolean(BASE_PATH+"display."+state.toString()+".title"
					+".autogenerate",false);
		}

		public static String getDefaultTitle(DisplayState state) {
			return data.getString(BASE_PATH+"display."+state.toString()+".title"+".value",
					"&9&l&m---&r&8&l&m[--&r     &8* &9{quest-name}&r &8*&r     &8&l&m--]&9&l&m---");
		}

		public static ItemStack getDefaultItem(DisplayState state) {
			ItemStack item;
			switch (state) {
			case ONPROGRESS:
			case UNSTARTED:
				try {
					item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
						+".value","BOOK"));
					if (item==null||item.getType()==Material.AIR)
						throw new Exception();
				}catch (Exception e) {
					item = MemoryUtils.getGuiItem("BOOK");
				}
				return item;
			case COMPLETED:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:5"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:5");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","LIME_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("LIME_STAINED_GLASS_PANE");
					}
				}
				return item;
			case COOLDOWN:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:1"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:1");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","ORANGE_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("ORANGE_STAINED_GLASS_PANE");
					}
				}
				return item;
			case LOCKED:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:14"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:14");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","RED_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("RED_STAINED_GLASS_PANE");
					}
				}
				return item;
			case FAILED:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:15"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:15");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","BLACK_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("BLACK_STAINED_GLASS_PANE");
					}
				}
				return item;
			}
			throw new IllegalArgumentException();
		}

		public static List<String> getDefaultLore(DisplayState state) {
			List<String> list = MemoryUtils.getStringList(data, BASE_PATH+"display."+state.toString()+".lore"
				+".value");
			if (list!=null)
				return list;
			switch (state) {
			case ONPROGRESS:
				return Arrays.asList("on progress lore default");
			case UNSTARTED:
				return Arrays.asList("unstarted lore default");
			case COMPLETED:
				return Arrays.asList("completed lore default");
			case COOLDOWN:
				return Arrays.asList("cooldown lore default");
			case LOCKED:
				return Arrays.asList("locked lore default");
			case FAILED:
				return Arrays.asList("failed lore default");
			default:
				throw new IllegalArgumentException();
			}
		}
		public static List<String> getWorldsListDefault() {
			return MemoryUtils.getStringList(data, BASE_PATH+"disabled-world.worlds");
		}
		public static boolean shouldWorldsAutogen() {
			return data.getBoolean(BASE_PATH+"disabled-world.autogenerate",false);
		}
		public static boolean getUseWorldsAsBlacklistDefault() {
			return data.getBoolean(BASE_PATH+"disabled-world.autogenerate",true);
		}
		public static boolean getDefaultHide(DisplayState state) {
			return data.getBoolean(BASE_PATH+"display."+state.toString()+".hide"
								+".value",false);
		}
		public static boolean shouldAutogenDisplayName() {
			return data.getBoolean(BASE_PATH+"display-name.autogenerate",true);
		}
	}
	
	public static class MissionDef {
		private static final String BASE_PATH = "mission.";
	
		public static long getDefaultCooldownMinutes() {
			return data.getLong(BASE_PATH+"cooldown.minutes", 1440);
		}
	
		public static boolean shouldCooldownAutogen() {
			return data.getBoolean(BASE_PATH+"cooldown.autogenerate", false);
		}
	
		public static boolean getDefaultCooldownUse() {
			return data.getBoolean(BASE_PATH+"cooldown.enable", false);
		}

		public static boolean shouldHideAutogen(DisplayState state) {
			return data.getBoolean(BASE_PATH+"display."+state.toString()+".hide"
				+".autogenerate",false);
		}

		public static boolean shouldItemAutogen(DisplayState state) {
			switch (state) {
			case ONPROGRESS:
			case UNSTARTED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".item"
					+".autogenerate",true);
			case COMPLETED:
			case COOLDOWN:
			case LOCKED:
			case FAILED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".item"
					+".autogenerate",false);
			default:
				throw new IllegalArgumentException();
			}
		}

		public static boolean shouldLoreAutogen(DisplayState state) {
			switch (state) {
			case ONPROGRESS:
			case UNSTARTED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".lore"
					+".autogenerate",true);
			case COMPLETED:
			case COOLDOWN:
			case LOCKED:
			case FAILED:
				return data.getBoolean(BASE_PATH+"display."+state.toString()+".lore"
					+".autogenerate",false);
			default:
				throw new IllegalArgumentException();
			}
		}

		public static boolean shouldTitleAutogen(DisplayState state) {
			return data.getBoolean(BASE_PATH+"display."+state.toString()+".title"
					+".autogenerate",false);
		}
		public static String getDefaultTitle(DisplayState state) {
			return data.getString(BASE_PATH+"display."+state.toString()+".title"
			+".value","&9&l&m---&r&8&l&m[--&r     &8* &9{mission-name}&r &8*&r     &8&l&m--]&9&l&m---");
		}//TODO

		public static ItemStack getDefaultItem(DisplayState state) {
			ItemStack item;
			switch (state) {
			case ONPROGRESS:
			case UNSTARTED:
				try {
					item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
						+".value","PAPER"));
					if (item==null||item.getType()==Material.AIR)
						throw new Exception();
				}catch (Exception e) {
					item = MemoryUtils.getGuiItem("PAPER");
				}
				return item;
			case COMPLETED:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:5"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:5");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","LIME_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("LIME_STAINED_GLASS_PANE");
					}
				}
				return item;
			case COOLDOWN:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:1"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:1");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","ORANGE_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("ORANGE_STAINED_GLASS_PANE");
					}
				}
				return item;
			case FAILED:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:15"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:15");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","BLACK_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("BLACK_STAINED_GLASS_PANE");
					}
				}
				return item;
			case LOCKED:
				if (MemoryUtils.isPre113) {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","STAINED_GLASS_PANE:14"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("STAINED_GLASS_PANE:14");
					}
				}
				else {
					try {
						item = MemoryUtils.getGuiItem(data.getString(BASE_PATH+"display."+state.toString()+".item"
							+".value","RED_STAINED_GLASS_PANE"));
						if (item==null||item.getType()==Material.AIR)
							throw new Exception();
					}catch (Exception e) {
						item = MemoryUtils.getGuiItem("RED_STAINED_GLASS_PANE");
					}
				}
				return item;
			}
			throw new IllegalArgumentException();
		}

		public static List<String> getDefaultLore(DisplayState state) {
			List<String> list = MemoryUtils.getStringList(data, BASE_PATH+"display."+state.toString()+".lore"
				+".value");
			if (list!=null)
				return list;
			switch (state) {
			case ONPROGRESS:
				return Arrays.asList("on progress lore default");
			case UNSTARTED:
				return Arrays.asList("unstarted lore default");
			case COMPLETED:
				return Arrays.asList("completed lore default");
			case COOLDOWN:
				return Arrays.asList("cooldown lore default");
			case LOCKED:
				return Arrays.asList("locked lore default");
			case FAILED:
				return Arrays.asList("locked lore default");
			default:
				throw new IllegalArgumentException();
			}
		}
		public static List<String> getWorldsListDefault() {
			return MemoryUtils.getStringList(data, BASE_PATH+"disabled-world.worlds");
		}
		public static boolean shouldWorldsAutogen() {
			return data.getBoolean(BASE_PATH+"disabled-world.autogenerate",false);
		}
		public static boolean getUseWorldsAsBlacklistDefault() {
			return data.getBoolean(BASE_PATH+"disabled-world.use-as-blacklist",true);
		}
		public static boolean getDefaultHide(DisplayState state) {
			return data.getBoolean(BASE_PATH+"display."+state.toString()+".hide"
							+".value",false);
		}
		public static boolean shouldAutogenDisplayName() {
			return data.getBoolean(BASE_PATH+"display-name.autogenerate",true);
		}

		public static List<String> getDefaultStartText() {
			List<String> list = data.getStringList(BASE_PATH+"start-text");
			return list;
		}

		public static List<String> getDefaultCompleteText() {
			List<String> list = data.getStringList(BASE_PATH+"complete-text");
			return list;
		}

		public static List<String> getDefaultPauseText() {
			List<String> list = data.getStringList(BASE_PATH+"pause-text");
			return list;
		}

		public static List<String> getDefaultUnpauseText() {
			List<String> list = data.getStringList(BASE_PATH+"unpause-text");
			return list;
		}

		public static List<String> getDefaultFailText() {
			List<String> list = data.getStringList(BASE_PATH+"fail-text");
			return list;
		}

	}
	public static class TaskDef {
		private static final String BASE_PATH = "task.";
		public static List<String> getWorldsListDefault() {
			return MemoryUtils.getStringList(data, BASE_PATH+"disabled-world.worlds");
		}
		public static boolean shouldWorldsAutogen() {
			return data.getBoolean(BASE_PATH+"disabled-world.autogenerate",false);
		}
		public static boolean getUseWorldsAsBlacklistDefault() {
			return data.getBoolean(BASE_PATH+"disabled-world.use-as-blacklist",true);
		}
		public static boolean shouldAutogenDisplayName() {
			return data.getBoolean(BASE_PATH+"display-name.autogenerate",true);
		}
		public static String getUnstartedDescription() {
			return H.MISSION_GENERIC_TASK_NAME+" "+H.MISSION_GENERIC_TASK_MAX_PROGRESS;
		}
		public static String getProgressDescription() {
			return H.MISSION_GENERIC_TASK_NAME+" "+H.MISSION_GENERIC_TASK_PROGRESS
					+"/"+H.MISSION_GENERIC_TASK_MAX_PROGRESS;
		}
	}
}
