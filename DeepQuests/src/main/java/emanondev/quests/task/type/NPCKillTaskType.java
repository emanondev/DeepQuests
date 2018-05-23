package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.NPCTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import net.citizensnpcs.api.event.NPCDeathEvent;

public class NPCKillTaskType extends TaskType {
	private static String key;
	public NPCKillTaskType() {
		super("KillNPC");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onNpcDeath(NPCDeathEvent event) {
		if (event.getNPC().getEntity()==null
				|| !(event.getNPC().getEntity() instanceof LivingEntity)
				|| ((LivingEntity) event.getNPC().getEntity()).getKiller()==null
				|| ((LivingEntity) event.getNPC().getEntity()).getKiller().hasMetadata("NPC"))
			return;
		Player p = ((LivingEntity) event.getNPC().getEntity()).getKiller();
		
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCKillTask task = (NPCKillTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.npc.isValidNPC(event.getNPC())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.isExpRemoved())
						event.setDroppedExp(0);
					if (task.drops.areDropsRemoved())
						event.getDrops().clear();
				}
			}
		}
	}
	
	public class NPCKillTask extends AbstractTask {
		private final NPCTaskInfo npc;
		private final DropsTaskInfo drops;
		public NPCKillTask(MemorySection m, Mission parent) {
			super(m, parent,NPCKillTaskType.this);
			npc = new NPCTaskInfo(m);
			drops = new DropsTaskInfo(m,this);
			this.addToEditor(drops.getRemoveDropsEditorButtonFactory());
			this.addToEditor(drops.getRemoveExpEditorButtonFactory());
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new NPCKillTask(m,parent);
	}
}
