package emanondev.quests;

import org.bukkit.entity.EntityType;

public class Language {
	private static final YMLConfig config = new YMLConfig(Quests.getInstance(),"language");
	private static final String PATH_BLOCKS = "blocks.";
	private static final String PATH_ENTITY = "entity.";
	private static final String PATH_TIME = "time.";
	private static final String PATH_SINGLE_TIME = ".single";
	private static final String PATH_MULTIPLE_TIME = ".multi";
	private static final String PATH_CONJUNCTIONS = "conjunctions.";
	private static final String PATH_TASK_ACTION = "action.";
	
	
	public static void reload() {
		config.reload();
	}
	
	public static String getBlockName(String block) {
		return config.getString(PATH_BLOCKS+block.toUpperCase(),block);
	}
	
	public static String getEntityName(EntityType type) {
		return config.getString(PATH_ENTITY+type.toString(),
				type.toString().toLowerCase());
	}
	public static String getSingleTime(String time) {
		return config.getString(PATH_TIME+time+PATH_SINGLE_TIME,
				time.toLowerCase());
	}
	public static String getMultiTime(String time) {
		return config.getString(PATH_TIME+time+PATH_MULTIPLE_TIME,
				time.toLowerCase());
	}
	public static String getConjOr() {
		return config.getString(PATH_CONJUNCTIONS+"OR",
				"or");
	}
	public static String getConjAnd() {
		return config.getString(PATH_CONJUNCTIONS+"AND",
				"and");
	}

	public static String getConjNot() {
		return config.getString(PATH_CONJUNCTIONS+"NOT",
				"not");
	}

	public static String getTaskActionName(String taskName) {
		return config.getString(PATH_TASK_ACTION+taskName,taskName.toLowerCase());
	}
}
