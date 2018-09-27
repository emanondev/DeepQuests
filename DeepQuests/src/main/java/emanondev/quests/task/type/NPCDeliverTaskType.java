package emanondev.quests.task.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.ItemStackData;
import emanondev.quests.data.NPCTaskInfo;
import emanondev.quests.inventory.InventoryUtils;
import emanondev.quests.inventory.InventoryUtils.ExcessManage;
import emanondev.quests.inventory.InventoryUtils.LackManage;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class NPCDeliverTaskType extends TaskType {
	private static String key;

	public NPCDeliverTaskType() {
		super("DeliverToNPC");
		key = getKey();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	private void onNPCRightClickEvent(NPCRightClickEvent event) {

		Player p = (Player) event.getClicker();
		QuestPlayer qPlayer = Quests.get().getPlayerManager().getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.get().getTaskManager().getTaskType(key));
		if (tasks == null || tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			NPCDeliverTask task = (NPCDeliverTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) && task.npc.isValidNPC(event.getNPC())) {
				int removedAmount = InventoryUtils.removeAmount(p, task.itemInfo.getItem(),
						task.getMaxProgress() - qPlayer.getTaskProgress(task), LackManage.REMOVE_MAX_POSSIBLE);
				if (removedAmount>0)
					if (task.onProgress(qPlayer, removedAmount)) {

					} else
						InventoryUtils.giveAmount(p, task.itemInfo.getItem(), removedAmount, ExcessManage.DROP_EXCESS);
			}
		}
	}

	public class NPCDeliverTask extends AbstractTask {
		private final NPCTaskInfo npc;
		private final ItemStackData itemInfo;

		public NPCDeliverTask(ConfigSection m, Mission parent) {
			super(m, parent, NPCDeliverTaskType.this);
			npc = new NPCTaskInfo(m, this);
			itemInfo = new ItemStackData(m, this);
		}
		public TaskEditor createEditorGui(Player p, Gui previusHolder) {
			TaskEditor gui = super.createEditorGui(p, previusHolder);
			gui.putButton(10, itemInfo.getItemSelectorButton(gui));
			gui.putButton(9, npc.getNpcSelectorButton(gui));
			return gui;
		}

	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new NPCDeliverTask(m, parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.CHEST;
	}

	private static final List<String> description = Arrays.asList("&7Player has give to the npc a specified",
			"&7number of specified items to selected npc");

	@Override
	public List<String> getDescription() {
		return description;
	}
}