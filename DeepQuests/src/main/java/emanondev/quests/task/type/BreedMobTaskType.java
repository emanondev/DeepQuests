package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityBreedEvent;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.DropsTaskInfo;
import emanondev.quests.data.EntityTaskInfo;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
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
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
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
		public BreedMobTask(ConfigSection m, Mission parent) {
			super(m, parent,BreedMobTaskType.this);
			entity = new EntityTaskInfo(m,this);
			drops = new DropsTaskInfo(m,this);
		}
		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(0, entity.getEntityTypeSelectorButton(gui));
			gui.putButton(9, entity.getIgnoreCitizenButton(gui));
			gui.putButton(10, drops.getRemoveExpButton(gui));
			return gui;
		}
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new BreedMobTask(m,parent);
	}
	

	@Override
	public Material getGuiItemMaterial() {
		return Material.OAK_FENCE;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to breeding a specified number",
			"&7of Animals of the selected type"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
