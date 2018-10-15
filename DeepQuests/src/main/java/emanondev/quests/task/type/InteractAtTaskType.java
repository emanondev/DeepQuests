package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.LocationData;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class InteractAtTaskType extends TaskType {

	public InteractAtTaskType() {
		super("InteractAt");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockPlace(PlayerInteractEvent event) {
		if (event.getPlayer()==null || event.getAction()!=Action.RIGHT_CLICK_BLOCK || event.getClickedBlock()==null)
			return;
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			InteractAtTask task = (InteractAtTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.locData.isValidLocation(event.getClickedBlock().getLocation()))
				task.onProgress(qPlayer);
		}
	}
	
	public class InteractAtTask extends AbstractTask {
		private final LocationData locData;

		public InteractAtTask(ConfigSection m, Mission parent) {
			super(m, parent,InteractAtTaskType.this);
			locData = new LocationData(m.loadSection("interact_at"),this);
		}
		
		public TaskEditor createEditorGui(Player p,Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(0, locData.getLocationSelectorButton(gui));
			return gui;
		}

	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new InteractAtTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.STICK;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to place a specified number",
			"&7of Blocks of the selected type"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}