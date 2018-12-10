package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.BlockTypeData;
import emanondev.quests.interfaces.data.DropData;
import emanondev.quests.interfaces.data.ToolData;
import emanondev.quests.interfaces.data.VirginBlockData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class BreakBlockTaskType extends ATaskType<QuestPlayer> {
	public BreakBlockTaskType() {
		super(ID);
	}

	private final static String ID = "player_break_block";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to break blocks");
		return list;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new BreakBlockTask(map);
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(event.getPlayer());
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreakBlockTask task = (BreakBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.blockData.isValidMaterial(event.getBlock().getType())
					&& task.virginBlockData.isValidBlock(event.getBlock())
					&& task.toolData.isValidTool(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer()))
				if (task.onProgress(qPlayer)>0) {
					if (task.dropsData.removeExpDrops())
						event.setExpToDrop(0);
					if (task.dropsData.removeItemDrops())
						event.setDropItems(false);
				}
		}
	}
	
	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class BreakBlockTask extends ATask<QuestPlayer> {
		
		private BlockTypeData blockData = null;
		private VirginBlockData virginBlockData = null;
		private DropData dropsData = null;
		private ToolData toolData = null;

		public BreakBlockTask(Map<String, Object> map) {
			super(map);
			try {
				if (map.containsKey(Paths.TASK_INFO_BLOCKDATA))
					blockData = (BlockTypeData) map.get(Paths.TASK_INFO_BLOCKDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (blockData==null)
				blockData = new BlockTypeData(null);
			blockData.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_VIRGINBLOCKDATA))
					virginBlockData = (VirginBlockData) map.get(Paths.TASK_INFO_VIRGINBLOCKDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (virginBlockData==null)
				virginBlockData = new VirginBlockData(null);
			virginBlockData.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_DROPDATA))
					dropsData = (DropData) map.get(Paths.TASK_INFO_DROPDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (dropsData==null)
				dropsData = new DropData(null);
			dropsData.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_TOOLDATA))
					toolData = (ToolData) map.get(Paths.TASK_INFO_TOOLDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (toolData==null)
				toolData = new ToolData(null);
			toolData.setParent(this);
		}
		
		public BlockTypeData getBlockTypeData() {
			return blockData;
		}
		public VirginBlockData getVirginBlockData() {
			return virginBlockData;
		}
		public DropData getDropData() {
			return dropsData;
		}
		public ToolData getToolData() {
			return toolData;
		}
		
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_BLOCKDATA,blockData);
			map.put(Paths.TASK_INFO_VIRGINBLOCKDATA,virginBlockData);
			map.put(Paths.TASK_INFO_DROPDATA,dropsData);
			if (toolData.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA,toolData);
			return map;
		}

		@Override
		public BreakBlockTaskType getType() {
			return BreakBlockTaskType.this;
		}
		
	}

}