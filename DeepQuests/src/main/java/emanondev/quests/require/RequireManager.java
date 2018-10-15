package emanondev.quests.require;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.QCWithCooldown;

public class RequireManager {
	private final static HashMap<String, RequireType> requiresType = new HashMap<String, RequireType>();
	private final static HashMap<String, RequireType> missionRequiresType = new HashMap<String, RequireType>();
	private final static HashMap<String, RequireType> questRequiresType = new HashMap<String, RequireType>();

	public void registerRequireType(RequireType type) {
		requiresType.put(type.getKey(), type);
		missionRequiresType.put(type.getKey(), type);
		questRequiresType.put(type.getKey(), type);
		Quests.get().consoleLog("Registered Require Type "+type.getKey());
	}

	public void registerMissionRequireType(MissionRequireType type) {
		missionRequiresType.put(type.getKey(), type);
		Quests.get().consoleLog("Registered Mission Require Type "+type.getKey());
	}

	public void registerQuestRequireType(QuestRequireType type) {
		questRequiresType.put(type.getKey(), type);
		Quests.get().consoleLog("Registered Quest Require Type "+type.getKey());
	}

	public LinkedHashMap<String, Require> loadRequires(Mission m, ConfigSection section) {
		LinkedHashMap<String, Require> requires = new LinkedHashMap<String, Require>();
		if (section != null) {
			Set<String> keys = section.getKeys(false);
			if (keys != null)
				keys.forEach((id) -> {
					try {
						String key = section.getString(id + ".type");
						if (key == null || !missionRequiresType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Require rew = missionRequiresType.get(key.toUpperCase())
								.getInstance(section.loadSection(id), m);
						if (rew != null)
							requires.put(rew.getID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return requires;
	}

	public LinkedHashMap<String, Require> loadRequires(Quest q, ConfigSection section) {
		LinkedHashMap<String, Require> requires = new LinkedHashMap<String, Require>();
		if (section != null) {
			Set<String> keys = section.getKeys(false);
			if (keys != null)
				keys.forEach((id) -> {
					try {
						String key = section.getString(id + ".type");
						if (key == null || !questRequiresType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Require rew = questRequiresType.get(key.toUpperCase())
								.getInstance(section.loadSection(id), q);
						if (rew != null)
							requires.put(rew.getID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return requires;
	}

	public LinkedHashMap<String, Require> loadRequires(QCWithCooldown loadable, ConfigSection section) {
		LinkedHashMap<String, Require> requires = new LinkedHashMap<String, Require>();
		if (section != null) {
			Set<String> keys = section.getKeys(false);
			if (keys != null)
				keys.forEach((id) -> {
					try {
						String key = section.getString(id + ".type");
						if (key == null || !requiresType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Require rew = requiresType.get(key.toUpperCase())
								.getInstance(section.loadSection(id), loadable);
						if (rew != null)
							requires.put(rew.getID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return requires;
	}

	public boolean isAllowed(QuestPlayer p, List<Require> list) {
		if (list == null || list.isEmpty())
			return true;
		for (Require req : list) {
			if (!req.isAllowed(p))
				return false;
		}
		return true;
	}

	public Collection<RequireType> getQuestRequiresTypes() {
		return Collections.unmodifiableCollection(questRequiresType.values());
	}

	public Collection<RequireType> getMissionRequiresTypes() {
		return Collections.unmodifiableCollection(missionRequiresType.values());
	}

	public Collection<RequireType> getSafeQuestRequiresTypes() {
		HashSet<RequireType> set = new HashSet<RequireType>();
		for (RequireType type:questRequiresType.values()) {
			if (type.getClass().getAnnotations().length>0)
				if (type.getClass().getAnnotation(Deprecated.class)!=null)
					continue;
			set.add(type);
		}
		return set;
	}

	public Collection<RequireType> getSafeMissionRequiresTypes() {
		HashSet<RequireType> set = new HashSet<RequireType>();
		for (RequireType type:missionRequiresType.values()) {
			if (type.getClass().getAnnotations().length>0)
				if (type.getClass().getAnnotation(Deprecated.class)!=null)
					continue;
			set.add(type);
		}
		return set;
	}
}
