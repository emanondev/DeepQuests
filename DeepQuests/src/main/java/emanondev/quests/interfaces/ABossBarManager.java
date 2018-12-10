package emanondev.quests.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.interfaces.storage.IConfig;

public abstract class ABossBarManager<T extends User<T>> implements BossBarManager<T> {
	private IConfig config;
	public ABossBarManager(QuestManager<T> questManager) {
		config = questManager.getBossBarConfig();
		reload();
	}
	private long duration = 70;
	private BarColor defaultColor = BarColor.BLUE;
	private BarStyle defaultStyle = BarStyle.SEGMENTED_20;
	private HashMap<T,HashMap<Task<T>,TaskBossBar<T>>> map = new HashMap<>();
	private HashMap<TaskType<T>,BarStyle> tasksStyle = new HashMap<>();
	private HashMap<TaskType<T>,BarColor> tasksColor = new HashMap<>();
	private boolean defaultShowBossBar = true;
	private HashMap<TaskType<T>,Boolean> tasksShowBossBar = new HashMap<>();;
	
	public void reload() {
		try {
			config.reload();
			duration = Math.max(10L,config.getLong(Paths.BOSSBAR_MANAGER_DURATION,70L));
			if (!config.exists(Paths.BOSSBAR_MANAGER_DURATION))
				config.set(Paths.BOSSBAR_MANAGER_DURATION,70L);
			
			try {
				defaultColor = BarColor.valueOf(config.getString(Paths
						.BOSSBAR_MANAGER_DEFAULT_COLOR).toUpperCase());
				if (!config.exists(Paths.BOSSBAR_MANAGER_DEFAULT_COLOR))
					config.set(Paths.BOSSBAR_MANAGER_DEFAULT_COLOR,defaultColor.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				defaultStyle = BarStyle.valueOf(config.getString(Paths
						.BOSSBAR_MANAGER_DEFAULT_STYLE).toUpperCase());
				if (!config.exists(Paths.BOSSBAR_MANAGER_DEFAULT_STYLE))
					config.set(Paths.BOSSBAR_MANAGER_DEFAULT_STYLE,defaultStyle.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				defaultShowBossBar = config.getBoolean(Paths
						.BOSSBAR_MANAGER_DEFAULT_SHOWBOSSBAR);
				if (!config.exists(Paths.BOSSBAR_MANAGER_DEFAULT_COLOR))
					config.set(Paths.BOSSBAR_MANAGER_DEFAULT_COLOR,defaultColor.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for (HashMap<Task<T>,TaskBossBar<T>> tMap: map.values())
				for (TaskBossBar<T> bar : tMap.values())
					bar.dispose();
			tasksStyle.clear();
			tasksColor.clear();
			map.clear();
			config.save();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	
	public void onProgress(T user,Task<T> task) {
		Collection<Player> targets = getPlayers(user,task);
		if (targets==null || targets.isEmpty())
			return;
		List<Player> players = new ArrayList<>(targets);
		for (Player target:targets)
			if (target.hasPermission(Perms.SEE_TASK_BOSSBAR))
				players.add(target);
		if (players.isEmpty())
			return;
		if (!map.containsKey(user)) {
			HashMap<Task<T>,TaskBossBar<T>> tMap = new HashMap<>();
			tMap.put(task,new TaskBossBar<T>(task,user.getData().getTaskData(task)));
			map.put(user, tMap);
		}
		else if (!map.get(user).containsKey(task)) {
			map.get(user).put(task,new TaskBossBar<T>(task,user.getData().getTaskData(task)));
		}
		TaskBossBar<T> taskBar = map.get(user).get(task);
		taskBar.updateProgress();
		long lastUpdate = taskBar.getLastUpdate();
		Bukkit.getScheduler().runTaskLater(Quests.get(),new Runnable() {

			@Override
			public void run() {
				if (lastUpdate != taskBar.getLastUpdate())
					return;
				if (map.containsKey(user))
					map.get(user).remove(task);
				taskBar.dispose();
			}
			
		},duration);
	}
	
	public BarColor getBarColor(TaskType<T> type) {
		if (tasksColor.containsKey(type))
			return tasksColor.get(type);
		
		String path = "type."+type.getID()+".color";
		BarColor color;
		try {
			color = BarColor.valueOf(config.getString(path).toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		 	color = null;
		}
		if (color ==null){
			config.set(path,defaultColor.toString());
			try {
				config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			color = defaultColor;
		}
		tasksColor.put(type,color);
		return color;
	}
	public BarStyle getBarStyle(TaskType<T> type) {
		if (tasksStyle.containsKey(type))
			return tasksStyle.get(type);
		
		String path = "type."+type.getID()+".style";
		BarStyle style = null;
		try {
			style = BarStyle.valueOf(config.getString(path).toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (style ==null){
			config.set(path,defaultStyle.toString());
			try {
				config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			style = defaultStyle;
		}
		tasksStyle.put(type,style);
		return style;
	}

	public boolean getShowBossBar(TaskType<T> type)	{
		if (tasksShowBossBar.containsKey(type))
			return tasksShowBossBar.get(type);
		
		String path = "type."+type.getID()+".showBossBar";
		boolean result = true;
		try {
			if (!config.exists(path)) {
				config.set(path,defaultShowBossBar);
				config.save();
			}
			result = config.getBoolean(path);
		} catch (Exception e) {
			e.printStackTrace();
			config.set(path,defaultShowBossBar);
			try {
				config.save();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			result = defaultShowBossBar;
		}
		tasksShowBossBar.put(type,result);
		return result;
	}
	
}