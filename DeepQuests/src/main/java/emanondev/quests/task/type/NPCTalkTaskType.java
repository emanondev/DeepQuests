package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.NPCTaskInfo;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCTalkTaskType extends TaskType {
	public NPCTalkTaskType() {
		super("TalkNpc");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockBreak(NPCRightClickEvent event) {
		
		Player p = (Player) event.getClicker();
		QuestPlayer qPlayer = Quests.get().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCTalkTask task = (NPCTalkTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.npc.isValidNPC(event.getNPC())) {
				if (task.onProgress(qPlayer)) {
					
				}
			}
		}
	}
	
	public class NPCTalkTask extends AbstractTask {
		private final NPCTaskInfo npc;
		public NPCTalkTask(ConfigSection m, Mission parent) {
			super(m, parent,NPCTalkTaskType.this);
			npc = new NPCTaskInfo(m,this);
		}
		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(0, npc.getNpcSelectorButton(gui));
			return gui;
		}
		
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new NPCTalkTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.POPPY;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to interact (right click) a specified",
			"&7number of times with selected npc"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
