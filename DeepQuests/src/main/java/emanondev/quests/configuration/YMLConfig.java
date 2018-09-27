package emanondev.quests.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * Class to manage yml files
 */
public class YMLConfig extends YamlConfiguration implements ConfigSection {
	private JavaPlugin pl;
	private File file;

	/**
	 * 
	 * @param pl
	 * @param name
	 *            - file name, it may ends with ".yml" else ".yml" will be added<br>
	 *            to put the file in a sub folder use "/" in the name<br>
	 * 			example "myfolder/mysubfolder/filename.yml"
	 */
	public YMLConfig(JavaPlugin pl, String name) {
		this.pl = pl;
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("YAML file must have a name!");
		if (!name.endsWith(".yml"))
			name += ".yml";
		file = new File(pl.getDataFolder(), name);
		reload();
	}

	/**
	 * Reload config object in RAM to that of the file - useful if developer wants
	 * changes made to config yml to be brought in <br>
	 * 
	 * @return true if reload was successfull - false if configuration errors
	 *         occurred <br>
	 * 		<br>
	 *         if file do not exist an attemp to copy it from the plugin's jar is
	 *         made,<br>
	 *         if jar do not have that file a new blank file is created<br>
	 *         <br>
	 *         if loading the file InvalidConfigurationException is throwed the file
	 *         won't be overriden
	 */
	public boolean reload() {
		// boolean existed = file.exists();
		if (!file.exists()) {

			if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
				file.getParentFile().mkdirs();
			}
			if (pl.getResource(file.getName()) != null) {
				pl.saveResource(file.getName(), true); // Save the one from the JAR if possible
			} else {
				try {
					file.createNewFile();
				} // Create a blank file if there's not one to copy from the JAR
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			this.load(file);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pl.getResource(file.getName()) != null) { // Set up defaults in case their config is broked.
			InputStreamReader defConfigStream = null;
			try {
				defConfigStream = new InputStreamReader(pl.getResource(file.getName()), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			this.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
		}
		return true;
	}

	/**
	 * Save the config object in RAM to the file - overwrites any changes that the
	 * configurator has made to the file unless reload() has been called since
	 * 
	 * this just override the file on disk with his copy on the ram
	 */
	public void save() {
		try {
			this.save(file);
			setDirty(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the file of this yml configuration
	 */
	public File getFile() {
		return file;
	}

	public boolean isNumber(String path) {
		Object input = get(path);
		return ((input instanceof Integer)) || ((input instanceof Byte)) || ((input instanceof Short))
				|| ((input instanceof Double)) || ((input instanceof Long)) || ((input instanceof Float));
	}

	@Override
	public ConfigSection getConfigurationSection(String path) {
		ConfigurationSection sect = super.getConfigurationSection(path);
		if (sect==null)
			return null;
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(this, (MemorySection) sect);
		throw new IllegalArgumentException("unexpected error class "+sect.getClass().getCanonicalName());
	}

	/**
	 * = getStringList(path,null)
	 */
	@Override
	public ArrayList<String> getStringList(String path) {
		return getStringList(path, null);
	}

	/**
	 * 
	 * @param path
	 * @param def
	 *            - default value returned when no value is found
	 * @return if the object is a String return a list that contains the object if
	 *         the object is a list returns it (even empty list) else return def
	 */
	public ArrayList<String> getStringList(String path, ArrayList<String> def) {
		if (isString(path)) {
			String val = getString(path);
			ArrayList<String> list = new ArrayList<String>();
			list.add(val);
			return list;
		}
		if (isList(path)) {
			List<String> l = super.getStringList(path);
			if (l instanceof ArrayList)
				return (ArrayList<String>) l;
			else
				return new ArrayList<String>(l);
		}
		return def;
	}

	@Override
	public ConfigSection createSection(String path) {
		ConfigurationSection sect = super.createSection(path);
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(this, (MemorySection) sect);
		throw new IllegalArgumentException();
	}

	@Override
	public ConfigSection createSection(String path, Map<?, ?> value) {
		ConfigurationSection sect = super.createSection(path, value);
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(this, (MemorySection) sect);
		throw new IllegalArgumentException();
	}

	@Override
	public Object get(String path) {
		Object obj = super.get(path);
		if (obj instanceof ConfigSection)
			return obj;
		if (obj instanceof MemorySection)
			return new YMLSection(this, (MemorySection) obj);
		return obj;
	}

	@Override
	public Object get(String path, Object def) {
		Object obj = super.get(path, def);
		if (obj instanceof ConfigSection)
			return obj;
		if (obj instanceof MemorySection)
			return new YMLSection(this, (MemorySection) obj);
		return obj;
	}

	@Override
	public ConfigSection getParent() {
		if (super.getParent() instanceof ConfigSection)
			return (ConfigSection) super.getParent();
		else if (super.getParent() instanceof MemorySection)
			return new YMLSection(this, (MemorySection) super.getParent());
		throw new IllegalArgumentException();
	}

	@Override
	public ConfigSection getDefaultSection() {
		ConfigurationSection sect = super.getDefaultSection();
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(this, (MemorySection) sect);
		throw new IllegalArgumentException();
	}
	
	@Override
	public void set(String path, Object value) {
		if (value instanceof YMLSection)
			value = ((YMLSection) value).section;
		super.set(path, value);
	}
	

	@Override
	public ConfigSection loadSection(String path) {
		ConfigSection value = getConfigurationSection(path);
		if (value==null) {
			return createSection(path);
		}
		if (value instanceof MemorySection) 
			return new YMLSection(this,(MemorySection) value);
		if (value instanceof ConfigSection)
			return value;
		throw new IllegalArgumentException();
	}
	
	@Override
	public YMLConfig getRoot() {
		return this;
	}

	private boolean dirty = true;
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean value) {
		dirty = value;
	}
	
}