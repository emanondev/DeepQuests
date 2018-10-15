package emanondev.quests.task;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.QuestComponent;

public interface Task extends QuestComponent {
	public List<String> getInfo();
	
	public void setWorldsList(Collection<String> coll);

	public Set<String> getWorldsList();

	public Set<World> getWorldsSet();

	public boolean isWorldListBlacklist();

	public boolean setWorldListBlacklist(boolean isBlacklist);

	public boolean isWorldAllowed(World w);

	public default BarStyle getBossBarStyle() {
		return BarStyle.SEGMENTED_20;
	}

	public default BarColor getBossBarColor() {
		return BarColor.BLUE;
	}
	
	
	/**
	 * 
	 * @return the Mission that holds this task
	 */
	public Mission getParent();
	/**
	 * 
	 * @return the type of the task
	 */
	public TaskType getTaskType();
	/**
	 * when a player starts a Task his progress on th task is 0<br>
	 * task is completed when player progress reach this value
	 * @return the maximus progress of this task
	 */
	public int getMaxProgress();
	
	public String getUnstartedDescription();
	public String getProgressDescription();
	
	public boolean onProgress(QuestPlayer p);
	public boolean onProgress(QuestPlayer p,int amount);
	
	public boolean toggleWorldFromWorldList(World world);
	
	@Override
	public default QuestManager getQuestManager() {
		return getParent().getQuestManager();
	}
}
