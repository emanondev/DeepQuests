package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
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
		if (qPlayer==null)
			return;
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			KillMobTask task = (KillMobTask) tasks.get(i);
			if (task.isWorldAllowed(qPlayer.getPlayer().getWorld()) 
					 && task.entityInfo.isValidEntity(event.getEntity())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.areDropsRemoved())
						event.getDrops().clear();
					if (task.drops.isExpRemoved())
						event.setDroppedExp(0);
				}
			}
		}
		
	}
	public class KillMobTask extends AbstractTask {
		private final EntityTaskInfo entityInfo;
		private final DropsTaskInfo drops;
		
		public KillMobTask(ConfigSection m, Mission parent) {
			super(m, parent, KillMobTaskType.this);
			entityInfo = new EntityTaskInfo(m,this);
			drops = new DropsTaskInfo(m,this);
			this.addToEditor(9,entityInfo.getEntityTypeEditorButtonFactory());
			this.addToEditor(10,entityInfo.getSpawnReasonEditorButtonFactory());
			this.addToEditor(29,entityInfo.getIgnoreCitizenNPCEditorButtonFactory());
			this.addToEditor(27,drops.getRemoveDropsEditorButtonFactory());
			this.addToEditor(28,drops.getRemoveExpEditorButtonFactory());
		}
	}
	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new KillMobTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_SWORD;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to kill a specified number",
			"&7of entity of the selected type"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
