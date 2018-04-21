package emanondev.quests.task;

import java.util.Collection;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import emanondev.quests.Quests;
import emanondev.quests.player.QuestPlayer;

public class BlockBreakType extends TaskType {

	private static String key;
	public BlockBreakType() {
		super("blockbreak", "break blocks",BreakBlockTask.class);
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onBlockBreak(BlockBreakEvent event) {
		Quests.getLogger("debug").log("inside listener");
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		Quests.getLogger("debug").log("point 1");
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).isWorldAllowed(event.getPlayer().getWorld()))
				tasks.get(i).onProgress(qPlayer);
		}//TODO
		Quests.getLogger("debug").log("point 2");
	}
}
