package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.CropsData;
import emanondev.quests.data.DropsTaskInfo;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class FarmerTaskType extends TaskType {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer() == null)
			return;
		QuestPlayer qPlayer = Quests.get().getPlayerManager().getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager().getTaskType(key));
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			FarmerTask task = (FarmerTask) tasks.get(i);
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

	public FarmerTaskType() {
		super("FarmerTask");
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new FarmerTask(m,parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_HOE;
	}

	private static final List<String> description = Arrays.asList(
			"&7Has to Break a specified number",
			"&7of growed crops");
	
	@Override
	public List<String> getDescription() {
		return description;
	}
	
	public class FarmerTask extends AbstractTask {
		private final DropsTaskInfo drops;
		private final CropsData crops;

		public FarmerTask(ConfigSection m, Mission parent) {
			super(m, parent, FarmerTaskType.this);
			drops = new DropsTaskInfo(m,this);
			crops = new CropsData(m,this);
		}
		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(0, crops.getCropsSelectorButton(gui));
			gui.putButton(9, drops.getRemoveExpButton(gui));
			gui.putButton(10, drops.getRemoveDropsButton(gui));
			return gui;
		}
		
		public boolean isValidBlock(Block b) {
			if (b==null)
				return false;
			MaterialData state = b.getState().getData();
			if (state instanceof Crops) {
				if (((Crops) state).getState()==CropState.RIPE)
					return isValidMaterial(b.getType());
			}
			else if (state instanceof NetherWarts) {
				if (((NetherWarts) state).getState()==NetherWartsState.RIPE)
					return isValidMaterial(b.getType());
			}
			return false;
		}
		private boolean isValidMaterial(Material m) {
			return crops.isValidType(m);
		}
		
		
	}

}
