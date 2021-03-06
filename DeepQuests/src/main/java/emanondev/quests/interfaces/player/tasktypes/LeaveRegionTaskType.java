package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import com.mewin.worldguardregionapi.events.RegionLeftEvent;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.RegionsData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class LeaveRegionTaskType  extends ATaskType<QuestPlayer> {
	public LeaveRegionTaskType() {
		super(ID);
	}

	private final static String ID = "player_leave_region";

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	private void onRegionLeave(RegionLeftEvent event) {
		Player p = event.getPlayer();
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(p);
		if (qPlayer==null)
			return;
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			LeaveRegionTask task = (LeaveRegionTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) && task.regionInfo.isValidRegion(event.getRegion())) {
				task.onProgress(qPlayer);
			}
		}
	}


	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class LeaveRegionTask extends ATask<QuestPlayer> {
		private RegionsData regionInfo = null;

		public LeaveRegionTask(Map<String,Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.TASK_INFO_REGIONSDATA))
					regionInfo = (RegionsData) map.get(Paths.TASK_INFO_REGIONSDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (regionInfo==null)
				regionInfo = new RegionsData(null);
			regionInfo.setParent(this);
		}
		
		public RegionsData getRegionsData() {
			return regionInfo;
		}
		
		public Map<String,Object> serialize(){
			Map<String,Object> map = super.serialize();
			map.put(Paths.TASK_INFO_REGIONSDATA, regionInfo);
			return map;
		}

		@Override
		public LeaveRegionTaskType getType() {
			return LeaveRegionTaskType.this;
		}
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String,Object> map) {
		return new LeaveRegionTask(map);
	}

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.COMPASS).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to leave/exit region");
		return list;
	}
}