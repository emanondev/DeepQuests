package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.RegionTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class LeaveRegionTaskType extends TaskType {
	public LeaveRegionTaskType() {
		super("LeaveRegion");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onRegionLeave(RegionLeaveEvent event) {
		Player p = event.getPlayer();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			LeaveRegionTask task = (LeaveRegionTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.regionInfo.isValidRegion(event.getRegion())) {
				task.onProgress(qPlayer);
			}
		}
	}
	
	public class LeaveRegionTask extends AbstractTask {
		private final RegionTaskInfo regionInfo;
		
		public LeaveRegionTask(MemorySection m, Mission parent) {
			super(m, parent,LeaveRegionTaskType.this);
			regionInfo = new RegionTaskInfo(m);
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new LeaveRegionTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.COMPASS;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has leave a specified region",
			"&7for a specified amount of times"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}