package emanondev.quests.task.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;

public class MythicMobKillTaskType extends TaskType{
	public MythicMobKillTaskType() {
		super("KillMythicMob");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onKill(MythicMobDeathEvent event) {
		if (event.getKiller()==null || !(event.getKiller() instanceof Player))
			return;
		Player p = (Player) event.getKiller();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			MythicMobKillTask task = (MythicMobKillTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.isValidMythicMob(event.getMobType().getInternalName())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.removeDrops())
						event.setDrops(new ArrayList<ItemStack>());
					if (task.drops.removeExp())
						event.setExp(0);
				}
			}
		}
	}
	
	public class MythicMobKillTask extends AbstractTask {
		private final static String PATH_INTERNAL_NAME = "mob-id";
		private final DropsTaskInfo drops;
		private final String mobName;
		//TODO option CHECKTOOL
		public MythicMobKillTask(MemorySection m, Mission parent) {
			super(m, parent,MythicMobKillTaskType.this);
			drops = new DropsTaskInfo(m);
			mobName = m.getString(PATH_INTERNAL_NAME,null);
		}
		public boolean isValidMythicMob(String internalName) {
			if (mobName==null || mobName.equalsIgnoreCase(internalName))
				return true;
			return false;
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new MythicMobKillTask(m,parent);
	}

}