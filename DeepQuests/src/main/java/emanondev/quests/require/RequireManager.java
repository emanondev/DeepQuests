package emanondev.quests.require;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public class RequireManager {
	private final static HashMap<String,Class<? extends Require>> requires = 
			new HashMap<String,Class<? extends Require>>();
	private final static HashMap<String,Class<? extends MissionRequire>> missionRequires =
			new HashMap<String,Class<? extends MissionRequire>>();
	private final static HashMap<String,Class<? extends QuestRequire>> questRequires =
			new HashMap<String,Class<? extends QuestRequire>>();
			

	public void registerRequireType(RequireType type) {
		requires.put(type.getNameID(),type.getRequireClass());
		missionRequires.put(type.getNameID(),type.getRequireClass());
		questRequires.put(type.getNameID(),type.getRequireClass());
	}
	public void registerMissionRequireType(MissionRequireType type) {
		missionRequires.put(type.getNameID(),type.getRequireClass());
	}
	public void registerQuestRequireType(QuestRequireType type) {
		questRequires.put(type.getNameID(),type.getRequireClass());
	}

	public List<Require> convertRequires(List<String> list) {
		ArrayList<Require> rews = new ArrayList<Require>();
		if (list==null||list.isEmpty())
			return rews;
		for (String rawRequire : list) {
			try {
				int index = rawRequire.indexOf(":");
				String key;
				String trueRequire;
				if (index == -1) {
					key = rawRequire;
					trueRequire = "";
				}
				else {
					key = rawRequire.substring(0,index);
					trueRequire = rawRequire.substring(index+1);
				}
				rews.add(requires.get(key.toUpperCase()).getConstructor(String.class)
						.newInstance(trueRequire));
			} catch (Exception e) {
				Quests.getLogger("errors").log("Error while creating require: '"+rawRequire+"'");
				Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
			}
			
		}
		return rews;
	}

	public List<MissionRequire> convertMissionRequires(List<String> list) {
		ArrayList<MissionRequire> rews = new ArrayList<MissionRequire>();
		if (list==null||list.isEmpty())
			return rews;
		for (String rawRequire : list) {
			try {
				int index = rawRequire.indexOf(":");
				String key;
				String trueRequire;
				if (index == -1) {
					key = rawRequire;
					trueRequire = "";
				}
				else {
					key = rawRequire.substring(0,index);
					trueRequire = rawRequire.substring(index+1);
				}
				rews.add(missionRequires.get(key.toUpperCase()).getConstructor(String.class)
						.newInstance(trueRequire));
			} catch (Exception e) {
				Quests.getLogger("errors").log("Error while creating require: '"+rawRequire+"'");
				Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
			}
			
		}
		return rews;
	}

	public List<QuestRequire> convertQuestRequires(List<String> list) {
		ArrayList<QuestRequire> rews = new ArrayList<QuestRequire>();
		if (list==null||list.isEmpty())
			return rews;
		for (String rawRequire : list) {
			try {
				int index = rawRequire.indexOf(":");
				String key;
				String trueRequire;
				if (index == -1) {
					key = rawRequire;
					trueRequire = "";
				}
				else {
					key = rawRequire.substring(0,index);
					trueRequire = rawRequire.substring(index+1);
				}
				rews.add(questRequires.get(key.toUpperCase()).getConstructor(String.class)
						.newInstance(trueRequire));
			} catch (Exception e) {
				Quests.getLogger("errors").log("Error while creating require: '"+rawRequire+"'");
				Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
			}
		}
		return rews;
	}
	public boolean isAllowed(QuestPlayer p,List<Require> list) {
		if (list == null || list.isEmpty())
			return true;
		for (Require req : list) {
			if (!req.isAllowed(p))
				return false;
		}
		return true;
	}
	public boolean isAllowed(QuestPlayer p,List<MissionRequire> list,Mission m) {
		if (list == null || list.isEmpty())
			return true;
		for (MissionRequire req : list) {
			if (!req.isAllowed(p,m))
				return false;
		}
		return true;
	}
	public boolean isAllowed(QuestPlayer p,List<QuestRequire> list,Quest q) {
		if (list == null || list.isEmpty())
			return true;
		for (QuestRequire req : list) {
			if (!req.isAllowed(p,q))
				return false;
		}
		return true;
	}

}
