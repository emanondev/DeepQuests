package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.IntegerPositiveAmountData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.QuestComponent;

public class QuestPointRewardType extends AbstractRewardType {
	private final static String KEY = "QUEST_POINT_REWARD";

	public QuestPointRewardType() {
		super(KEY);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.DIAMOND_BLOCK;
	}

	@Override
	public Reward getInstance(ConfigSection section, QuestComponent parent) {
		return new QuestPointReward(section, parent);
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Give Quest Points for Player");
		return desc;
	}
	
	public class QuestPointReward extends AbstractReward {
		private final IntegerPositiveAmountData quantity;

		public QuestPointReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			this.quantity = new IntegerPositiveAmountData(getSection(), this);
		}

		@Override
		public RewardType getType() {
			return QuestPointRewardType.this;
		}

		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			qPlayer.setQuestPoints(qPlayer.getQuestPoints()+quantity.getAmount()*amount);
		}
		
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Quest Points: &e"+quantity.getAmount());
			return info;
		}

		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(0, quantity.getAmountEditorButton(gui));
			return gui;
		}
	}
}
