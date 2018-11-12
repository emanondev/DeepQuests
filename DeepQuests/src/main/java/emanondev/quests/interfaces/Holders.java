package emanondev.quests.interfaces;

public class Holders {

	public static final String DISPLAY_NAME = holder("name");
	public static final String TASK_MAX_PROGRESS = holder("max-progress");
	public static final String TASK_CURRENT_PROGRESS = holder("current-progress");
	
	
	
	
	
	private static String holder(String name) {
		return "%"+name+"%";
	}
}
