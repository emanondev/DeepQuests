package emanondev.quests.interfaces.player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;

import emanondev.quests.interfaces.Mission;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.QuestComponent;
import emanondev.quests.interfaces.Reward;
import emanondev.quests.interfaces.Task;
import emanondev.quests.interfaces.TaskType;


@SerializableAs("PlayerTask")
@DelegateDeserialization(PlayerTaskManager.class)
class PlayerVoidTask implements Task<QuestPlayer>{
	
	private final Map<String,Object> map;
	private Mission<QuestPlayer> parent = null;
	
	public PlayerVoidTask(Map<String,Object> map) {
		this.map = map;
	}

	@Override
	public boolean isWorldAllowed(World world) {
		return false;
	}

	@Override
	public List<String> getInfo() {
		return Arrays.asList("&cThis task couldn't be loaded correctly",
				"&cMaybe a plugin wasn't loaded correctly",
				"&cExample: NPCKillTask and Citizen not loading",
				"&cIf error wasn't caused by a plugin you can delete",
				"&cthis task or fix it manually editing the database file");
	}

	@Override
	public String getKey() {
		return map.get(Paths.KEY)==null ? null : map.get(Paths.KEY).toString();
	}

	@Override
	public void setParent(QuestComponent<QuestPlayer> parent) {
		if (this.parent==null)
			this.parent = (Mission<QuestPlayer>) parent;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public boolean setPriority(int priority) {
		return false;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public boolean setDisplayName(String name) {
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		return map;
	}

	@Override
	public Mission<QuestPlayer> getParent() {
		return parent;
	}

	@Override
	public Collection<Reward<QuestPlayer>> getRewards() {
		return null;
	}

	@Override
	public Reward<QuestPlayer> getReward(String key) {
		return null;
	}

	@Override
	public boolean addReward(Reward<QuestPlayer> reward) {
		return false;
	}

	@Override
	public boolean removeReward(String key) {
		return false;
	}

	@Override
	public double getProgressChance() {
		return 0;
	}

	@Override
	public boolean setProgressChance(double progressChance) {
		return false;
	}

	@Override
	public int getMaxProgress() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean setMaxProgress(int maxProgress) {
		return false;
	}

	@Override
	public TaskType<QuestPlayer> getType() {
		return null;
	}

	@Override
	public BarStyle getBossBarStyle() {
		return BarStyle.SOLID;
	}

	@Override
	public BarColor getBossBarColor() {
		return BarColor.BLUE;
	}

	@Override
	public boolean setBossBarStyle(BarStyle barStyle) {
		return false;
	}

	@Override
	public boolean setBossBarColor(BarColor barColor) {
		return false;
	}

	@Override
	public boolean showBossBar() {
		return false;
	}

	@Override
	public void setShowBossBar(Boolean value) {
	}

	@Override
	public String getRawUnstartedDescription() {
		return null;
	}

	@Override
	public String getRawProgressDescription() {
		return null;
	}

	@Override
	public String getTypeName() {
		return "error";
	}

	@Override
	public boolean setUnstartedDescription(String desc) {
		return false;
	}

	@Override
	public boolean setProgressDescription(String desc) {
		return false;
	}
}
