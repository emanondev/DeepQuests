package emanondev.quests.bossbar;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.Task;

public class BossBarManager {
	private long duration = 70;
	private BarColor color = BarColor.BLUE;
	private BarStyle style = BarStyle.SEGMENTED_20;
	private HashMap<QuestPlayer,HashMap<Task,TaskBossBar>> map = new HashMap<QuestPlayer,HashMap<Task,TaskBossBar>>();
	public void reload() {
		for (HashMap<Task,TaskBossBar> tMap: map.values())
			for (TaskBossBar bar : tMap.values())
				bar.dispose();
		map.clear();
	}
	public void onProgress(QuestPlayer questPlayer,Task task) {
		if (!questPlayer.getPlayer().hasPermission(Perms.SEE_TASK_BOSSBAR))
			return;
		if (!map.containsKey(questPlayer)) {
			HashMap<Task,TaskBossBar> tMap = new HashMap<Task,TaskBossBar>();
			tMap.put(task,new TaskBossBar(questPlayer,task,color,style));
			map.put(questPlayer, tMap);
		}
		else if (!map.get(questPlayer).containsKey(task)) {
			map.get(questPlayer).put(task,new TaskBossBar(questPlayer,task,color,style));
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
}
