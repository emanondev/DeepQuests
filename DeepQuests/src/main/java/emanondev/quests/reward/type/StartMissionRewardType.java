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
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.reward.MissionRewardType;
import emanondev.quests.utils.NoLoopQC;
import emanondev.quests.utils.QuestComponent;

public class StartMissionRewardType  extends AbstractRewardType implements MissionRewardType {

	public StartMissionRewardType() {
		super("STARTMISSION");
	}

	public class ForceStartAnotherQuestMissionReward extends AbstractReward implements NoLoopQC,MissionReward {
		private TargetMissionData missionData;
		
		public ForceStartAnotherQuestMissionReward(ConfigSection section, Mission parent) {
			super(section, parent);
			missionData = new TargetMissionData(getSection(),this);
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			Mission m = missionData.getTargetMission();
			if (m == null)
				info.add("&9Start Mission: &cnot setted");
			else {
				info.add("&9Start Mission: &e" + m.getDisplayName());
				info.add("  &9of Quest: &e"+m.getParent().getDisplayName());
			}
			return info;
		}

		public Mission getParent() {
			return (Mission) super.getParent();
		}

		@Override
		public MissionRewardType getType() {
			return StartMissionRewardType.this;
		}

		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			if (amount<=0)
				return;
			Mission m = missionData.getTargetMission();
			if (m == null)
				return;
			qPlayer.startMission(m, true);
		}
		
		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, missionData.getMissionSelectorButton(gui));
			return gui;
		}
		
	}

	@Override
	public MissionReward getInstance(ConfigSection m, QuestComponent mission) {
		if(!(mission instanceof Mission))
			throw new IllegalArgumentException();
		return new ForceStartAnotherQuestMissionReward(m,(Mission) mission);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.ARROW;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Force the player to start selected mission", "&7of selected quest");
	}

}
