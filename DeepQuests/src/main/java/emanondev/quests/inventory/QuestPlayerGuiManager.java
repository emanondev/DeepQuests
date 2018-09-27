package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import emanondev.quests.Quests;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;

public class QuestPlayerGuiManager {
	public Inventory getMissionsInventory(Player target,Quest quest,boolean backButton,boolean seeAllForced) {
		QuestPlayer questPlayer = Quests.get().getPlayerManager().getQuestPlayer(target);
		return new PlayerMissionsGui(questPlayer,quest,backButton,seeAllForced).getInventory();
	}
	public Inventory getQuestsInventory(Player target,QuestManager questManager,boolean seeAllForced) {
		QuestPlayer questPlayer = Quests.get().getPlayerManager().getQuestPlayer(target);
		return new PlayerQuestsGui(questPlayer,questManager,seeAllForced).getInventory();
	}
	public Inventory getMissionsResetGui(Player target, QuestManager questManager) {
		return new MissionsResetGui(target,null,questManager).getInventory();
	}
	public Inventory getQuestsResetGui(Player target, QuestManager questManager) {
		return new QuestResetGui(target,null,questManager).getInventory();
	}
	
}



