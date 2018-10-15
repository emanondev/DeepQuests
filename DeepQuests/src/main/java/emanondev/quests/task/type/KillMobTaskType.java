package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

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

public class KillMobTaskType extends TaskType {

	public KillMobTaskType() {
		super("KillMob");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onEntityDie(EntityDeathEvent event) {
		if (event.getEntity().getKiller()==null)
			return;
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(event.getEntity().getKiller());
		if (qPlayer==null)
			return;
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
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
		}
		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(0, entityInfo.getEntityTypeSelectorButton(gui));
			gui.putButton(1, entityInfo.getSpawnReasonSelectorButton(gui));
			gui.putButton(9, entityInfo.getIgnoreCitizenButton(gui));
			gui.putButton(10, drops.getRemoveDropsButton(gui));
			gui.putButton(11, drops.getRemoveExpButton(gui));
			return gui;
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
