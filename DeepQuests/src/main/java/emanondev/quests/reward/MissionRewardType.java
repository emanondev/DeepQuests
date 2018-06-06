package emanondev.quests.reward;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;

public abstract interface MissionRewardType {
	public MissionReward getRewardInstance(MemorySection m,Mission mission);
	public Material getGuiItemMaterial();
	public List<String> getDescription();
	public String getKey();
}
