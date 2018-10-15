package emanondev.quests;

import org.bukkit.event.Listener;

import emanondev.quests.events.PlayerCompleteMissionEvent;
import emanondev.quests.events.PlayerCompleteTaskEvent;
import emanondev.quests.events.PlayerFailMissionEvent;
import emanondev.quests.events.PlayerProgressTaskEvent;
import emanondev.quests.events.PlayerStartMissionEvent;

public class ProgressLogger implements Listener {

	public static void logEvent(PlayerCompleteMissionEvent event) {
		Quests.getLogger("PlayerQuestLogs")
		.log("Mission Complete "+event.getQuestPlayer().getPlayer().getName()
				+" "+event.getMission().getDisplayName()+" ("+event.getMission().getID()+")");
	}
	public static void logEvent(PlayerFailMissionEvent event) {
		Quests.getLogger("PlayerQuestLogs")
		.log("Mission Fail "+event.getQuestPlayer().getPlayer().getName()
				+" "+event.getMission().getDisplayName()+" ("+event.getMission().getID()+")");
	}
	public static void logEvent(PlayerProgressTaskEvent event) {
		Quests.getLogger("PlayerQuestLogs")
		.log("Task Progress "+event.getQuestPlayer().getPlayer().getName()
				+" "+event.getTask().getDisplayName()+" ("+event.getTask().getID()+") +"
				+event.getProgressAmount());
	}
	public static void logEvent(PlayerStartMissionEvent event) {
		Quests.getLogger("PlayerQuestLogs")
		.log("Mission Start "+event.getQuestPlayer().getPlayer().getName()
				+" "+event.getMission().getDisplayName()+" ("+event.getMission().getID()+")");
	}
	public static void logEvent(PlayerCompleteTaskEvent event) {
		Quests.getLogger("PlayerQuestLogs")
		.log("Task Complete "+event.getQuestPlayer().getPlayer().getName()
				+" "+event.getTask().getDisplayName()+" ("+event.getTask().getID()+")");
	}
	
}
