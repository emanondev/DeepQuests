package emanondev.quests.newgui.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.Utils;

public class AdminPlayerManagerGui extends MapGui {
	private Player target;

	public AdminPlayerManagerGui(Player admin, Gui previusHolder,Player target) {
		super("Editor of Player "+target.getName(), 6, admin, previusHolder);
		this.target = target;
		putButton(53,new BackButton(this));
		putButton(8,new QuestResetButton(Quests.get().getQuestManager()));
	}
	
	private class QuestResetButton extends SelectOneElementButton<Quest> {
		
		public QuestResetButton(QuestManager qm) {
			super("&cSelect which quest reset", new ItemBuilder(Material.BOOK).setGuiProperty().build()
					, AdminPlayerManagerGui.this, qm.getQuests(), false,true,true);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&cClick to reset a Quest for "+target.getPlayer().getName());
			return desc;
		}

		@Override
		public List<String> getElementDescription(Quest element) {
			return element.getInfo();
		}

		@Override
		public ItemStack getElementItem(Quest element) {
			return new ItemBuilder(Material.BOOK).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(Quest element) {
			Quests.get().getPlayerManager().getQuestPlayer(target).resetQuest(element);
			getTargetPlayer().openInventory(getParent().getPreviusGui().getInventory());
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (!Utils.checkPermission(clicker,Perms.ADMIN_RESET_PLAYER_QUEST))
				return;
			super.onClick(clicker, click);
		}
	}
	

}
