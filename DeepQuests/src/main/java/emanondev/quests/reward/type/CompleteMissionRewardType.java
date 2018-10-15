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

public class CompleteMissionRewardType extends AbstractRewardType implements RewardType {
	public CompleteMissionRewardType() {
		super("COMPLETEMISSION");
	}

	public class CompleteMissionReward extends AbstractReward implements Reward,NoLoopQC {
		private final TargetMissionData missionData;
		public CompleteMissionReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			if (!((parent instanceof Quest) || (parent instanceof Mission) || (parent instanceof Task)))
				throw new IllegalArgumentException();
			missionData = new TargetMissionData(getSection(),this);
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			Mission m = missionData.getTargetMission();
			if (m == null)
				info.add("&9Complete Mission: &cnot setted");
			else {
				info.add("&9Complete Mission: &e" + m.getDisplayName());
				info.add("  &9of Quest: &e"+m.getParent().getDisplayName());
			}
			return info;
		}

		@Override
		public RewardType getType() {
			return CompleteMissionRewardType.this;
		}

		@Override
		public void applyReward(QuestPlayer qPlayer,int amount) {
			if (amount<=0)
				return;
			try {
				Mission m = missionData.getTargetMission();
				qPlayer.completeMission(m);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, missionData.getMissionSelectorButton(gui));
			return gui;
		}
		public boolean isLoopSafe(Mission mission) {
			if (mission==null)
				return true;
			if (mission.equals(getParent()))
				return false;
			
			for (Reward rew : mission.getCompleteRewards()) {
				if (!(rew instanceof CompleteMissionReward))
					continue;
				CompleteMissionReward reward = (CompleteMissionReward) rew;
				
				Mission targetMission = reward.missionData.getTargetMission();
				if (getParent().equals(targetMission))
					return false;
				if (!isLoopSafe(targetMission))
					return false;
			}
			return true;
		}
		
	}

	@Override
	public Reward getInstance(ConfigSection m, QuestComponent loadable) {
		return new CompleteMissionReward(m,loadable);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.EMERALD_BLOCK;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Force the player to complete selected mission", "&7of selected quest");
	}

}