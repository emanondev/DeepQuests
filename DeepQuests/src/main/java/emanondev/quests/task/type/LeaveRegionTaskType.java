package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.RegionTaskInfo;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class LeaveRegionTaskType extends TaskType {
	public LeaveRegionTaskType() {
		super("LeaveRegion");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onRegionLeave(RegionLeaveEvent event) {
		Player p = event.getPlayer();
		QuestPlayer qPlayer = Quests.get().getPlayerManager().getQuestPlayer(p);
		if (qPlayer==null)
			return;
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager().getTaskType(key));
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			LeaveRegionTask task = (LeaveRegionTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) && task.regionInfo.isValidRegion(event.getRegion())) {
				task.onProgress(qPlayer);
			}
		}
	}

	public class LeaveRegionTask extends AbstractTask {
		private final RegionTaskInfo regionInfo;

		public LeaveRegionTask(ConfigSection m, Mission parent) {
			super(m, parent, LeaveRegionTaskType.this);
			regionInfo = new RegionTaskInfo(m, this);
		}

		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(9, regionInfo.getRegionSelectorButton(gui));
			return gui;
		}

	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new LeaveRegionTask(m, parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.COMPASS;
	}

	private static final List<String> description = Arrays.asList("&7Player has leave a specified region",
			"&7for a specified amount of times");

	@Override
	public List<String> getDescription() {
		return description;
	}
}