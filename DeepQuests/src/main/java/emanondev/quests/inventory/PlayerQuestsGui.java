package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Language;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.DisplayState;

public class PlayerQuestsGui extends AbstractQuestPlayerGui {

	private final QuestManager questManager;
	private boolean forceSeeAll;

	public boolean isForcedShow() {
		return forceSeeAll;
	}
	
	private PlayerQuestStateTogglerButton[] toggler = new PlayerQuestStateTogglerButton[DisplayState.values().length];

	public PlayerQuestsGui(QuestPlayer target, QuestManager questManager, boolean forceSeeAll) {
		super(target, null);
		if (questManager == null)
			throw new NullPointerException();
		this.questManager = questManager;
		this.forceSeeAll = forceSeeAll;
		this.setTitle(null, Language.Gui.getQuestsMenuTitle(getPlayer(), getPage()));
		//this.setFromEndCloseButtonPosition(1);
		this.setFromEndBackButtonPosition(9);
		this.setFromEndNextPageButtonPosition(2);
		this.setFromEndPrevPageButtonPosition(3);
		//this.setFromEndCurrentPageButtonPosition(-1);
		for (int i = 0; i<DisplayState.values().length;i++) {
			toggler[i] = new PlayerQuestStateTogglerButton(DisplayState.values()[i]);
		}
		update();
	}
	
	public CustomButton craftCloseButton(){
		return null;
	}

	public void update() {
		this.items.clear();
		for (Quest quest : questManager.getQuests()) {
			if (forceSeeAll || getQuestPlayer().canSee(quest))
				this.items.add(new QuestButton(this, quest));
		}
		for (int i = 0; i< DisplayState.values().length; i++) {
			toggler[i].update();
			this.getInventory().setItem(getInventory().getSize()-9+i, toggler[i].getItem());
		}
		super.update();
	}
	
	@Override
	public void onSlotClick(Player clicker, int slot, ClickType click) {
		if (slot>= getInventory().getSize()-9 && slot< getInventory().getSize()-9+toggler.length) {
			toggler[slot-getInventory().getSize()+9].onClick(clicker, click);
		}
		super.onSlotClick(clicker,slot,click);
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
	public class PlayerQuestStateTogglerButton extends CustomButton {
		private ItemStack item;
		private DisplayState state;

		public ItemStack getItem() {
			return item;
		}

		public PlayerQuestStateTogglerButton(DisplayState state) {
			super(PlayerQuestsGui.this);
			this.state = state;
			update();
		}

		public void update() {
			item = Language.Gui.getDisplayTogglerItem(getQuestPlayer().canSeeQuestState(state), state);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			getQuestPlayer().toggleCanSeeQuestState(state);
			this.getParent().update();
			this.getParent().reloadInventory();
		}
	}
	
	/*
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
	}*/

	protected CustomButton craftPrevPageButton() {
		return new PlayerPrevPageButton();
	}

	protected CustomButton craftNextPageButton() {
		return new PlayerNextPageButton();
	}

	protected CustomButton craftCurrentPageButton() {
		return null;
		//return new PlayerCurrPageButton();
	}

}
