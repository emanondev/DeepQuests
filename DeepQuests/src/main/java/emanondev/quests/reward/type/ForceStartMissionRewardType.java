package emanondev.quests.reward.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.reward.MissionRewardType;
import emanondev.quests.utils.QuestComponent;

@Deprecated
public class ForceStartMissionRewardType extends AbstractRewardType implements MissionRewardType {

	private final static String PATH_TARGET_MISSION_ID = "target-mission";
	public ForceStartMissionRewardType() {
		super("FORCESTARTMISSION");
	}
	public class ForceStartMissionReward extends AbstractReward implements MissionReward {
		private String targetMissionID;
		public ForceStartMissionReward(ConfigSection section, Mission parent) {
			super(section, parent);
			this.targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID,null);
			//this.addToEditor(9,new ForceStartMissionRewardEditorButtonFactory());
		}
		public String getInfo() {
			if (targetMissionID==null || getParent().getParent().getMissionByID(targetMissionID)==null)
				return "Mission ("+targetMissionID+")";
			return "Mission "+getParent().getParent().getMissionByID(targetMissionID).getDisplayName()+"("+targetMissionID+")";
		}
		public Mission getParent() {
			return (Mission) super.getParent();
		}
		@Override
		public void applyReward(QuestPlayer p,int amount) {
			if (amount<=0)
				return;
			if (targetMissionID==null)
				return;
			Mission target = getParent().getParent().getMissionByID(targetMissionID);
			if (target==null)
				return;
			p.startMission(target,true);
		}
		@Override
		public MissionRewardType getType() {
			return ForceStartMissionRewardType.this;
		}
		public boolean setTargetMission(Mission mission) {
			if (mission == null) {
				targetMissionID = null;
			} else
				targetMissionID = mission.getID();

			getSection().set(PATH_TARGET_MISSION_ID, targetMissionID);
			getParent().setDirty(true);

			return true;
		}

	}
	@Override
	public MissionReward getInstance(ConfigSection m, QuestComponent mission) {
		if(!(mission instanceof Mission))
			throw new IllegalArgumentException();
		return new ForceStartMissionReward(m,(Mission) mission);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.ARROW;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Force the player to start selected mission");
	}

}
