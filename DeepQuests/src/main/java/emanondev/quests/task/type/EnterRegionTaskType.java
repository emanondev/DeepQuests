package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.RegionTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class EnterRegionTaskType extends TaskType {
	public EnterRegionTaskType() {
		super("EnterRegion");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onRegionEnter(RegionEnterEvent event) {
		Player p = event.getPlayer();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			EnterRegionTask task = (EnterRegionTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.regionInfo.isValidRegion(event.getRegion())) {
				task.onProgress(qPlayer);
			}
		}
	}
	
	public class EnterRegionTask extends AbstractTask {
		private final RegionTaskInfo regionInfo;
		
		public EnterRegionTask(MemorySection m, Mission parent) {
			super(m, parent,EnterRegionTaskType.this);
			regionInfo = new RegionTaskInfo(m);
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new EnterRegionTask(m,parent);
	}

}
