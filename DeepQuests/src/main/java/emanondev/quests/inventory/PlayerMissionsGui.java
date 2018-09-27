package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Language;
import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.DisplayState;

public class PlayerMissionsGui extends AbstractQuestPlayerGui {
	private final Quest quest;
	private boolean seeAllForced;

	public PlayerMissionsGui(QuestPlayer target, Quest quest, boolean backButton, boolean seeAllForced) {
		super(target, null);
		if (quest == null)
			throw new NullPointerException();
		this.quest = quest;
		this.seeAllForced = seeAllForced;
		this.setTitle(null, Language.Gui.getMissionsMenuTitle(getPlayer(), quest, getPage()));
		this.setFromEndBackButtonPosition(9);
		this.setFromEndNextPageButtonPosition(2);
		this.setFromEndPrevPageButtonPosition(3);
		for (int i = 0; i<DisplayState.values().length;i++) {
			toggler[i] = new PlayerMissionStateTogglerButton(DisplayState.values()[i]);
		}
		update();
	}
	private PlayerMissionStateTogglerButton[] toggler = new PlayerMissionStateTogglerButton[DisplayState.values().length];
	
	public class PlayerMissionStateTogglerButton extends CustomButton {
		private ItemStack item;
		private DisplayState state;

		public ItemStack getItem() {
			return item;
		}

		public PlayerMissionStateTogglerButton(DisplayState state) {
			super(PlayerMissionsGui.this);
			this.state = state;
			update();
		}

		public void update() {
			item = Language.Gui.getDisplayTogglerItem(getQuestPlayer().canSeeMissionState(state), state);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			getQuestPlayer().toggleCanSeeMissionState(state);
			this.getParent().update();
			this.getParent().reloadInventory();
		}
	}
	
	public void reloadInventory() {
		super.reloadInventory();
		getInventory().setItem(45, Language.Gui.getMissionsMenuBackItem(getPlayer(), quest, getPage()));
	}

	public void update() {
		this.items.clear();
		for (Mission mission : quest.getMissions()) {
			if (seeAllForced || getQuestPlayer().canSee(mission))
				this.items.add(new MissionButton(this, mission));
		}
		for (int i = 0; i< DisplayState.values().length; i++) {
			toggler[i].update();
			this.getInventory().setItem(getInventory().getSize()-9+i, toggler[i].getItem());
		}
		super.update();
	}

	@Override
	public void onSlotClick(Player clicker, int slot, ClickType click) {
		if (slot == 45) {
			clicker.openInventory(Quests.get().getGuiManager().getQuestsInventory(getPlayer(),
					quest.getParent(), this.seeAllForced));
			return;
		}
		super.onSlotClick(clicker, slot, click);
	}

	public class PlayerPrevPageButton extends PrevPageButton {
		private ItemStack item;

		public ItemStack getItem() {
			return item;

		}

		public PlayerPrevPageButton() {
			super(PlayerMissionsGui.this);
			
		}
		public void update() {
			if (quest != null)
				item = Language.Gui.getMissionsMenuPreviusPageItem(getPlayer(), quest, getPage());
		}
	}

	public class PlayerNextPageButton extends NextPageButton {
		private ItemStack item;

		public ItemStack getItem() {
			return item;
		}

		public PlayerNextPageButton() {
			super(PlayerMissionsGui.this);
		}
		public void update() {
			if (quest != null)
				item = Language.Gui.getMissionsMenuNextPageItem(getPlayer(), quest, getPage());
		}
	}

	/*
	public class PlayerCurrPageButton extends CustomButton {
		private ItemStack item;

		public ItemStack getItem() {
			return item;
		}

		public PlayerCurrPageButton() {
			super(PlayerMissionsGui.this);
		}

		public void update() {
			if (quest != null)
				item = Language.Gui.getMissionsMenuPageItem(getPlayer(), quest, getPage());
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent() == null)
				clicker.closeInventory();
		}
	}*/

	protected CustomButton craftPrevPageButton() {
		return new PlayerPrevPageButton();
	}

	protected CustomButton craftNextPageButton() {
		return new PlayerNextPageButton();
	}

	protected CustomButton craftCurrentPageButton() {
		return null;//new PlayerCurrPageButton();
	}
	protected CustomButton craftCloseButton() {
		return null;//new PlayerCurrPageButton();
	}
}
