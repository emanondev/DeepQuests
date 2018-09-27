package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.BlocksTaskInfo;
import emanondev.quests.data.DropsTaskInfo;
import emanondev.quests.data.VirginBlockData;
import emanondev.quests.hooks.Hooks;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class BreakBlockTaskType extends TaskType {

	public BreakBlockTaskType() {
		super("breakblock");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer() == null)
			return;
		QuestPlayer qPlayer = Quests.get().getPlayerManager().getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager().getTaskType(key));
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreakBlockTask task = (BreakBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) && task.isValidBlock(event.getBlock())) {
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
		private VirginBlockData virginBlockData;
		private final DropsTaskInfo drops;
		private final BlocksTaskInfo blocks;

		// TODO option CHECKTOOL
		public BreakBlockTask(ConfigSection m, Mission parent) {
			super(m, parent, BreakBlockTaskType.this);
			drops = new DropsTaskInfo(m, this);
			blocks = new BlocksTaskInfo(m, this);
			virginBlockData = new VirginBlockData(m,this);
		}

		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(9, blocks.getBlockSelectorButton(gui));
			gui.putButton(27, drops.getRemoveDropsButton(gui));
			gui.putButton(28, drops.getRemoveExpButton(gui));
			if (Hooks.isVirginBlockPluginEnabled())
				gui.putButton(29, virginBlockData.getVirginCheckButton(gui));
			return gui;
		}
		
		public boolean isValidBlock(Block block) {
			return blocks.isValidBlock(block) && virginBlockData.isValidBlock(block);
		}
		
		public BlocksTaskInfo getBlocksData() {
			return blocks;
		}
		public DropsTaskInfo getDropsData() {
			return drops;
		}
		public VirginBlockData getVirginBlockData() {
			return virginBlockData;
		}

		
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new BreakBlockTask(m, parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_PICKAXE;
	}

	private static final List<String> description = Arrays.asList("&7Has to Break a specified number",
			"&7of Blocks of the selected type");

	@Override
	public List<String> getDescription() {
		return description;
	}
}
