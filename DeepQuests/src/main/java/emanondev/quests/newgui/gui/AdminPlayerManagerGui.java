package emanondev.quests.newgui.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
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
		putButton(17,new MissionResetButton(Quests.get().getQuestManager()));
		putButton(9,new MissionCompleteButton(Quests.get().getQuestManager()));
		//putButton(10,new MissionCompleteNoRewardButton(Quests.get().getQuestManager()));
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
	
	private class MissionResetButton extends SelectOneElementButton<Quest> {
		
		public MissionResetButton(QuestManager qm) {
			super("&cSelect which quest", new ItemBuilder(Material.BOOK).setGuiProperty().build()
					, AdminPlayerManagerGui.this, qm.getQuests(), false,true,true);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&cClick to reset a Mission for "+target.getPlayer().getName());
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
			new MissionSelectButton(element).onClick(getTargetPlayer(), ClickType.LEFT);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (!Utils.checkPermission(clicker,Perms.ADMIN_RESET_PLAYER_QUEST))
				return;
			super.onClick(clicker, click);
		}
		
		private class MissionSelectButton extends SelectOneElementButton<Mission> {
			
			public MissionSelectButton(Quest quest) {
				super("&cSelect which mission", new ItemBuilder(Material.BOOK).setGuiProperty().build()
						, AdminPlayerManagerGui.this, quest.getMissions(), false,true,true);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&cClick to select Quest for "+target.getPlayer().getName());
				return desc;
			}

			@Override
			public List<String> getElementDescription(Mission element) {
				return element.getInfo();
			}

			@Override
			public ItemStack getElementItem(Mission element) {
				return new ItemBuilder(Material.BOOK).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Mission element) {
				Quests.get().getPlayerManager().getQuestPlayer(target).resetMission(element);
				getTargetPlayer().openInventory(getParent().getPreviusGui().getInventory());
			}
		}
	}
	
	private class MissionCompleteButton extends SelectOneElementButton<Quest> {
		
		public MissionCompleteButton(QuestManager qm) {
			super("&cSelect which quest", new ItemBuilder(Material.BOOK).setGuiProperty().build()
					, AdminPlayerManagerGui.this, qm.getQuests(), false,true,false);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&aClick to complete a Mission &e(with rewards)&a for "+target.getPlayer().getName());
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
			new MissionSelectButton(element).onClick(getTargetPlayer(), ClickType.LEFT);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (!Utils.checkPermission(clicker,Perms.ADMIN_RESET_PLAYER_QUEST))
				return;
			super.onClick(clicker, click);
		}
		
		private class MissionSelectButton extends SelectOneElementButton<Mission> {
			
			public MissionSelectButton(Quest quest) {
				super("&cSelect which mission", new ItemBuilder(Material.BOOK).setGuiProperty().build()
						, AdminPlayerManagerGui.this, quest.getMissions(), false,true,true);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&cClick to select Quest for "+target.getPlayer().getName());
				return desc;
			}

			@Override
			public List<String> getElementDescription(Mission element) {
				return element.getInfo();
			}

			@Override
			public ItemStack getElementItem(Mission element) {
				return new ItemBuilder(Material.BOOK).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Mission element) {
				Quests.get().getPlayerManager().getQuestPlayer(target).completeMission(element);
				getTargetPlayer().openInventory(getParent().getPreviusGui().getInventory());
			}
		}
	}
	
/*
	
	private class MissionCompleteNoRewardsButton extends SelectOneElementButton<Quest> {
		
		public MissionCompleteNoRewardsButton(QuestManager qm) {
			super("&cSelect which quest", new ItemBuilder(Material.BOOK).setGuiProperty().build()
					, AdminPlayerManagerGui.this, qm.getQuests(), false,true,true);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&aClick to complete a Mission &c(no rewards)&a for "+target.getPlayer().getName());
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
			new MissionSelectButton(element).onClick(getTargetPlayer(), ClickType.LEFT);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (!Utils.checkPermission(clicker,Perms.ADMIN_RESET_PLAYER_QUEST))
				return;
			super.onClick(clicker, click);
		}
		
		private class MissionSelectButton extends SelectOneElementButton<Mission> {
			
			public MissionSelectButton(Quest quest) {
				super("&cSelect which mission", new ItemBuilder(Material.BOOK).setGuiProperty().build()
						, AdminPlayerManagerGui.this, quest.getMissions(), false,true,true);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&cClick to select Quest for "+target.getPlayer().getName());
				return desc;
			}

			@Override
			public List<String> getElementDescription(Mission element) {
				return element.getInfo();
			}

			@Override
			public ItemStack getElementItem(Mission element) {
				return new ItemBuilder(Material.BOOK).setGuiProperty().build();
			}

			@Override
			public void onElementSelectRequest(Mission element) {
				Quests.get().getPlayerManager().getQuestPlayer(target).resetMission(element);
				getTargetPlayer().openInventory(getParent().getPreviusGui().getInventory());
			}
		}
	}*/
}
