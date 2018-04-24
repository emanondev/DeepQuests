package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractBlockTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class PlaceBlockTaskType extends TaskType {

	private static String key;
	public PlaceBlockTaskType() {
		super("placeblock", "place blocks");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			PlaceBlockTask task = (PlaceBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.isValidBlock(event.getBlock()))
				task.onProgress(qPlayer);
		}
	}
	
	public class PlaceBlockTask extends AbstractBlockTask {

		public PlaceBlockTask(MemorySection m, Mission parent) {
			super(m, parent,PlaceBlockTaskType.this);
		}

	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new PlaceBlockTask(m,parent);
	}
}