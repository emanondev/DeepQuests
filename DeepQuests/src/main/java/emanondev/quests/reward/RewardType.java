package emanondev.quests.reward;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.WithGui;

public interface RewardType extends MissionRewardType,QuestRewardType {
	public Reward getRewardInstance(MemorySection m,WithGui parent);
	public Material getGuiItemMaterial();
	public List<String> getDescription();
	public String getKey();
}
