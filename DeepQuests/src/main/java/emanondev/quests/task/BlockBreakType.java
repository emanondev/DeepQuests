package emanondev.quests.task;

import java.util.Collection;

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
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		Collection<Task> coll = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (coll ==null||coll.isEmpty())
			return;
		for (Task t : coll) {
			if (t.isWorldAllowed(event.getPlayer().getWorld()))
				t.onProgress(qPlayer);
		}//TODO
	}
}
