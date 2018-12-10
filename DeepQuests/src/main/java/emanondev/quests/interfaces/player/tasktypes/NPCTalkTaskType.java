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

import emanondev.quests.Quests;
import emanondev.quests.interfaces.ATask;
import emanondev.quests.interfaces.ATaskType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.data.NPCData;
import emanondev.quests.interfaces.player.PlayerTaskManager;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCTalkTaskType extends ATaskType<QuestPlayer> {
	public NPCTalkTaskType() {
		super(ID);
	}

	private final static String ID = "player_talk_to_npc";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.POPPY).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		List<String> list = new ArrayList<>();
		list.add("&7Player have to interact with NPC");
		return list;
	}

	@Override
	public String getID() {
		return ID;
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onRightClick(NPCRightClickEvent event) {
		Player p = (Player) event.getClicker();
		QuestPlayer qPlayer = Quests.get().getDefaultQuestManager()
				.getUserManager().getUser(p);
		List<Task<QuestPlayer>> tasks = qPlayer.getData().getActiveTasks(Quests.get()
				.getDefaultQuestManager().getTaskManager().getType(ID));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCTalkTask task = (NPCTalkTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.npcData.isValidNPC(event.getNPC())) {
				if (task.onProgress(qPlayer)>0) {
					//TODO ??
				}
			}
		}
	}

	@SerializableAs("PlayerTask")
	@DelegateDeserialization(PlayerTaskManager.class)
	public class NPCTalkTask extends ATask<QuestPlayer> {
		
		private NPCData npcData = null;

		public NPCTalkTask(Map<String, Object> map) {
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
		}
		
		public NPCData getNPCData() {
			return npcData;
		}
		
		public Map<String,Object> serialize() {
			Map<String, Object> map = super.serialize();
			map.put(Paths.TASK_INFO_NPCDATA,npcData);
			return map;
		}
		
		public NPCTalkTaskType getType() {
			return NPCTalkTaskType.this;
		}
	}

	@Override
	public Task<QuestPlayer> getInstance(Map<String, Object> map) {
		return new NPCTalkTask(map);
	}
}
