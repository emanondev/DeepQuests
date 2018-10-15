package emanondev.quests.bossbar;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import emanondev.quests.Quests;
import emanondev.quests.player.OfflineQuestPlayer.QuestData.MissionData.TaskData;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;

public class TaskBossBar {
	
	private BossBar bar;
	private TaskData taskData;
	public TaskBossBar (Player p,Task t) {
		this(Quests.get().getPlayerManager().getQuestPlayer(p),t);
	}
	public TaskBossBar (QuestPlayer p,Task task) {
		if (p == null || task == null)
			throw new NullPointerException();
		taskData = p.getTaskData(task);
		bar = Bukkit.createBossBar("", task.getBossBarColor(), task.getBossBarStyle());
		updateProgress();
		bar.addPlayer(p.getPlayer());
	}
	public void dispose() {
		bar.removeAll();
	}

	public BarColor getColor() {
		return bar.getColor();
	}

	public double getProgress() {
		return bar.getProgress();
	}

	public BarStyle getStyle() {
		return bar.getStyle();
	}

	public String getTitle() {
		return bar.getTitle();
	}

	public boolean isVisible() {
		return bar.isVisible();
	}

	public void setColor(BarColor color) {
		bar.setColor(color);
	}
	private long lastUpdate = 0; 
	public void updateProgress() {
		bar.setProgress(((double) taskData.getProgress())/taskData.getTask().getMaxProgress());
		try {
			bar.setTitle(StringUtils.fixColorsAndHolders(StringUtils.convertText(
					bar.getPlayers().get(0), 
					"&3"+getTask().getDisplayName()+" &b"+taskData.getProgress()+"&3/"+getTask().getMaxProgress())));
		} catch (Exception e) {
			bar.setTitle(StringUtils.fixColorsAndHolders(StringUtils.convertText(null,
					"&3"+getTask().getDisplayName()+" &b"+taskData.getProgress()+"&3/"+getTask().getMaxProgress())));
		}
		lastUpdate = new Date().getTime();
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public Task getTask() {
		return taskData.getTask();
	}
	public void setStyle(BarStyle style) {
		bar.setStyle(style);
	}
	public void setVisible(boolean visible) {
		bar.setVisible(visible);
	}
}
