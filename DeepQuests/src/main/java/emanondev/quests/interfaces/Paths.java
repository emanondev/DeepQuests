package emanondev.quests.interfaces;

import java.util.regex.Pattern;

public class Paths {
	public static final Pattern ALPHANUMERIC = Pattern.compile("[a-zA-Z_0-9]*");
	public static final String KEY = "unique-key";
	public static final String USERDATA_LAST_STARTED = "last-started";
	// public static final String USERDATA_IS_STARTED = "is-started";
	public static final String USERDATA_LAST_COMPLETED = "last-sompleted";
	public static final String USERDATA_COMPLETED_TIMES = "completed-times";
	// public static final String USERDATA_IS_FAILED = "is-failed";
	public static final String USERDATA_FAILED_TIMES = "failed-times";
	public static final String USERDATA_LAST_FAILED = "last-failed";
	public static final String USERDATA_MISSION_POINTS = "mission-points";
	public static final String USERDATA_QUEST_POINTS = "quest-points";
	public static final String USERDATA_MISSION_DATA_LIST = "mission-data-list";
	public static final String USERDATA_QUEST_DATA_LIST = "quest-data-list";
	public static final String USERDATA_TASK_DATA_LIST = "task-data-list";
	// public static final String USERDATA_IS_COMPLETED = "is-completed";
	public static final String USERDATA_TASK_PROGRESS = "progress";
	public static final String USERDATA_SEE_QUEST = "can-see-quest";
	public static final String USERDATA_SEE_MISSION = "can-see-mission";
	public static final String PRIORITY = "priority";
	public static final String WORLDS = "worlds";
	public static final String WORLDS_LIST = "list";
	public static final String WORLDS_IS_WHITELIST = "is-whitelist";
	public static final String TASK_PROGRESS_CHANCE = "progress-chance";
	public static final String TASK_MAX_PROGRESS = "max-progress";
	public static final String TASK_BAR_STYLE = "bossbar-style";
	public static final String TASK_BAR_COLOR = "bossbar-color";
	public static final String TASK_REWARDS = "rewards";
	public static final String DISPLAY_NAME = "display-name";
	public static final String TASK_PROGRESS_DESCRIPTION = "progress-description";
	public static final String TASK_UNSTARTED_DESCRIPTION = "unstarted-description";
	public static final String TASK_SHOW_BOSSBAR = "show-bossbar";
	public static final String REPEATABLE = "is-repeatable";
	public static final String COOLDOWN_MINUTES = "cooldown-minutes";
	public static final String MISSION_TASKS = "tasks";
	public static final String MISSION_START_REWARDS = "start-rewards";
	public static final String MISSION_FAIL_REWARDS = "fail-rewards";
	public static final String MISSION_COMPLETE_REWARDS = "complete-rewards";
	public static final String MISSION_REQUIRES = "requires";
	public static final String QUEST_REQUIRES = "requires";
	public static final String QUEST_MISSIONS = "missions";
	public static final String QUEST_IS_DEVELOPED = "is-developed";
	public static final String QUESTCONTAINER_QUEST_COUNTER = "quest-counter";
	public static final String QUESTCONTAINER_MISSION_COUNTER = "mission-counter";
	public static final String QUESTCONTAINER_TASK_COUNTER = "task-counter";
	public static final String QUESTCONTAINER_REWARD_COUNTER = "reward-counter";
	public static final String QUESTCONTAINER_REQUIRE_COUNTER = "require-counter";
	public static final String USERDATA_BASE = "userdata";
	public static final String TYPE_NAME = "type-name";
	public static final String QUESTCONTAINER_BASE = "questcontainer";

	public static final String DATA_ENTITYTYPE_LIST = "entitytype-list";
	public static final String DATA_ENTITYTYPE_IS_WHITELIST = "entitytype-is-whitelist";
	public static final String DATA_ENTITY_NAME = "entity-name";
	public static final String DATA_IGNORE_NPC = "ignore-npc";
	public static final String DATA_ENTITY_SPAWNREASON_LIST = "spawn-reason-list";
	public static final String DATA_ENTITY_SPAWNREASON_IS_WHITELIST = "spawn-reason-is-whitelist";
	public static final String DATA_CHECK_VIRGIN = "check-virgin-block";
	public static final String DATA_TARGET_QUEST_KEY = "target-quest";
	public static final String DATA_TARGET_MISSION_KEY = "target-mission";
	public static final String DATA_EXPERIENCE = "experience";
	public static final String DATA_REGION_LIST = "region-list";
	public static final String DATA_REGION_LIST_IS_WHITELIST = "region-is-whitelist";
	public static final String DATA_LEVEL = "level";
	public static final String DATA_DENY_ITEM_DROPS = "remove-item-drops";
	public static final String DATA_DENY_EXP_DROPS = "remove-exp-drops";
	public static final String DATA_JOB_TYPE = "job-type";
	public static final String DATA_MCMMO_SKILLTYPE = "mcmmo-skilltype";
	public static final String DATA_SOUND_VOLUME = "sound-volume";
	public static final String DATA_SOUND_PITCH = "sound-pitch";
	public static final String DATA_SOUND_NAME = "sound-name";
	public static final String DATA_PERMISSION = "permission";
	public static final String DATA_COMMAND = "command";
	public static final String DATA_LOCATION_X = "location-x";
	public static final String DATA_LOCATION_Y = "location-y";
	public static final String DATA_LOCATION_Z = "location-z";
	public static final String DATA_LOCATION_WORLD = "location-world-name";
	public static final String DATA_NPC_ID_LIST_IS_WHITELIST = "npc-id-list-is-whitelist";
	public static final String DATA_NPC_ID_LIST = "npc-id-list";
	public static final String DATA_BLOCK_TYPE_LIST = "blocktype-list";
	public static final String DATA_BLOCK_TYPE_IS_WHITELIST = "blocktype-list-is-whitelist";
	public static final String BOSSBAR_MANAGER_DURATION = "ticks-duration";
	public static final String BOSSBAR_MANAGER_DEFAULT_COLOR = "default-color";
	public static final String BOSSBAR_MANAGER_DEFAULT_STYLE = "default-style";
	public static final String BOSSBAR_MANAGER_DEFAULT_SHOWBOSSBAR = "default-show-bossbar";
	public static final String TASK_INFO_BLOCKDATA = "task-block-type-info";
	public static final String TASK_INFO_VIRGINBLOCKDATA = "task-block-virgin-info";
	public static final String TASK_INFO_DROPDATA = "task-drops-info";
	public static final String TASK_INFO_ENTITYDATA = "task-entity-info";
	public static final String TOOL_INFO_ITEM = "tool-base-item";
	public static final String TOOL_INFO_CHECK_MATERIAL = "tool-check-material";
	public static final String TOOL_INFO_CHECK_ENCHANT = "tool-check-enchant";
	public static final String TOOL_INFO_CHECK_AMOUNT = "tool-check-amount";
	public static final String TOOL_INFO_CHECK_LORE = "tool-check-lore";
	public static final String TOOL_INFO_CHECK_DISPLAY_NAME = "tool-check-display-name";
	public static final String TOOL_INFO_CHECK_LOCALIZED_NAME = "tool-check-localized-name";
	public static final String TOOL_INFO_CHECK_FLAGS = "tool-check-flags";
	public static final String TOOL_INFO_CHECK_UNBREAKABLE = "tool-check-unbreakable";
	public static final String TOOL_INFO_CHECK_ALL = "tool-check-all";
	public static final String TOOL_INFO_CHECK_ATTRIBUTES = "tool-check-attributes";
	public static final String TOOL_INFO_DEFAULT_ENCHANT_CHECK = "tool-default-enchant-check";

	public static final String TOOL_INFO_ENCHANT_CHECK_TYPE = "tool-enchants-checks";
	public static final String TOOL_INFO_USE_PLACE_HOLDER = "tool-use-placeholder";
	public static final String TASK_INFO_TOOLDATA = "task-tool-data-info";
	public static final String TOOL_ENABLE_CHECK = "tool-checks-enabled";
	public static final String TASK_INFO_TOOLDATA_TARGET = "task-tool-target-data-info";
	public static final String TASK_INFO_REGIONSDATA = "task-regions-info";
	public static final String TASK_INFO_LOCATIONDATA = "task-location-info";
	
	public static final String MYTHICMOBDATA_MAX_LV = "mm-max-lv";
	public static final String MYTHICMOBDATA_INTERNAL_NAMES = "mm-internal-names";
	public static final String MYTHICMOBDATA_MIN_LV = "mm-min-lv";
	public static final String MYTHICMOBDATA_INTERNAL_NAMES_IS_WHITELIST = "mm-internal-names-is-whitelist";
	public static final String MYTHICMOBDATA_CHECK_LV = "mm-check-lv";
	public static final String TASK_INFO_MYTHICMOBSDATA = "task-mythicmobs-info";
	public static final String TASK_INFO_NPCDATA = "task-npc-info";
	public static final String TASK_INFO_ITEM = "task-itemstack-info";
	public static final String ITEMSTACK_INFO = "itemstack";
	public static final String REQUIRE_INFO_JOBDATA = "require-job-info";
	public static final String REQUIRE_INFO_LEVELDATA = "require-level-info";
	public static final String REQUIRE_INFO_MCMMODATA = "require-mcmmo-info";
	public static final String REQUIRE_INFO_PERMISSIONDATA = "require-permission-info";
}