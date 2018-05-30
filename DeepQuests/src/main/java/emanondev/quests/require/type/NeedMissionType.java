package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.require.MissionRequireType;

public class NeedMissionType extends AbstractRequireType implements MissionRequireType {
	private final static String ID = "MISSIONCOMPLETED";
	private final static String PATH_TARGET_MISSION_ID = "target-mission";
	
	public NeedMissionType() {
		super(ID);
	}
	@Override
	public MissionRequire getRequireInstance(MemorySection section, Mission mission) {
		return new NeedMission(section,mission);
	}
	public class NeedMission extends AbstractRequire implements MissionRequire {
		private final String targetMissionID;
		
		public NeedMission(MemorySection section, Mission mission) {
			super(section,mission);
			targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID);
		}
		public Mission getParent() {
			return (Mission) super.getParent();
		}

		@Override
		public boolean isAllowed(QuestPlayer p) {//TODO avoid loops
			Mission target = getParent().getParent().getMissionByNameID(targetMissionID);
			if (target==null) {
				Quests.getLogger("errors").log("quest "+getParent().getParent().getNameID()
						+" -> mission "+getParent().getNameID()+" -> require unexistent mission "
						+targetMissionID);
				return true;
			}
			switch (p.getDisplayState(target)) {
			case COMPLETED:
			case COOLDOWN:
				return true;
			default:
				break;
			}
			return false;
		}
		@Override
		public MissionRequireType getRequireType() {
			return NeedMissionType.this;
		}
		public String getKey() {
			return getRequireType().getKey();
		}
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_FENCE;
	}
	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has completed the selected mission");
		return desc;
	}

}
