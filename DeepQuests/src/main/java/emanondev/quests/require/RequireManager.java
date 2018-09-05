package emanondev.quests.require;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public class RequireManager {
	private final static HashMap<String, RequireType> requiresType = new HashMap<String, RequireType>();
	private final static HashMap<String, RequireType> missionRequiresType = new HashMap<String, RequireType>();
	private final static HashMap<String, RequireType> questRequiresType = new HashMap<String, RequireType>();

	public void registerRequireType(RequireType type) {
		requiresType.put(type.getKey(), type);
		missionRequiresType.put(type.getKey(), type);
		questRequiresType.put(type.getKey(), type);
	}

	public void registerMissionRequireType(MissionRequireType type) {
		missionRequiresType.put(type.getKey(), type);
	}

	public void registerQuestRequireType(QuestRequireType type) {
		questRequiresType.put(type.getKey(), type);
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
							requires.put(rew.getNameID(), rew);
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
							requires.put(rew.getNameID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return requires;
	}

	public LinkedHashMap<String, Require> loadRequires(YmlLoadableWithCooldown loadable, ConfigSection section) {
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
							requires.put(rew.getNameID(), rew);
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
}
