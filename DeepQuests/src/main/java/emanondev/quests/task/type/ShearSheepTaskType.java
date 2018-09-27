package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

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

public class ShearSheepTaskType extends TaskType {
	public ShearSheepTaskType() {
		super("ShearSheep");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onShear(PlayerShearEntityEvent event) {
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			ShearSheepTask task = (ShearSheepTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) 
					 && task.entity.isValidEntity(event.getEntity())) {
				if (task.onProgress(qPlayer) 
						&& task.drops.areDropsRemoved() 
						&& event.getEntity() instanceof Sheep) {
					((Sheep) event.getEntity()).setSheared(true);
					event.setCancelled(true);
				}
			}
		}
	}
	
	public class ShearSheepTask extends AbstractTask {
		private final EntityTaskInfo entity;
		private final DropsTaskInfo drops;
		public ShearSheepTask(ConfigSection m, Mission parent) {
			super(m, parent,ShearSheepTaskType.this);
			entity = new EntityTaskInfo(m,this);
			drops = new DropsTaskInfo(m,this);
		}
		public TaskEditor createEditorGui(Player p,Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(9, entity.getEntityTypeSelectorButton(gui));
			gui.putButton(28, entity.getSpawnReasonSelectorButton(gui));
			gui.putButton(27, drops.getRemoveDropsButton(gui));
			return gui;
		}
		
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new ShearSheepTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.SHEARS;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to shear a specified number",
			"&7of times a sheep"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
