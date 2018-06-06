package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;

public class PlayerQuestsGui extends AbstractQuestPlayerGui {
	
	private final QuestManager questManager;
	private boolean forceSeeAll;
	public PlayerQuestsGui(QuestPlayer target, QuestManager questManager,boolean forceSeeAll) {
		super(target, null);
		if (questManager==null)
			throw new NullPointerException();
		this.questManager = questManager;
		this.forceSeeAll = forceSeeAll;
		this.setTitle(null, Quests.getInstance().getConfigManager()
				.getQuestsMenuTitle(getPlayer()));
		update();
	}
	public void update() {
		this.items.clear();
		for (Quest quest:questManager.getQuests()) {
			this.items.add(new QuestButton(this,quest,forceSeeAll));
		}
		super.update();
	}
	
	public class PlayerPrevPageButton extends PrevPageButton {
		private ItemStack item;
		public ItemStack getItem() {
			return item;
			
		}
		public PlayerPrevPageButton() {
			super(PlayerQuestsGui.this);
			item = Quests.getInstance().getConfigManager()
					.getQuestsMenuPreviusPageItem(getPlayer());
		}
	}
	public class PlayerNextPageButton extends NextPageButton {
		private ItemStack item;
		public ItemStack getItem() {
			return item;
		}
		public PlayerNextPageButton() {
			super(PlayerQuestsGui.this);
			item = Quests.getInstance().getConfigManager()
					.getQuestsMenuNextPageItem(getPlayer());
		}
	}
	public class PlayerCurrPageButton extends CustomButton {
		private ItemStack item;
		
		public ItemStack getItem() {
			return item;
		}
		public PlayerCurrPageButton() {
			super(PlayerQuestsGui.this);
			update();
		}
		public void update() {
			item = Quests.getInstance().getConfigManager()
					.getQuestsMenuPageItem(getPlayer(),getPage());
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
