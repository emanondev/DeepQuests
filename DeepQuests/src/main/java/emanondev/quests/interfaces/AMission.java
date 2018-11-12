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


public abstract class AMission<T extends User<T>> extends AQuestComponentWithCooldown<T>
				implements Mission<T> {

	@SuppressWarnings("unchecked")
	public AMission(Map<String, Object> map) {
		super(map);
		List<Task<T>> list = null;
		try {
			list = (List<Task<T>>) map.get(Paths.MISSION_TASKS);
		} catch (Error e) {
			e.printStackTrace();
		}
		if (list== null)
			list = new ArrayList<>();
		for (Task<T> task : list) {
			if (tasks.containsKey(task.getKey()))
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			task.setParent(this);
			tasks.put(task.getKey(),task);
		}
		

		List<Require<T>> listRequires = null;
		try {
			listRequires = (List<Require<T>>) map.get(Paths.MISSION_REQUIRES);
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
		

		List<Reward<T>> listRewards = null;
		try {
			listRewards = (List<Reward<T>>) map.get(Paths.MISSION_COMPLETE_REWARDS);
		} catch (Error e) {
			e.printStackTrace();
		}
		if (listRewards== null)
			listRewards = new ArrayList<>();
		for (Reward<T> reward : listRewards) {
			if (completeRewards.containsKey(reward.getKey()))
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			reward.setParent(this);
			completeRewards.put(reward.getKey(),reward);
		}

		listRewards = null;
		try {
			listRewards = (List<Reward<T>>) map.get(Paths.MISSION_FAIL_REWARDS);
		} catch (Error e) {
			e.printStackTrace();
		}
		if (listRewards== null)
			listRewards = new ArrayList<>();
		for (Reward<T> reward : listRewards) {
			if (failRewards.containsKey(reward.getKey()))
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			reward.setParent(this);
			failRewards.put(reward.getKey(),reward);
		}

		listRewards = null;
		try {
			listRewards = (List<Reward<T>>) map.get(Paths.MISSION_START_REWARDS);
		} catch (Error e) {
			e.printStackTrace();
		}
		if (listRewards== null)
			listRewards = new ArrayList<>();
		for (Reward<T> reward : listRewards) {
			if (startRewards.containsKey(reward.getKey()))
				new IllegalArgumentException("Not Unique key found").printStackTrace();
			reward.setParent(this);
			startRewards.put(reward.getKey(),reward);
		}
	}
	
	@Override
	public Map<String,Object> serialize() {
		Map<String,Object> map = super.serialize();
		if (tasks.size()>0)
			map.put(Paths.MISSION_TASKS,new ArrayList<>(tasks.values()));
		if (requires.size()>0)
			map.put(Paths.MISSION_REQUIRES,new ArrayList<>(requires.values()));
		if (completeRewards.size()>0)
			map.put(Paths.MISSION_COMPLETE_REWARDS,new ArrayList<>(completeRewards.values()));
		if (startRewards.size()>0)
			map.put(Paths.MISSION_START_REWARDS,new ArrayList<>(startRewards.values()));
		if (failRewards.size()>0)
			map.put(Paths.MISSION_FAIL_REWARDS,new ArrayList<>(failRewards.values()));
		return map;
	}

	@Override
	public long getCooldownLeft(T user) {
		UserMissionData data = user.getData().getMissionData(this);
		return Math.max(0,data.getCooldownTimeLeft());
	}

	@Override
	public List<String> getInfo() {
		List<String> info = new ArrayList<String>();
		info.add("&9&lMission: &6"+ this.getDisplayName());
		info.add("&8KEY: "+ this.getKey());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		info.add("&9Quest: &e"+getParent().getDisplayName());

		if (!this.isRepeatable())
			info.add("&9Repeatable: &cFalse");
		else {
			info.add("&9Repeatable: &aTrue");
			info.add("&9Cooldown: &e" + StringUtils.getStringCooldown(getCooldownTime()));
		}
		if (tasks.size() > 0) {
			info.add("&9Missions:");
			for (Task<T> task : tasks.values()) {
				info.add("&9 - &e"+task.getDisplayName());
				info.add("   &7Type: "+task.getType().getID());
			}
		}
		List<String> blackList = new ArrayList<String>();
		List<String> whiteList = new ArrayList<String>();
		for (World world:Bukkit.getServer().getWorlds()) {
			if (isWorldAllowed(world) && getParent().isWorldAllowed(world))
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
				info.add("   &7" + require.getType().getID());
			}
		}
		if (startRewards.size() > 0) {
			info.add("&9Start Rewards:");
			for (Reward<T> reward : startRewards.values()) {
				info.add("&9 - &e" + reward.getDisplayName());
				info.add("   &7" + reward.getType().getID());
			}
		}
		if (completeRewards.size() > 0) {
			info.add("&9Complete Rewards:");
			for (Reward<T> reward : completeRewards.values()) {
				info.add("&9 - &e" + reward.getDisplayName());
				info.add("   &7" + reward.getType().getID());
			}
		}
		if (failRewards.size() > 0) {
			info.add("&9Fail Rewards:");
			for (Reward<T> reward : failRewards.values()) {
				info.add("&9 - &e" + reward.getDisplayName());
				info.add("   &7" + reward.getType().getID());
			}
		}
		return info;
	}

	private Map<String,Task<T>> tasks = new HashMap<>();
	@Override
	public Task<T> getTask(String key) {
		return tasks.get(key);
	}

	@Override
	public Collection<Task<T>> getTasks() {
		return Collections.unmodifiableCollection(tasks.values());
	}

	@Override
	public boolean addTask(Task<T> task) {
		if (tasks.containsKey(task.getKey()))
			throw new IllegalArgumentException();
		task.setParent(this);
		tasks.put(task.getKey(),task);
		return true;
	}

	@Override
	public boolean removeTask(String key) {
		return tasks.remove(key)!=null;
	}

	@Override
	public Quest<T> getParent() {
		return (Quest<T>) super.getParent();
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

	private Map<String,Reward<T>> startRewards = new HashMap<>();
	@Override
	public Collection<Reward<T>> getStartRewards() {
		return Collections.unmodifiableCollection(startRewards.values());
	}
	@Override
	public Reward<T> getStartReward(String key) {
		return startRewards.get(key);
	}
	@Override
	public boolean addStartReward(Reward<T> reward) {
		if (startRewards.containsKey(reward.getKey()))
			throw new IllegalArgumentException();
		reward.setParent(this);
		startRewards.put(reward.getKey(),reward);
		return true;
	}
	@Override
	public boolean removeStartReward(String key) {
		return startRewards.remove(key)!=null;
	}
	
	private Map<String,Reward<T>> completeRewards = new HashMap<>();
	@Override
	public Collection<Reward<T>> getCompleteRewards() {
		return Collections.unmodifiableCollection(completeRewards.values());
	}
	@Override
	public Reward<T> getCompleteReward(String key) {
		return completeRewards.get(key);
	}
	@Override
	public boolean addCompleteReward(Reward<T> reward) {
		if (completeRewards.containsKey(reward.getKey()))
			throw new IllegalArgumentException();
		reward.setParent(this);
		completeRewards.put(reward.getKey(),reward);
		return true;
	}
	@Override
	public boolean removeCompleteReward(String key) {
		return completeRewards.remove(key)!=null;
	}

	private Map<String,Reward<T>> failRewards = new HashMap<>();
	@Override
	public Collection<Reward<T>> getFailRewards() {
		return Collections.unmodifiableCollection(failRewards.values());
	}
	@Override
	public Reward<T> getFailReward(String key) {
		return failRewards.get(key);
	}
	@Override
	public boolean addFailReward(Reward<T> reward) {
		if (failRewards.containsKey(reward.getKey()))
			throw new IllegalArgumentException();
		reward.setParent(this);
		failRewards.put(reward.getKey(),reward);
		return true;
	}
	@Override
	public boolean removeFailReward(String key) {
		return failRewards.remove(key)!=null;
	}

}
