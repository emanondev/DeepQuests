package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.EntityTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class KillMobTaskType extends TaskType {

	public KillMobTaskType() {
		super("KillMob");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onEntityDie(EntityDeathEvent event) {
		if (event.getEntity().getKiller()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getEntity().getKiller());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			KillMobTask task = (KillMobTask) tasks.get(i);
			if (task.isWorldAllowed(qPlayer.getPlayer().getWorld()) 
					 && task.entityInfo.isValidEntity(event.getEntity())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.removeDrops())
						event.getDrops().clear();
					if (task.drops.removeExp())
						event.setDroppedExp(0);
				}
			}
		}
		
	}
	public class KillMobTask extends AbstractTask {
		private final EntityTaskInfo entityInfo;
		private final DropsTaskInfo drops;
		
		public KillMobTask(MemorySection m, Mission parent) {
			super(m, parent, KillMobTaskType.this);
			entityInfo = new EntityTaskInfo(m,this);
			drops = new DropsTaskInfo(m);
			this.addToEditor(entityInfo.getEntityTypeEditorButton());
			this.addToEditor(entityInfo.getSpawnReasonEditorButton());
			this.addToEditor(entityInfo.getIgnoreCitizenNPCEditorButton());
		}
	}
	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new KillMobTask(m,parent);
	}
	
	

}
