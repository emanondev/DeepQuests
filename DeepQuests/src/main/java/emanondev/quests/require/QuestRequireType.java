package emanondev.quests.require;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.quest.Quest;

public interface QuestRequireType {
	
	public QuestRequire getRequireInstance(MemorySection memorySection, Quest q);
	public String getKey();
	public Material getGuiItemMaterial();
	public List<String> getDescription();
}
