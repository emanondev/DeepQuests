package emanondev.quests.utils;

import java.util.List;

import org.bukkit.Material;

import emanondev.quests.configuration.ConfigSection;

public interface ApplyableType<T extends QuestComponent> {
	
	/**
	 * 
	 * @param section 
	 * @param parent
	 * @return an instance of the Applyable object
	 */
	public Applyable<T> getInstance(ConfigSection section, T parent);
	/**
	 * 
	 * @return the identifier for this Type
	 */
	public String getKey();
	/**
	 * 
	 * @return util for display purpose
	 */
	public Material getGuiItemMaterial();
	/**
	 * 
	 * @return util for display purpose <br>
	 * should descript what this object does
	 */
	public List<String> getDescription();
	
}