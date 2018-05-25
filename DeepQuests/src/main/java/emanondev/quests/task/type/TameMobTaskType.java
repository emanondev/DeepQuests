package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.EntityTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class TameMobTaskType extends TaskType {
	public TameMobTaskType() {
		super("TameMob");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onTaming(EntityTameEvent event) {
		if (!(event.getOwner() instanceof Player))
			return;
		Player p = (Player) event.getOwner();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			TameMobTask task = (TameMobTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.entity.isValidEntity(event.getEntity())) {
				task.onProgress(qPlayer);
			}
		}
	}
	
	public class TameMobTask extends AbstractTask {
		private final EntityTaskInfo entity;
		//TODO option CHECKTOOL
		public TameMobTask(MemorySection m, Mission parent) {
			super(m, parent,TameMobTaskType.this);
			entity = new EntityTaskInfo(m,this);
			this.addToEditor(9,entity.getEntityTypeEditorButtonFactory());
			this.addToEditor(10,entity.getSpawnReasonEditorButtonFactory());
			this.addToEditor(27,entity.getIgnoreCitizenNPCEditorButtonFactory());
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new TameMobTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.BONE;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to tame a specified number",
			"&7of entity of the selected type"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
