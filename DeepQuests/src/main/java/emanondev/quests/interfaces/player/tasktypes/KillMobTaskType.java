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
import org.bukkit.event.entity.EntityDeathEvent;
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

public class KillMobTaskType extends ATaskType<QuestPlayer> {

	public KillMobTaskType() {
		super(ID);
	}

	private final static String ID = "player_kill_mob";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to kill mobs");
		return list;
	}

	@Override
	public String getID() {
		return ID;
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onEntityDie(EntityDeathEvent event) {
		if (event.getEntity().getKiller()==null)
			return;
		Player p = event.getEntity().getKiller();
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(p);
		if (qPlayer==null)
			return;
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			KillMobTask task = (KillMobTask) tasks.get(i);
			if (task.isWorldAllowed(qPlayer.getPlayer().getWorld()) 
					 && task.entityData.isValidEntity(event.getEntity())
					 && task.toolData.isValidTool(p.getInventory().getItemInMainHand(), p)) {
				if (task.onProgress(qPlayer)>0) {
					if (task.dropsData.removeItemDrops())
						event.getDrops().clear();
					if (task.dropsData.removeExpDrops())
						event.setDroppedExp(0);
				}
			}
		}
		
	}

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class KillMobTask extends ATask<QuestPlayer> {
		
		private EntityData entityData = null;
		private DropData dropsData = null;
		private ToolData toolData = null;
		
		public KillMobTask(Map<String,Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.TASK_INFO_ENTITYDATA))
					dropsData = (DropData) map.get(Paths.TASK_INFO_ENTITYDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (dropsData==null)
				dropsData = new DropData(null);
			dropsData.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_DROPDATA))
					entityData = (EntityData) map.get(Paths.TASK_INFO_DROPDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (entityData==null)
				entityData = new EntityData(null);
			entityData.setParent(this);
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

		public DropData getDropData() {
			return dropsData;
		}
		public ToolData getWeaponData() {
			return toolData;
		}
		public EntityData getEntityDataData() {
			return entityData;
		}
		
		public Map<String,Object> serialize() {
			Map<String,Object> map = super.serialize();
			map.put(Paths.TASK_INFO_ENTITYDATA,entityData);
			map.put(Paths.TASK_INFO_DROPDATA, dropsData);
			if (toolData.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA, toolData);
			return map;
		}

		@Override
		public KillMobTaskType getType() {
			return KillMobTaskType.this;
		}
		
		
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new KillMobTask(map);
	}
}
