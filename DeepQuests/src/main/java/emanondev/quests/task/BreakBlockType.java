package emanondev.quests.task;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import emanondev.quests.Quests;
import emanondev.quests.player.QuestPlayer;

public class BreakBlockType extends TaskType {

	private static String key;
	public BreakBlockType() {
		super("breakblock", "break blocks",BreakBlockTask.class);
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreakBlockTask task = (BreakBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.isValidBlock(event.getBlock()))
				task.onProgress(qPlayer);
		}//TODO
	}
}
