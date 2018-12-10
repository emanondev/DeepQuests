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
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.DropData;
import emanondev.quests.interfaces.data.EntityData;
import emanondev.quests.interfaces.data.ToolData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class BreedMobTaskType extends ATaskType<QuestPlayer> {
	public BreedMobTaskType() {
		super(ID);
	}

	private final static String ID = "player_breed_mob";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.CARROT).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to breed mobs");
		return list;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new BreedMobTask(map);
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBreedMob(EntityBreedEvent event) {
		if (!(event.getBreeder() instanceof Player))
			return;
		Player p = (Player) event.getBreeder();
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(p);
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreedMobTask task = (BreedMobTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld())
					&& task.entityData.isValidEntity(event.getEntity())
					&& task.toolData.isValidTool(event.getBredWith(), p))
				if (task.onProgress(qPlayer)>0) {
					if (task.dropsData.removeExpDrops())
						event.setExperience(0);
				}
		}
	}
	
	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class BreedMobTask extends ATask<QuestPlayer> {
		
		private EntityData entityData = null;
		private DropData dropsData = null;
		private ToolData toolData = null;

		public BreedMobTask(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.TASK_INFO_ENTITYDATA))
					entityData = (EntityData) map.get(Paths.TASK_INFO_ENTITYDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (entityData==null)
				entityData = new EntityData(null);
			entityData.setParent(this);
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
		
		public EntityData getEntityData() {
			return entityData;
		}
		public DropData getDropData() {
			return dropsData;
		}
		public ToolData getBreedItemData() {
			return toolData;
		}
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_ENTITYDATA,entityData);
			map.put(Paths.TASK_INFO_DROPDATA,dropsData);
			if (toolData.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA,toolData);
			return map;
		}

		@Override
		public BreedMobTaskType getType() {
			return BreedMobTaskType.this;
		}
		
	}

}