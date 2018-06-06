package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public class PlayerMissionsGui extends AbstractQuestPlayerGui{
	private final Quest quest;
	private boolean seeAllForced;

	public PlayerMissionsGui(QuestPlayer target, Quest quest,boolean backButton,boolean seeAllForced) {
		super(target, null);
		if (quest==null)
			throw new NullPointerException();
		this.quest = quest;
		this.seeAllForced = seeAllForced;
		this.setTitle(null, Quests.getInstance().getConfigManager()
				.getMissionsMenuTitle(getPlayer(), quest));
		update();
	}
	public void reloadInventory() {
		super.reloadInventory();
		getInventory().setItem(45, Quests.getInstance().getConfigManager()
				.getMissionsMenuBackItem(getPlayer()));
	}
	public void update() {
		this.items.clear();
		for (Mission mission:quest.getMissions()) {
			if (seeAllForced || getQuestPlayer().canSee(mission))
				this.items.add(new MissionButton(this,mission,seeAllForced));
		}
		super.update();
	}
	@Override
	public void onSlotClick(Player clicker,int slot,ClickType click) {
		if (slot == 45) {
			clicker.openInventory(Quests.getInstance().getGuiManager().getQuestsInventory(getPlayer(), quest.getParent(), this.seeAllForced));
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
			item = Quests.getInstance().getConfigManager()
					.getMissionsMenuPreviusPageItem(getPlayer());
		}
	}
	public class PlayerNextPageButton extends NextPageButton {
		private ItemStack item;
		public ItemStack getItem() {
			return item;
		}
		public PlayerNextPageButton() {
			super(PlayerMissionsGui.this);
			item = Quests.getInstance().getConfigManager()
					.getMissionsMenuNextPageItem(getPlayer());
		}
	}
	public class PlayerCurrPageButton extends CustomButton {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public PlayerCurrPageButton() {
			super(PlayerMissionsGui.this);
			update();
		}
		public void update() {
			item = Quests.getInstance().getConfigManager()
					.getMissionsMenuPageItem(getPlayer(),getPage(),quest);
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent()==null)
				clicker.closeInventory();
		}
	}

	protected CustomButton craftPrevPageButton() {
		return new PlayerPrevPageButton();
	}
	protected CustomButton craftNextPageButton() {
		return new PlayerNextPageButton();
	}
	protected CustomButton craftCurrentPageButton() {
		return new PlayerCurrPageButton();
	}
}
