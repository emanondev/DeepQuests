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
		public BreedMobTask(ConfigSection m, Mission parent) {
			super(m, parent,BreedMobTaskType.this);
			entity = new EntityTaskInfo(m,this);
			drops = new DropsTaskInfo(m,this);
			this.addToEditor(9,entity.getEntityTypeEditorButtonFactory());
			this.addToEditor(10,entity.getIgnoreCitizenNPCEditorButtonFactory());
			this.addToEditor(27,drops.getRemoveExpEditorButtonFactory());
		}
		
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new BreedMobTask(m,parent);
	}
	

	@Override
	public Material getGuiItemMaterial() {
		return Material.FENCE;
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
