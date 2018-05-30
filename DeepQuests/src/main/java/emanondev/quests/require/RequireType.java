package emanondev.quests.require;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.utils.WithGui;

public interface RequireType extends MissionRequireType,QuestRequireType {
	
	public Require getRequireInstance(MemorySection memorySection, WithGui gui);
	public String getKey();
	public Material getGuiItemMaterial();
	public List<String> getDescription();
}
