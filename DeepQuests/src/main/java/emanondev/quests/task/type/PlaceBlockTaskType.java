package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.BlocksTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class PlaceBlockTaskType extends TaskType {

	public PlaceBlockTaskType() {
		super("placeblock");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockPlace(BlockPlaceEvent event) {
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
					&& task.blocks.isValidBlock(event.getBlock()))
				task.onProgress(qPlayer);
		}
	}
	
	public class PlaceBlockTask extends AbstractTask {
		private final BlocksTaskInfo blocks;

		public PlaceBlockTask(MemorySection m, Mission parent) {
			super(m, parent,PlaceBlockTaskType.this);
			blocks = new BlocksTaskInfo(m);
		}

	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new PlaceBlockTask(m,parent);
	}
}