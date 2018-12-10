package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import emanondev.quests.utils.StringUtils;

public abstract class ATask<T extends User<T>> extends AQuestComponentWithWorlds<T>
				implements Task<T> {

	@SuppressWarnings({ "unchecked" })
	public ATask(Map<String, Object> map) {
		super(map);
		taskType = (String) map.get(Paths.TYPE_NAME);
		if (taskType==null || taskType.isEmpty())
			throw new NullPointerException("invalid task id");
		progressChance = (double) map.getOrDefault(Paths.TASK_PROGRESS_CHANCE,1D);
		maxProgress = (int) map.getOrDefault(Paths.TASK_MAX_PROGRESS,1);
		if (map.containsKey(Paths.TASK_BAR_STYLE)) {
			isBarStyleDefault = false;
			try {
				barStyle = BarStyle.valueOf((String) map.get(Paths.TASK_BAR_STYLE));
			}catch (Exception e) {
				barStyle = BarStyle.SOLID;
			}
		}
		else {
			isBarStyleDefault = true;
			barStyle = BarStyle.SOLID;
		}
			
		if (map.containsKey(Paths.TASK_BAR_COLOR)) {
			isBarColorDefault = false;
			try {
				barColor = BarColor.valueOf((String) map.get(Paths.TASK_BAR_COLOR));
			}catch (Exception e) {
				barColor = BarColor.BLUE;
			}
		}
		else {
			isBarColorDefault = true;
			barColor = BarColor.BLUE;
		}
		
		List<Reward<T>> list;
		try {
			list = (List<Reward<T>>) map.get(Paths.TASK_REWARDS);
			if (list == null)
				list = new ArrayList<>();
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<>();
		}
		for(Reward<T> reward:list) {
			reward.setParent(this);
			rewards.put(reward.getKey(),reward);
		}
		
		if (map.containsKey(Paths.TASK_SHOW_BOSSBAR)) {
			isShowBossBarDefault = false;
			showBossBar = (boolean) map.get(Paths.TASK_SHOW_BOSSBAR);
		} else {
			isShowBossBarDefault = true;
		}
		
		//TODO read progress description and stuffs
	}

	@Override
	public List<String> getInfo() {
		List<String> info = new ArrayList<String>();
		info.add("&9&lTask: &6"+ this.getDisplayName());
		info.add("&8Type: &7"+getType().getID());
	
		info.add("&8KEY: "+ this.getKey());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		info.add("&9Max Progress: &e"+getMaxProgress());
		info.add("&9Progress Chance: &e"+StringUtils.getDecimalFormat()
			.format(getProgressChance()*100)+"%");
		if (showBossBar)
			info.add("&9Show BossBar onProgress: &cfalse");
		else {
			info.add("&9Show BossBar onProgress: &atrue");
			info.add("&9  BarStyle: &e"+barStyle.toString());
			info.add("&9  BarColor: &e"+barColor.toString());
		}
			
		info.add("&9Quest: &e"+getParent().getParent().getDisplayName());
		info.add("&9Mission: &e"+getParent().getDisplayName());
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
		if (rewards.size() > 0) {
			info.add("&9Rewards:");
			for (Reward<T> reward : rewards.values()) {
				info.add("&9 - &e" + reward.getDisplayName());
				info.add("   &7" + reward.getType().getID());
			}
		}
		return info;
	}
	
	@Override
	public Map<String,Object> serialize(){
		Map<String, Object> map = super.serialize();
		map.put(Paths.TYPE_NAME,taskType);
		if (progressChance<1)
			map.put(Paths.TASK_PROGRESS_CHANCE,progressChance);
		if (maxProgress!=1)
			map.put(Paths.TASK_MAX_PROGRESS,maxProgress);
		if (!isBarStyleDefault)
			map.put(Paths.TASK_BAR_STYLE,barStyle.toString());
		if (!isBarColorDefault)
			map.put(Paths.TASK_BAR_COLOR,barColor.toString());
		if (!isProgressDescriptionDefault)
			map.put(Paths.TASK_PROGRESS_DESCRIPTION,progressDescription);
		if (!isUnstartedDescriptionDefault)
			map.put(Paths.TASK_UNSTARTED_DESCRIPTION,unstartedDescription);
		if (!isShowBossBarDefault)
			map.put(Paths.TASK_SHOW_BOSSBAR,showBossBar);
		List<Reward<T>> list = new ArrayList<>(rewards.values());
		if(!list.isEmpty())
			map.put(Paths.TASK_REWARDS,list);
		return map;
	}
	
	protected boolean fullyLoaded = false;
	
	@Override
	public void setParent(QuestComponent<T> mission) {
		super.setParent(mission);
	}

	@Override
	public Mission<T> getParent() {
		return (Mission<T>) super.getParent();
	}

	private Map<String,Reward<T>> rewards = new HashMap<>();
	@Override
	public Collection<Reward<T>> getRewards() {
		return Collections.unmodifiableCollection(rewards.values());
	}
	@Override
	public Reward<T> getReward(String key) {
		return rewards.get(key);
	}
	@Override
	public boolean addReward(Reward<T> reward) {
		if (rewards.containsKey(reward.getKey()))
			return false;
		rewards.put(reward.getKey(),reward);
		return true;
	}
	@Override
	public boolean removeReward(String key) {
		if (!rewards.containsKey(key))
			return false;
		rewards.remove(key);
		return true;
	}
	
	private double progressChance;
	@Override
	public double getProgressChance() {
		return progressChance;
	}
	@Override
	public boolean setProgressChance(double progressChance) {
		if (progressChance<=0)
			return false;
		this.progressChance = Math.min(1,progressChance);
		return true;
	}
	
	private int maxProgress;
	@Override
	public int getMaxProgress() {
		return maxProgress;
	}
	@Override
	public boolean setMaxProgress(int maxProgress) {
		this.maxProgress = Math.max(1,maxProgress);
		return true;
	}

	private String taskType;
	
	public String getTypeName() {
		return taskType;
	}

	private boolean isBarStyleDefault;
	private BarStyle barStyle;
	@Override
	public BarStyle getBossBarStyle() {
		if (fullyLoaded == false) {
			fullyLoaded = true;
			if (isBarStyleDefault)
				barStyle = getQuestManager().getBossBarManager()
					.getBarStyle(this.getType());
		}
		return barStyle;
	}

	private boolean isBarColorDefault;
	private BarColor barColor;
	@Override
	public BarColor getBossBarColor() {
		if (fullyLoaded == false) {
			fullyLoaded = true;
			if (isBarColorDefault)
				barColor = getQuestManager().getBossBarManager()
					.getBarColor(this.getType());
		}
		return barColor;
	}

	private boolean isUnstartedDescriptionDefault;
	private String unstartedDescription = null;
	@Override
	public String getRawUnstartedDescription() {
		if (fullyLoaded == false) {
			fullyLoaded = true;
			if (isUnstartedDescriptionDefault)
				unstartedDescription = getType().getDefaultUnstartedDescription(this)==null 
				? Holders.DISPLAY_NAME+" "+Holders.TASK_MAX_PROGRESS
				: getType().getDefaultUnstartedDescription(this);
		}
		return unstartedDescription;
	}
	
	private boolean isProgressDescriptionDefault;
	private String progressDescription = null;
	@Override
	public String getRawProgressDescription() {
		if (fullyLoaded == false) {
			fullyLoaded = true;
			if (isProgressDescriptionDefault)
				progressDescription = getType().getDefaultProgressDescription(this)==null 
				? Holders.DISPLAY_NAME+" "+Holders.TASK_CURRENT_PROGRESS+"/"+Holders.TASK_MAX_PROGRESS 
				: getType().getDefaultProgressDescription(this);
		}
		return progressDescription;
	}
	
	@Override
	public boolean isWorldAllowed(World world) {
		return super.isWorldAllowed(world)&&getParent().isWorldAllowed(world)&&
				getParent().getParent().isWorldAllowed(world);
	}

	@Override
	public boolean setBossBarStyle(BarStyle barStyle) {
		if (barStyle == null) {
			this.isBarStyleDefault = true;
			this.barStyle = getQuestManager().getBossBarManager()
					.getBarStyle(this.getType());
		}
		else {
			this.isBarStyleDefault = false;
			this.barStyle = barStyle;
		}
		return true;
	}

	@Override
	public boolean setBossBarColor(BarColor barColor) {
		if (barColor == null) {
			this.isBarColorDefault = true;
			this.barColor = getQuestManager().getBossBarManager()
					.getBarColor(this.getType());
		}
		else {
			this.isBarColorDefault = false;
			this.barColor = barColor;
		}
		return true;
	}

	private boolean isShowBossBarDefault;
	private boolean showBossBar = true;
	@Override
	public boolean showBossBar() {
		if (fullyLoaded == false) {
			fullyLoaded = true;
			if (isShowBossBarDefault)
				showBossBar = getQuestManager().getBossBarManager().getShowBossBar(getType());
		}
		return showBossBar;
	}

	@Override
	public void setShowBossBar(Boolean value) {
		if (value == null) {
			isShowBossBarDefault = true;
			showBossBar = getQuestManager().getBossBarManager().getShowBossBar(getType());
		} else {
			isShowBossBarDefault = false;
			showBossBar = value;
		}
	}

	@Override
	public boolean setUnstartedDescription(String desc) {
		if (desc==null) {
			isUnstartedDescriptionDefault = true;
			unstartedDescription = getType().getDefaultUnstartedDescription(this)==null 
					? Holders.DISPLAY_NAME+" "+Holders.TASK_MAX_PROGRESS
							: getType().getDefaultUnstartedDescription(this);
		} else {
			isUnstartedDescriptionDefault = false;
			unstartedDescription = desc;
		}
		return true;
	}

	@Override
	public boolean setProgressDescription(String desc) {
		if (desc==null) {
			isProgressDescriptionDefault = true;
			progressDescription = getType().getDefaultProgressDescription(this)==null 
					? Holders.DISPLAY_NAME+" "+Holders.TASK_CURRENT_PROGRESS+"/"+Holders.TASK_MAX_PROGRESS 
							: getType().getDefaultProgressDescription(this);
		} else {
			isProgressDescriptionDefault = false;
			progressDescription = desc;
		}
		return true;
	}
	


	@Override
	protected Set<String> getDefaultWorldsList() {
		return new HashSet<String>();
	}

	@Override
	protected boolean getDefaultWorldsAreWhitelist() {
		return false;
	}

}
