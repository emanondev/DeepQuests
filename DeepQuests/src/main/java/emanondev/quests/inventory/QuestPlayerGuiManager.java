package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import emanondev.quests.quest.QuestManager;

public class QuestPlayerGuiManager {
	public Inventory getMissionsResetGui(Player target, QuestManager questManager) {
		return new MissionsResetGui(target,null,questManager).getInventory();
	}
	
}



