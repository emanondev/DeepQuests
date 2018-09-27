package emanondev.quests.utils;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;

public interface NoLoopQC extends QuestComponent {

	public default boolean isLoopSafe(Mission mission) {
		return true;
	}
	public default boolean isLoopSafe(Quest quest) {
		return true;
	}
	public default boolean isLoopSafe(Task task) {
		return true;
	}

}
