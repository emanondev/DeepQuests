package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.EntityTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class ShearSheepTaskType extends TaskType {
	private static String key;
	public ShearSheepTaskType() {
		super("ShearSheep", "Shear sheep");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onShear(PlayerShearEntityEvent event) {
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			ShearSheepTask task = (ShearSheepTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) 
					 && task.entity.isValidEntity(event.getEntity())) {
				if (task.onProgress(qPlayer) 
						&& task.drops.removeDrops() 
						&& event.getEntity() instanceof Sheep) {
					((Sheep) event.getEntity()).setSheared(true);
					event.setCancelled(true);
				}
			}
		}
	}
	
	public class ShearSheepTask extends Task {
		private final EntityTaskInfo entity;
		private final DropsTaskInfo drops;
		public ShearSheepTask(MemorySection m, Mission parent) {
			super(m, parent,ShearSheepTaskType.this);
			entity = new EntityTaskInfo(m);
			drops = new DropsTaskInfo(m);
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new ShearSheepTask(m,parent);
	}
}
