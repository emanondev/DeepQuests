package emanondev.quests.gui;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.mission.Mission;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.utils.StringUtils;

public class MissionCompleteRewardExplorerFactory implements EditorButtonFactory {
	private final Mission mission;
	private final String title;

	public MissionCompleteRewardExplorerFactory(Mission mission, String title) {
		this.mission = mission;
		this.title = title;
	}

	private class MissionRewardExplorerButton extends CustomButton {
		private ItemStack item = new ItemStack(Material.GOLD_INGOT);

		public MissionRewardExplorerButton(CustomGui parent) {
			super(parent);
			update();
		}
		public void update() {
			item.setAmount(Math.max(1, Math.min(127, mission.getCompleteRewards().size())));
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSelect/Show rewards");
			desc.add("&6Click to Select a reward to edit");
			if (mission.getCompleteRewards().size()>0)
				for (MissionReward reward: mission.getCompleteRewards())
					desc.add("&7"+reward.getInfo());
			StringUtils.setDescription(item, desc);
		}

		@Override
		public ItemStack getItem() {
			if (mission.getCompleteRewards().isEmpty())
				return null;
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (mission.getCompleteRewards().isEmpty())
				return;
			clicker.openInventory(new RewardExplorer(clicker, this.getParent()).getInventory());
		}

		private class RewardExplorer extends CustomMultiPageGui<CustomButton> {

			public RewardExplorer(Player p, CustomGui parent) {
				super(p, parent, 6, 1);
				this.setFromEndCloseButtonPosition(8);
				for (MissionReward reward : mission.getCompleteRewards())
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
