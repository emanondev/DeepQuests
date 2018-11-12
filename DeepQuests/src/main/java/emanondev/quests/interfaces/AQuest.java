package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import emanondev.quests.utils.StringUtils;

public abstract class AQuest<T extends User<T>> extends AQuestComponentWithCooldown<T>
							implements Quest<T> {

	@SuppressWarnings("unchecked")
	public AQuest(Map<String, Object> map) {
		super(map);
		List<Mission<T>> list = null;
		try {
			list = (List<Mission<T>>) map.get(Paths.QUEST_MISSIONS);
		} catch (Error e) {
			e.printStackTrace();
		}
		if (list== null)
			list = new ArrayList<>();
		for (Mission<T> mission : list) {
			if (missions.containsKey(mission.getKey()))
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			mission.setParent(this);
			missions.put(mission.getKey(),mission);
		}
		

		List<Require<T>> listRequires = null;
		try {
			listRequires = (List<Require<T>>) map.get(Paths.QUEST_REQUIRES);
		} catch (Error e) {
			e.printStackTrace();
		}
		if (listRequires== null)
			listRequires = new ArrayList<>();
		for (Require<T> require : listRequires) {
			if (requires.containsKey(require.getKey()))
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			require.setParent(this);
			requires.put(require.getKey(),require);
		}
		
		isDeveloped = map.containsKey(Paths.QUEST_IS_DEVELOPED) ? false : (boolean) map.get(Paths.QUEST_IS_DEVELOPED);
	}

	@Override
	public Map<String,Object> serialize(){
		Map<String, Object> map = super.serialize();
		if (missions.size()>0)
			map.put(Paths.QUEST_MISSIONS,new ArrayList<>(missions.values()));
		if (requires.size()>0)
			map.put(Paths.QUEST_REQUIRES,new ArrayList<>(requires.values()));
		return map;
	}

	@Override
	public long getCooldownLeft(T user) {
		UserQuestData data = user.getData().getQuestData(this);
		return Math.max(0,data.getCooldownTimeLeft());
	}

	@Override
	public List<String> getInfo() {
		List<String> info = new ArrayList<String>();
		info.add("&9&lQuest: &6"+ this.getDisplayName());
		info.add("&8KEY: "+ this.getKey());
		info.add("");
		if (!isDeveloped()) {
			info.add("&cPlayers can't advance nor see this Quest");
			info.add("&cbecause it's marked as uncompleted.");
			info.add("");
		}
			
		info.add("&9Priority: &e"+getPriority());

		if (!this.isRepeatable())
			info.add("&9Repeatable: &cFalse");
		else {
			info.add("&9Repeatable: &aTrue");
			info.add("&9Cooldown: &e" + StringUtils.getStringCooldown(this.getCooldownTime()));
		}
		if (missions.size() > 0) {
			info.add("&9Missions:");
			for (Mission<T> mission : missions.values()) {
				info.add("&9 - &e"+mission.getDisplayName());
			}
		}
		List<String> blackList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();
		for (World world:Bukkit.getServer().getWorlds()) {
			if (isWorldAllowed(world))
				whiteList.add("&9 - &c" + world.getName());
			else 
				blackList.add("&9 - &a" + world.getName());
		}
		if (blackList.size()==0)
			info.add("&9All Worlds are allowed");
		else if (whiteList.size()<=blackList.size()) {
			info.add("&9Whitelisted Worlds:");
			info.addAll(whiteList);
		} else {
			info.add("&9Blacklisted Worlds:");
			info.addAll(blackList);
		}
		if (requires.size() > 0) {
			info.add("&9Requires:");
			for (Require<T> require : requires.values()) {
				info.add("&9 - &e" + require.getDisplayName());
				info.add("   &8" + require.getType().getID());
			}
		}
		return info;
	}

	private Map<String,Mission<T>> missions = new HashMap<>();
	@Override
	public Mission<T> getMission(String key) {
		return missions.get(key);
	}

	@Override
	public Collection<Mission<T>> getMissions() {
		return Collections.unmodifiableCollection(missions.values());
	}

	@Override
	public boolean addMission(Mission<T> mission) {
		if (missions.containsKey(mission.getKey()))
			throw new IllegalArgumentException();
		mission.setParent(this);
		missions.put(mission.getKey(),mission);
		return true;
	}

	@Override
	public boolean removeMission(String key) {
		return missions.remove(key)!=null;
	}

	@Override
	public QuestContainer<T> getParent() {
		return (QuestContainer<T>) super.getParent();
	}

	private Map<String,Require<T>> requires = new HashMap<>();
	@Override
	public Collection<Require<T>> getRequires() {
		return Collections.unmodifiableCollection(requires.values());
	}

	@Override
	public Require<T> getRequire(String key) {
		return requires.get(key);
	}

	@Override
	public boolean addRequire(Require<T> require) {
		if (requires.containsKey(require.getKey()))
			throw new IllegalArgumentException();
		require.setParent(this);
		requires.put(require.getKey(),require);
		return true;
	}

	@Override
	public boolean removeRequire(String key) {
		return requires.remove(key)!=null;
	}
	
	private boolean isDeveloped;
	@Override
	public boolean isDeveloped() {
		return isDeveloped;
	}
}
