package emanondev.quests.configuration;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import emanondev.quests.utils.Savable;

public interface ConfigSection extends ConfigurationSection,Savable {

	/**
	 * 
	 * @param path
	 * @return true if the object is a Number
	 */
	public boolean isNumber(String path);

	@Override
	public ConfigSection createSection(String path);

	@Override
	public ConfigSection createSection(String path, Map<?, ?> value);

	@Deprecated
	@Override
	public ConfigSection getConfigurationSection(String path);

	@Override
	public ConfigSection getParent();

	@Override
	public ConfigSection getDefaultSection();
	
	public ConfigSection loadSection(String path);

}
