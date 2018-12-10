package emanondev.quests.interfaces;

import java.util.Collection;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import emanondev.quests.utils.StringUtils;

public class TaskBossBar<T extends User<T>> {
	
	private BossBar bar;
	private UserTaskData taskData;
	private Task<T> task;
	public TaskBossBar (Task<T> task,UserTaskData taskData) {
		this.task = task;
		this.taskData = taskData;
		bar = Bukkit.createBossBar("", task.getBossBarColor(), task.getBossBarStyle());
		updateProgress();
	}

	public void addPlayers(Collection<Player> players) {
		for (Player p:players)
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
	public Task<T> getTask() {
		return task;
	}
	public void setStyle(BarStyle style) {
		bar.setStyle(style);
	}
	public void setVisible(boolean visible) {
		bar.setVisible(visible);
	}
}
