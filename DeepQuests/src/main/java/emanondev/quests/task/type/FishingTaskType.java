package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.DropsTaskInfo;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class FishingTaskType extends TaskType {
	public FishingTaskType() {
		super("Fishing");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onFishing(PlayerFishEvent event) {
		if (event.getState()!=PlayerFishEvent.State.CAUGHT_FISH)
			return;
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			FishingTask task = (FishingTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())) {
				if (task.onProgress(qPlayer)) {
					if(task.drops.isExpRemoved())
						event.setExpToDrop(0);
					if(task.drops.areDropsRemoved()&&event.getHook()!=null)
						event.getHook().remove();
				}
			}
		}
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new FishingTask(m,parent);
	}
	
	public class FishingTask extends AbstractTask {
		private final DropsTaskInfo drops;
		public FishingTask(ConfigSection m, Mission parent) {
			super(m, parent,FishingTaskType.this);
			drops = new DropsTaskInfo(m,this);
		}

		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(27, drops.getRemoveDropsButton(gui));
			gui.putButton(28, drops.getRemoveExpButton(gui));
			return gui;
		}
		
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.FISHING_ROD;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to fish some specified amount",
			"&7of specified items");
	@Override
	public List<String> getDescription() {
		return description;
	}
}
