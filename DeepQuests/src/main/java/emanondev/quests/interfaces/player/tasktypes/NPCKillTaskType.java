package emanondev.quests.interfaces.player.tasktypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.DropData;
import emanondev.quests.interfaces.data.NPCData;
import emanondev.quests.interfaces.data.ToolData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;
import net.citizensnpcs.api.event.NPCDeathEvent;

public class NPCKillTaskType extends ATaskType<QuestPlayer> {
	public NPCKillTaskType() {
		super(ID);
	}

	private final static String ID = "player_kill_npc";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.GOLDEN_SWORD).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to kill NPC");
		return list;
	}

	@Override
	public String getID() {
		return ID;
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onNpcDeath(NPCDeathEvent event) {
		if (event.getNPC().getEntity()==null
				|| !(event.getNPC().getEntity() instanceof LivingEntity)
				|| ((LivingEntity) event.getNPC().getEntity()).getKiller()==null
				|| ((LivingEntity) event.getNPC().getEntity()).getKiller().hasMetadata("NPC"))
			return;
		Player p = ((LivingEntity) event.getNPC().getEntity()).getKiller();
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(p);
		if (qPlayer==null)
			return;
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCKillTask task = (NPCKillTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.npcData.isValidNPC(event.getNPC())
					 && task.toolData.isValidTool(p.getInventory().getItemInMainHand(), p)) {
				if (task.onProgress(qPlayer)>0) {
					if (task.dropsData.removeExpDrops())
						event.setDroppedExp(0);
					if (task.dropsData.removeItemDrops())
						event.getDrops().clear();
				}
			}
		}
	}

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class NPCKillTask extends ATask<QuestPlayer> {
		
		private NPCData npcData = null;
		private ToolData toolData = null;
		private DropData dropsData = null;

		public NPCKillTask(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.TASK_INFO_NPCDATA))
					npcData = (NPCData) map.get(Paths.TASK_INFO_NPCDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (npcData==null)
				npcData = new NPCData(null);
			npcData.setParent(this);
			try {
				if (map.containsKey(Paths.TASK_INFO_TOOLDATA))
					toolData = (ToolData) map.get(Paths.TASK_INFO_TOOLDATA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			if (toolData==null)
				toolData = new ToolData(null);
			toolData.setParent(this);
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
		
		public NPCData getNPCData() {
			return npcData;
		}
		
		public DropData getDropData() {
			return dropsData;
		}
		
		public ToolData getWeaponData() {
			return toolData;
		}
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_NPCDATA,npcData);
			map.put(Paths.TASK_INFO_DROPDATA,dropsData);
			if (toolData.isEnabled())
				map.put(Paths.TASK_INFO_TOOLDATA,toolData);
			return map;
		}
		
		public NPCKillTaskType getType() {
			return NPCKillTaskType.this;
		}
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new NPCKillTask(map);
	}
}
