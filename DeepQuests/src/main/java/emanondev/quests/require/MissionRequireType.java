package emanondev.quests.require;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;

public interface MissionRequireType {

	public MissionRequire getRequireInstance(MemorySection memorySection, Mission m);
	public String getKey();
	public Material getGuiItemMaterial();
	public List<String> getDescription();
}
