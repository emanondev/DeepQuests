package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Language;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;

public class PlayerQuestsGui extends AbstractQuestPlayerGui {

	private final QuestManager questManager;
	private boolean forceSeeAll;

	public boolean isForcedShow() {
		return forceSeeAll;
	}

	public PlayerQuestsGui(QuestPlayer target, QuestManager questManager, boolean forceSeeAll) {
		super(target, null);
		if (questManager == null)
			throw new NullPointerException();
		this.questManager = questManager;
		this.forceSeeAll = forceSeeAll;
		this.setTitle(null, Language.Gui.getQuestsMenuTitle(getPlayer(), getPage()));
		update();
	}

	public void update() {
		this.items.clear();
		for (Quest quest : questManager.getQuests()) {
			if (forceSeeAll || getQuestPlayer().canSee(quest))
				this.items.add(new QuestButton(this, quest));
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
			item = Language.Gui.getQuestsMenuPreviusPageItem(getPlayer(), getPage());
		}
	}

	public class PlayerNextPageButton extends NextPageButton {
		private ItemStack item;

		public ItemStack getItem() {
			return item;
		}

		public PlayerNextPageButton() {
			super(PlayerQuestsGui.this);
			item = Language.Gui.getQuestsMenuNextPageItem(getPlayer(), getPage());
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
			item = Language.Gui.getQuestsMenuPageItem(getPlayer(), getPage());
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (getParent() == null)
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
