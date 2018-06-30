package emanondev.quests.require;

import emanondev.quests.mission.Mission;

public interface MissionRequire extends Require {

	/**
	 * 
	 * @return the object that has this applied
	 */
	@Override
	public Mission getParent();
	/**
	 * 
	 * @return the Type
	 */
	@Override
	public MissionRequireType getType();
}
