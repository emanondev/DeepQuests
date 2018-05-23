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
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.BlocksTaskInfo;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class BreakBlockTaskType extends TaskType {

	public BreakBlockTaskType() {
		super("breakblock");
	}
	
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockBreak(BlockBreakEvent event) {
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
					if (task.drops.areDropsRemoved())
						event.setDropItems(false);
					if (task.drops.isExpRemoved())
						event.setExpToDrop(0);
				}
			}
		}
	}
	
	public class BreakBlockTask extends AbstractTask {
		private final static String PATH_CHECK_VIRGIN = "check-virgin-block";
		private final boolean checkVirgin;
		private final DropsTaskInfo drops;
		private final BlocksTaskInfo blocks;
		//TODO option CHECKTOOL
		public BreakBlockTask(MemorySection m, Mission parent) {
			super(m, parent,BreakBlockTaskType.this);
			drops = new DropsTaskInfo(m,this);
			blocks = new BlocksTaskInfo(m);
			checkVirgin = m.getBoolean(PATH_CHECK_VIRGIN,true);
			this.addToEditor(drops.getRemoveDropsEditorButtonFactory());
			this.addToEditor(drops.getRemoveExpEditorButtonFactory());
		}
		
		public boolean isValidBlock(Block block) {
			if (checkVirgin)
				return blocks.isValidBlock(block)&&Hooks.isBlockVirgin(block);
			else
				return blocks.isValidBlock(block);
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new BreakBlockTask(m,parent);
	}
}
