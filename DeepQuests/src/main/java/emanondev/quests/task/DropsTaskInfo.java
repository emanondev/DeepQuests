package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;

public class DropsTaskInfo {

	private final static String PATH_REMOVE_DROP = "remove-drops";
	private final static String PATH_REMOVE_EXP = "remove-exp";
	private final boolean removeDrops;
	private final boolean removeExp;
	public DropsTaskInfo(MemorySection m) {
		removeDrops = m.getBoolean(PATH_REMOVE_DROP,false);
		removeExp = m.getBoolean(PATH_REMOVE_EXP,false);
	}
	
	public boolean removeDrops() {
		return removeDrops;
	}
	public boolean removeExp() {
		return removeExp;
	}

}
