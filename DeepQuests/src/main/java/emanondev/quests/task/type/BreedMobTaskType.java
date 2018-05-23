package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.EntityTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class BreedMobTaskType extends TaskType {
	public BreedMobTaskType() {
		super("BreedMob");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockBreak(EntityBreedEvent event) {
		if (!(event.getBreeder() instanceof Player))
			return;
		Player p = (Player) event.getBreeder();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreedMobTask task = (BreedMobTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.entity.isValidEntity(event.getEntity())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.isExpRemoved())
						event.setExperience(0);
				}
			}
		}
	}
	
	public class BreedMobTask extends AbstractTask {
		private final EntityTaskInfo entity;
		private final DropsTaskInfo drops;
		public BreedMobTask(MemorySection m, Mission parent) {
			super(m, parent,BreedMobTaskType.this);
			entity = new EntityTaskInfo(m,this);
			drops = new DropsTaskInfo(m,this);
			this.addToEditor(entity.getEntityTypeEditorButtonFactory());
			this.addToEditor(entity.getIgnoreCitizenNPCEditorButtonFactory());
			this.addToEditor(drops.getRemoveExpEditorButtonFactory());
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new BreedMobTask(m,parent);
	}
}
