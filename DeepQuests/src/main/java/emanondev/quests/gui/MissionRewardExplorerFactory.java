package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.reward.MissionReward;
import emanondev.quests.utils.StringUtils;

public class MissionRewardExplorerFactory implements EditorButtonFactory {
	private final Collection<MissionReward> rewards;
	private final String title;

	public MissionRewardExplorerFactory(Collection<MissionReward> rewards, String title) {
		this.rewards = rewards;
		this.title = title;
	}

	private class MissionRewardExplorerButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.GOLD_INGOT);

		public MissionRewardExplorerButton(CustomGui parent) {
			super(parent);
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSelect/Show rewards");
			desc.add("&6Click to Select a reward to edit");
			StringUtils.setDescription(item, desc);
			update();
		}
		public void update() {
			item.setAmount(Math.max(1, Math.min(127, rewards.size())));
		}

		@Override
		public ItemStack getItem() {
			if (rewards.isEmpty())
				return null;
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (rewards.isEmpty())
				return;
			clicker.openInventory(new RewardExplorer(clicker, this.getParent()).getInventory());
		}

		private class RewardExplorer extends CustomMultiPageGui<CustomButton> {

			public RewardExplorer(Player p, CustomGui parent) {
				super(p, parent, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (MissionReward reward : rewards)
					addButton(new RewardButton(reward));
				this.setTitle(null, StringUtils.fixColorsAndHolders(title));
				reloadInventory();
			}

			private class RewardButton extends CustomButton {
				private final MissionReward rew;
				private final ItemStack item;

				public RewardButton(MissionReward reward) {
					super(RewardExplorer.this);
					this.rew = reward;
					this.item = new ItemStack(rew.getRewardType().getGuiItemMaterial());
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6"+rew.getInfo());
					StringUtils.setDescription(item, desc);
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					rew.openEditorGui(clicker, MissionRewardExplorerButton.this.getParent());
				}
			}
		}
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new MissionRewardExplorerButton(parent);
	}

}
