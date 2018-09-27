package emanondev.quests.reward.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.TargetMissionData;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.task.Task;
import emanondev.quests.utils.NoLoopQC;
import emanondev.quests.utils.QuestComponent;

public class FailMissionRewardType extends AbstractRewardType implements RewardType {
	public FailMissionRewardType() {
		super("FAILMISSION");
	}

	public class FailMissionReward extends AbstractReward implements Reward,NoLoopQC {
		private final TargetMissionData missionData;
		public FailMissionReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			if (!((parent instanceof Quest) || (parent instanceof Mission) || (parent instanceof Task)))
				throw new IllegalArgumentException();
			missionData = new TargetMissionData(getSection(),this);
		}

		public String getInfo() {
			Mission m = missionData.getTargetMission();
			if (m == null)
				return "Quest ??? Mission ???";
			return "Quest " + m.getParent().getDisplayName() + " Mission "
				+ m.getDisplayName();
		}

		@Override
		public RewardType getType() {
			return FailMissionRewardType.this;
		}

		@Override
		public void applyReward(QuestPlayer qPlayer,int amount) {
			if (amount<=0)
				return;
			try {
				Mission m = missionData.getTargetMission();
				qPlayer.failMission(m);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, missionData.getMissionSelectorButton(gui));
			return gui;
		}
	}

	@Override
	public Reward getInstance(ConfigSection m, QuestComponent loadable) {
		return new FailMissionReward(m,loadable);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.BARRIER;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Force the player to fail selected mission", "&7of selected quest");
	}

}
