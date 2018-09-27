package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.DropsTaskInfo;
import emanondev.quests.data.NPCTaskInfo;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import net.citizensnpcs.api.event.NPCDeathEvent;

public class NPCKillTaskType extends TaskType {
	private static String key;
	public NPCKillTaskType() {
		super("KillNPC");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onNpcDeath(NPCDeathEvent event) {
		if (event.getNPC().getEntity()==null
				|| !(event.getNPC().getEntity() instanceof LivingEntity)
				|| ((LivingEntity) event.getNPC().getEntity()).getKiller()==null
				|| ((LivingEntity) event.getNPC().getEntity()).getKiller().hasMetadata("NPC"))
			return;
		Player p = ((LivingEntity) event.getNPC().getEntity()).getKiller();
		
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCKillTask task = (NPCKillTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.npc.isValidNPC(event.getNPC())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.isExpRemoved())
						event.setDroppedExp(0);
					if (task.drops.areDropsRemoved())
						event.getDrops().clear();
				}
			}
		}
	}
	
	public class NPCKillTask extends AbstractTask {
		private final NPCTaskInfo npc;
		private final DropsTaskInfo drops;
		public NPCKillTask(ConfigSection m, Mission parent) {
			super(m, parent,NPCKillTaskType.this);
			npc = new NPCTaskInfo(m,this);
			drops = new DropsTaskInfo(m,this);
		}

		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(27, drops.getRemoveDropsButton(gui));
			gui.putButton(28, drops.getRemoveExpButton(gui));
			gui.putButton(9, npc.getNpcSelectorButton(gui));
			return gui;
		}
		
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new NPCKillTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.DIAMOND_SWORD;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to kill a specified number",
			"&7of npc with selected name/id"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
