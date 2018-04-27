package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.NPCTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCTalkTaskType extends TaskType {
	private static String key;
	public NPCTalkTaskType() {
		super("TalkNpc", "Talk to NPC");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onBlockBreak(NPCRightClickEvent event) {
		
		Player p = (Player) event.getClicker();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCTalkTask task = (NPCTalkTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.npc.isValidNPC(event.getNPC())) {
				if (task.onProgress(qPlayer)) {
					
				}
			}
		}
	}
	
	public class NPCTalkTask extends Task {
		private final NPCTaskInfo npc;
		public NPCTalkTask(MemorySection m, Mission parent) {
			super(m, parent,NPCTalkTaskType.this);
			npc = new NPCTaskInfo(m);
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new NPCTalkTask(m,parent);
	}
}
