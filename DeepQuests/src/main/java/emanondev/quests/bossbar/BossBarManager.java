package emanondev.quests.bossbar;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;

public class BossBarManager {
	private YMLConfig data = new YMLConfig(Quests.get(),"BossBarConfig");
	public BossBarManager() {
		reload();
	}
	private long duration = 70;
	private BarColor defaultColor = BarColor.BLUE;
	private BarStyle defaultStyle = BarStyle.SEGMENTED_20;
	private HashMap<QuestPlayer,HashMap<Task,TaskBossBar>> map = new HashMap<QuestPlayer,HashMap<Task,TaskBossBar>>();
	private HashMap<TaskType,BarStyle> tasksStyle = new HashMap<TaskType,BarStyle>();
	private HashMap<TaskType,BarColor> tasksColor = new HashMap<TaskType,BarColor>();
	
	public void reload() {
		data.reload();
		duration = Math.max(10L,data.getLong("duration.task.ticks",70L));
		try {
			defaultColor = BarColor.valueOf(data.getString("barcolor.default").toUpperCase());
		} catch (Exception e) {
			defaultColor = BarColor.BLUE;
			e.printStackTrace();
		}
		try {
			defaultStyle = BarStyle.valueOf(data.getString("barstyle.default").toUpperCase());
		} catch (Exception e) {
			defaultStyle = BarStyle.SEGMENTED_20;
			e.printStackTrace();
		}
		for (HashMap<Task,TaskBossBar> tMap: map.values())
			for (TaskBossBar bar : tMap.values())
				bar.dispose();
		tasksStyle.clear();
		tasksColor.clear();
		map.clear();
	}
	
	
	public void onProgress(QuestPlayer questPlayer,Task task) {
		if (!questPlayer.getPlayer().hasPermission(Perms.SEE_TASK_BOSSBAR))
			return;
		if (!map.containsKey(questPlayer)) {
			HashMap<Task,TaskBossBar> tMap = new HashMap<Task,TaskBossBar>();
			tMap.put(task,new TaskBossBar(questPlayer,task));
			map.put(questPlayer, tMap);
		}
		else if (!map.get(questPlayer).containsKey(task)) {
			map.get(questPlayer).put(task,new TaskBossBar(questPlayer,task));
		}
		TaskBossBar taskBar = map.get(questPlayer).get(task);
		taskBar.updateProgress();
		long lastUpdate = taskBar.getLastUpdate();
		Bukkit.getScheduler().runTaskLater(Quests.get(),new Runnable() {

			@Override
			public void run() {
				if (lastUpdate != taskBar.getLastUpdate())
					return;
				if (map.containsKey(questPlayer))
					map.get(questPlayer).remove(task);
				taskBar.dispose();
			}
			
		},duration);
	}
	
	public BarColor getTaskColor(Task task) {
		BarColor color = tasksColor.get(task.getTaskType());
		if (color!=null)
			return color;
		String path = "barcolor.task."+task.getTaskType().getKey().toLowerCase();
		try {
			color = BarColor.valueOf(data.getString(path).toUpperCase());
		} catch (Exception e) {}
		if (color!=null) {
			tasksColor.put(task.getTaskType(),color);
			return color;
		}
		data.set(path,defaultColor.toString());
		tasksColor.put(task.getTaskType(),defaultColor);
		data.setDirty(true);
		return defaultColor;
	}
	public BarStyle getTaskStyle(Task task) {
		BarStyle style = tasksStyle.get(task.getTaskType());
		if (style!=null)
			return style;
		String path = "barstyle.task."+task.getTaskType().getKey().toLowerCase();
		try {
			style = BarStyle.valueOf(data.getString(path).toUpperCase());
		} catch (Exception e) {}
		if (style!=null) {
			tasksStyle.put(task.getTaskType(),style);
			return style;
		}
		data.set(path,defaultStyle.toString());
		tasksStyle.put(task.getTaskType(),defaultStyle);
		data.setDirty(true);
		return defaultStyle;
		
	}
}
