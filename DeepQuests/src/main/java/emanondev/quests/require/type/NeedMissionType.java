package emanondev.quests.require.type;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.require.MissionRequireType;

public class NeedMissionType extends MissionRequireType {

	public NeedMissionType() {
		super("MISSION", NeedMission.class);
	}
	
	public class NeedMission implements MissionRequire {
		private final String targetMission;
		
		public NeedMission(String missionId) {
			targetMission = missionId;
		}

		@Override
		public boolean isAllowed(QuestPlayer p, Mission m) {//TODO avoid loops
			Mission target = m.getParent().getMissionByNameID(targetMission);
			if (target==null) {
				Quests.getLogger("errors").log("quest "+m.getParent().getNameID()
						+" -> mission "+m.getNameID()+" -> require unexistent mission "
						+targetMission);
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
		
	}

}
