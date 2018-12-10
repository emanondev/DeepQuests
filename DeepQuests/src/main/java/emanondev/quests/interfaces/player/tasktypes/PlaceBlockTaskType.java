package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.BlockTypeData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class PlaceBlockTaskType extends ATaskType<QuestPlayer> {
	public PlaceBlockTaskType() {
		super(ID);
	}

	private final static String ID = "player_place_block";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.BRICKS).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to place blocks");
		return list;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new PlaceBlockTask(map);
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.MONITOR)
	private void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(event.getPlayer());
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			PlaceBlockTask task = (PlaceBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.blockData.isValidMaterial(event.getBlock().getType()))
				task.onProgress(qPlayer);
		}
	}

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class PlaceBlockTask extends ATask<QuestPlayer> {
		
		private BlockTypeData blockData = null;

		public PlaceBlockTask(Map<String, Object> map) {
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
		}
		public BlockTypeData getBlockTypeData() {
			return blockData;
		}
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_BLOCKDATA,blockData);
			return map;
		}
		
		public PlaceBlockTaskType getType() {
			return PlaceBlockTaskType.this;
		}
		
	}

}
