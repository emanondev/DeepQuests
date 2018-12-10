package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.DropData;
import emanondev.quests.interfaces.data.ToolData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class FishingTaskType extends ATaskType<QuestPlayer> {
	public FishingTaskType() {
		super(ID);
	}

	private static final String ID = "player_fishing";

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onFishing(PlayerFishEvent event) {
		if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH)
			return;
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager().getUserManager().getUser(event.getPlayer());
		List<Task<QuestPlayer>> tasks = qPlayer.getData()
				.getActiveTasks(Quests.get().getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			FishingTask task = (FishingTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.fishingRod.isValidTool(getFishingRod(event.getPlayer()), event.getPlayer())) {
				if (!task.fishedItem.isEnabled() || (event.getCaught() instanceof Item
						&& task.fishedItem.isValidTool(((Item) event.getCaught()).getItemStack(), event.getPlayer()))) {
					if (task.onProgress(qPlayer) > 0) {
						if (task.dropsData.removeExpDrops())
							event.setExpToDrop(0);
						if (task.dropsData.removeItemDrops() && event.getHook() != null)
							event.getHook().remove();
					}
				}
			}
		}
	}

	private static ItemStack getFishingRod(Player p) {
		ItemStack hand = p.getInventory().getItemInMainHand();
		if (hand != null && hand.getType() == Material.FISHING_ROD)
			return hand;
		ItemStack offHand = p.getInventory().getItemInOffHand();
		if (offHand != null && offHand.getType() == Material.FISHING_ROD)
			return offHand;
		return hand;
	}

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class FishingTask extends ATask<QuestPlayer> {
		private DropData dropsData = null;
		private ToolData fishingRod = null;
		private ToolData fishedItem = null;

		public FishingTask(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.TASK_INFO_DROPDATA))
					dropsData = (DropData) map.get(Paths.TASK_INFO_DROPDATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (dropsData == null)
				dropsData = new DropData(null);
			dropsData.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_TOOLDATA))
					fishingRod = (ToolData) map.get(Paths.TASK_INFO_TOOLDATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (fishingRod == null)
				fishingRod = new ToolData(null);
			fishingRod.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_TOOLDATA_TARGET))
					fishedItem = (ToolData) map.get(Paths.TASK_INFO_TOOLDATA_TARGET);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (fishedItem == null)
				fishedItem = new ToolData(null);
			fishedItem.setParent(this);
		}
		
		public DropData getDropData() {
			return dropsData;
		}
		
		public ToolData getFishingRodData() {
			return fishingRod;
		}
		
		public ToolData getFishedItemData() {
			return fishedItem;
		}
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_DROPDATA,dropsData);
			if (fishingRod.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA,fishingRod);
			if (fishedItem.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA_TARGET,fishedItem);
			return map;
		}

		@Override
		public FishingTaskType getType() {
			return FishingTaskType.this;
		}
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.FISHING_ROD).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to fish items");
		return list;
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new FishingTask(map);
	}
}
