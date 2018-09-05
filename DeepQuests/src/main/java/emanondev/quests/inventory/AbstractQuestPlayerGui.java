package emanondev.quests.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Language;
import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.player.QuestPlayer;

abstract class AbstractQuestPlayerGui extends CustomMultiPageGui<CustomButton> {

	public AbstractQuestPlayerGui(QuestPlayer target, CustomGui previusHolder) {
		super(target.getPlayer(), previusHolder, 6, 1);
	}

	public QuestPlayer getQuestPlayer() {
		return Quests.getInstance().getPlayerManager().getQuestPlayer(getPlayer());
	}

	protected CustomButton craftCloseButton() {
		return new CloseButton();
	}

	private class CloseButton extends CustomButton {
		private ItemStack item;

		public ItemStack getItem() {
			return item;
		}

		public CloseButton() {
			super(AbstractQuestPlayerGui.this);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.closeInventory();
			update();
		}

		public void update() {
			item = Language.Gui.getMissionsMenuCloseItem(getPlayer(), getPage());
		}
	}
}