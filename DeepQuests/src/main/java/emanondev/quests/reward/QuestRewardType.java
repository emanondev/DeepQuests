package emanondev.quests.reward;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.quest.Quest;

public abstract interface QuestRewardType {
	public Reward getRewardInstance(MemorySection m,Quest q);
	public Material getGuiItemMaterial();
	public List<String> getDescription();
	public String getKey();
}
