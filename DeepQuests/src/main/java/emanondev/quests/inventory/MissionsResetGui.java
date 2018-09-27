package emanondev.quests.inventory;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class MissionsResetGui extends AbstractQuestPlayerGui {
	private QuestManager questManager;
	public MissionsResetGui(Player target, CustomGui previusHolder,QuestManager questManager) {
		super(Quests.get().getPlayerManager().getQuestPlayer(target), previusHolder);
		this.questManager = questManager;
		this.setTitle(null, ChatColor.RED+"Click to select Quest for "+target.getPlayer().getName());
		update();
	}
	public void update() {
		this.items.clear();
		for (Quest quest:questManager.getQuests()) {
			this.items.add(new QuestSelectButton(quest));
		}
		
		super.update();
	}
	
	private class QuestSelectButton extends CustomButton {
		private final Quest quest;
		private ItemStack item;
		public QuestSelectButton(Quest quest) {
			super(MissionsResetGui.this);
			this.quest = quest;
			item = new ItemStack(Material.BOOK);
			update();
		}
		public void update() {
			DisplayState state = getQuestPlayer().getDisplayState(quest);
			this.item.setType(quest.getDisplayInfo().getItem(state).getType());
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Click to select this quest");
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
				clicker.openInventory(new MissionResetGui().getInventory());
		}
		private class MissionResetGui extends AbstractQuestPlayerGui {

			public MissionResetGui() {
				super(MissionsResetGui.this.getQuestPlayer(), MissionsResetGui.this);
				this.setTitle(null, ChatColor.RED+"Click to reset Mission for "+getQuestPlayer().getPlayer().getName());
				update();
			}
			public void update() {
				this.items.clear();
				for (Mission mission : quest.getMissions()) {
					this.items.add(new MissionResetButton(mission));
				}
				
				super.update();
			}
			
			private class MissionResetButton extends CustomButton {
				private final Mission mission;
				private ItemStack item;
				public MissionResetButton(Mission mission) {
					super(MissionResetGui.this);
					this.mission = mission;
					this.item = new ItemStack(Material.PAPER);
				}
				public void update() {
					DisplayState state = getQuestPlayer().getDisplayState(mission);
					this.item.setType(mission.getDisplayInfo().getItem(state).getType());
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&cClick to reset this mission");
					desc.add("");
					desc.addAll(mission.getDisplayInfo().getDescription(state));
					StringUtils.setDescription(item, desc,mission.getDisplayInfo().getHolders(getPlayer(), state));
				}
				@Override
				public ItemStack getItem() {
					return item;
				}
				@Override
				public void onClick(Player clicker, ClickType click) {
					if (click == ClickType.LEFT)
						getQuestPlayer().resetMission(mission);
					update();
				}
				
			}
			
		}
	}
}
