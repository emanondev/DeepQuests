package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.data.DropData;
import emanondev.quests.interfaces.data.EntityData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.interfaces.Task;
import emanondev.quests.utils.ItemBuilder;

public class ShearSheepTaskType extends ATaskType<QuestPlayer> {
	public ShearSheepTaskType() {
		super(ID);
	}

	private final static String ID = "player_shear_sheep";

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
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onShear(PlayerShearEntityEvent event) {
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(event.getPlayer());
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			ShearSheepTask task = (ShearSheepTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) 
					 && task.entityData.isValidEntity(event.getEntity())) {
				if (task.onProgress(qPlayer)>0 
						&& task.dropsData.removeItemDrops() 
						&& event.getEntity() instanceof Sheep) {
					((Sheep) event.getEntity()).setSheared(true);
					event.setCancelled(true);
					//TODO break/damage shears
				}
			}
		}
	}
	

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class ShearSheepTask extends ATask<QuestPlayer> {
		
		private EntityData entityData = null;
		private DropData dropsData = null;

		public ShearSheepTask(Map<String, Object> map) {
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
		}
		
		public EntityData getEntityData() {
			return entityData;
		}
		
		public DropData getDropData() {
			return dropsData;
		}
		public ShearSheepTaskType getType() {
			return ShearSheepTaskType.this;
		}
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_ENTITYDATA,entityData);
			map.put(Paths.TASK_INFO_DROPDATA,dropsData);
			return map;
		}
		
	}


	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new ShearSheepTask(map);
	}

}
