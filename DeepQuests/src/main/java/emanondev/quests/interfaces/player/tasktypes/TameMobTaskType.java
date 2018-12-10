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
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.data.EntityData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.interfaces.Task;
import emanondev.quests.utils.ItemBuilder;

public class TameMobTaskType extends ATaskType<QuestPlayer> {
	public TameMobTaskType() {
		super(ID);
	}

	private final static String ID = "player_tame_mob";

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
	private void onTaming(EntityTameEvent event) {
		if (!(event.getOwner() instanceof Player))
			return;
		Player p = (Player) event.getOwner();
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(p);
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			TameMobTask task = (TameMobTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.entityData.isValidEntity(event.getEntity())) {
				task.onProgress(qPlayer);
			}
		}
	}
	
	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class TameMobTask extends ATask<QuestPlayer> {
		
		private EntityData entityData = null;

		public TameMobTask(Map<String, Object> map) {
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
		}
		
		public EntityData getEntityData() {
			return entityData;
		}
		
		public TameMobTaskType getType() {
			return TameMobTaskType.this;
		}
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_ENTITYDATA,entityData);
			return map;
		}
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new TameMobTask(map);
	}


}
