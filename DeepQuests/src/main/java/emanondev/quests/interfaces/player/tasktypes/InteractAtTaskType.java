package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.LocationData;
import emanondev.quests.interfaces.data.ToolData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class InteractAtTaskType extends ATaskType<QuestPlayer> {
	public InteractAtTaskType() {
		super(ID);
	}

	private final static String ID = "player_interact_at";
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onInteract(PlayerInteractEvent event) {
		if (event.getPlayer()==null || event.getAction()!=Action.RIGHT_CLICK_BLOCK || event.getClickedBlock()==null)
			return;
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(event.getPlayer());
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			InteractAtTask task = (InteractAtTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld())
					&& task.locData.isValidLocation(event.getClickedBlock().getLocation())
					&& task.toolData.isValidTool(event.getItem(), event.getPlayer()))
				task.onProgress(qPlayer);
		}
	}

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class InteractAtTask extends ATask<QuestPlayer> {
		private LocationData locData = null;
		private ToolData toolData = null;

		public InteractAtTask(Map<String,Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.TASK_INFO_LOCATIONDATA))
					locData = (LocationData) map.get(Paths.TASK_INFO_LOCATIONDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (locData==null)
				locData = new LocationData(null);
			locData.setParent(this);
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
		public LocationData getLocationData() {
			return locData;
		}
		public ToolData getToolData() {
			return toolData;
		}
		public Map<String,Object> serialize(){
			Map<String,Object> map = super.serialize();
			map.put(Paths.TASK_INFO_LOCATIONDATA,locData);
			if (toolData.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA,toolData);
			return map;
		}
		@Override
		public InteractAtTaskType getType() {
			return InteractAtTaskType.this;
		}
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String,Object> map) {
		return new InteractAtTask(map);
	}
	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.STICK).setGuiProperty().build();
	}
	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to interact at location");
		return list;
	}
}