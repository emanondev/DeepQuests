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
	private final static HashMap<String,RequireType> requires = 
			new HashMap<String,RequireType>();
	private final static HashMap<String,MissionRequireType> missionRequires =
			new HashMap<String,MissionRequireType>();
	private final static HashMap<String,QuestRequireType> questRequires =
			new HashMap<String,QuestRequireType>();
			

	public void registerRequireType(RequireType type) {
		requires.put(type.getNameID(),type);
		missionRequires.put(type.getNameID(),type);
		questRequires.put(type.getNameID(),type);
	}
	public void registerMissionRequireType(MissionRequireType type) {
		missionRequires.put(type.getNameID(),type);
	}
	public void registerQuestRequireType(QuestRequireType type) {
		questRequires.put(type.getNameID(),type);
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
				rews.add(requires.get(key.toUpperCase()).getRequireInstance(trueRequire));
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
				rews.add(requires.get(key.toUpperCase()).getRequireInstance(trueRequire));
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
				rews.add(requires.get(key.toUpperCase()).getRequireInstance(trueRequire));
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
