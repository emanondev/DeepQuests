package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class FishingTaskType extends TaskType {
	private static String key;
	public FishingTaskType() {
		super("Fishing", "fish sheep");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onFishing(PlayerFishEvent event) {
		if (event.getState()!=PlayerFishEvent.State.CAUGHT_FISH)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			FishingTask task = (FishingTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())) {
				if (task.onProgress(qPlayer))
					if(task.drops.removeExp())
						event.setExpToDrop(0);
				}
			}
		}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new FishingTask(m,parent);
	}
	
	public class FishingTask extends Task {
		private final DropsTaskInfo drops;
		public FishingTask(MemorySection m, Mission parent) {
			super(m, parent,FishingTaskType.this);
			drops = new DropsTaskInfo(m);
		}
		
	}

}
