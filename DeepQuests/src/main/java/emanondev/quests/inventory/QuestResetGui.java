package emanondev.quests.inventory;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class QuestResetGui extends AbstractQuestPlayerGui {

	private QuestManager questManager;
	public QuestResetGui(Player target, CustomGui previusHolder,QuestManager questManager) {
		super(Quests.getInstance().getPlayerManager().getQuestPlayer(target), previusHolder);
		this.questManager = questManager;
		this.setTitle(null, ChatColor.RED+"Click to reset Quest for "+target.getPlayer().getName());
		update();
	}
	public void update() {
		this.items.clear();
		for (Quest quest:questManager.getQuests()) {
			this.items.add(new QuestResetButton(this,quest));
		}
		
		super.update();
	}
	
	private class QuestResetButton extends CustomButton {
		private final Quest quest;
		private ItemStack item;
		public QuestResetButton(QuestResetGui questResetGui, Quest quest) {
			super(questResetGui);
			this.quest = quest;
			item = new ItemStack(Material.BOOK);
			update();
		}
		public void update() {
			DisplayState state = QuestResetGui.this.getQuestPlayer().getDisplayState(quest);
			this.item.setType(quest.getDisplayInfo().getItem(state).getType());
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&cClick to reset this quest");
			desc.add("");
			desc.addAll(quest.getDisplayInfo().getDescription(state));
			StringUtils.setDescription(item, desc,quest.getDisplayInfo().getHolders(getPlayer(), state));
		}
		@Override
		public ItemStack getItem() {
			return item;
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (click == ClickType.LEFT)
				QuestResetGui.this.getQuestPlayer().resetQuest(quest);
			update();
		}
	}

}
