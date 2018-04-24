package emanondev.quests.task.type;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import emanondev.quests.Quests;
import emanondev.quests.hooks.Hooks;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractBlockTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class BreakBlockTaskType extends TaskType {

	private static String key;
	public BreakBlockTaskType() {
		super("breakblock", "break blocks");
		key = getKey();
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private static void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreakBlockTask task = (BreakBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) 
					 && task.isValidBlock(event.getBlock())) {
				if (task.onProgress(qPlayer)) {
					if (task.removeDrops())
						event.setDropItems(false);
					if (task.removeExp())
						event.setExpToDrop(0);
				}
			}
		}
	}
	
	public class BreakBlockTask extends AbstractBlockTask {
		private final static String PATH_CHECK_VIRGIN = "check-virgin-block";
		private final static String PATH_REMOVE_DROP = "remove-drops";
		private final static String PATH_REMOVE_EXP = "remove-exp";
		private final boolean checkVirgin;
		private final boolean removeDrops;
		private final boolean removeExp;
		//TODO option CHECKTOOL
		public BreakBlockTask(MemorySection m, Mission parent) {
			super(m, parent,BreakBlockTaskType.this);
			checkVirgin = m.getBoolean(PATH_CHECK_VIRGIN,true);
			removeDrops = m.getBoolean(PATH_REMOVE_DROP,false);
			removeExp = m.getBoolean(PATH_REMOVE_EXP,false);
		}
		@Override
		public boolean isValidBlock(Block block) {
			if (checkVirgin)
				return super.isValidBlock(block)&&Hooks.isBlockVirgin(block);
			else
				return super.isValidBlock(block);
		}
		public boolean removeDrops() {
			return removeDrops;
		}
		public boolean removeExp() {
			return removeExp;
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new BreakBlockTask(m,parent);
	}
}
