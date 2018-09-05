package emanondev.quests.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class YMLSection implements ConfigSection {

	private final YMLConfig parent;
	final MemorySection section;

	public YMLSection(YMLConfig parent, MemorySection section) {
		if (parent == null || section == null)
			throw new NullPointerException();
		this.parent = parent;
		this.section = section;
	}

	public boolean isNumber(String path) {
		Object input = get(path);
		return ((input instanceof Integer)) || ((input instanceof Byte)) || ((input instanceof Short))
				|| ((input instanceof Double)) || ((input instanceof Long)) || ((input instanceof Float));
	}

	@Override
	public ConfigSection getConfigurationSection(String path) {
		ConfigurationSection sect = section.getConfigurationSection(path);
		if (sect==null)
			return null;
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) sect);
		throw new IllegalArgumentException("unexpected error");
	}

	@Override
	public List<?> getList(String path) {
		return section.getList(path);
	}

	@Override
	public List<?> getList(String path, List<?> def) {
		return section.getList(path, def);
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
			List<String> l = section.getStringList(path);
			if (l instanceof ArrayList)
				return (ArrayList<String>) l;
			else
				return new ArrayList<String>(l);
		}
		return def;
	}

	@Override
	public ConfigSection createSection(String path) {
		ConfigurationSection sect = section.createSection(path);
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) sect);
		throw new IllegalArgumentException();
	}

	@Override
	public ConfigSection createSection(String path, Map<?, ?> value) {
		ConfigurationSection sect = section.createSection(path, value);
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) sect);
		throw new IllegalArgumentException();
	}

	@Override
	public ConfigSection getDefaultSection() {
		ConfigurationSection sect = section.getDefaultSection();
		if (sect instanceof ConfigSection)
			return (ConfigSection) sect;
		if (sect instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) sect);
		throw new IllegalArgumentException();
	}

	@Override
	public Object get(String path) {
		Object obj = section.get(path);
		if (obj instanceof ConfigSection)
			return obj;
		if (obj instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) obj);
		return obj;
	}

	@Override
	public Object get(String path, Object def) {
		Object obj = section.get(path, def);
		if (obj instanceof ConfigSection)
			return obj;
		if (obj instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) obj);
		return obj;
	}

	@Override
	public ItemStack getItemStack(String path) {
		return section.getItemStack(path);
	}

	@Override
	public ItemStack getItemStack(String path, ItemStack def) {
		return section.getItemStack(path, def);
	}

	@Override
	public List<Boolean> getBooleanList(String path) {
		return section.getBooleanList(path);
	}

	@Override
	public List<Byte> getByteList(String path) {
		return section.getByteList(path);
	}

	@Override
	public List<Double> getDoubleList(String path) {
		return section.getDoubleList(path);
	}

	@Override
	public List<Float> getFloatList(String path) {
		return section.getFloatList(path);
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		return section.getIntegerList(path);
	}

	@Override
	public List<Long> getLongList(String path) {
		return section.getLongList(path);
	}

	@Override
	public List<Map<?, ?>> getMapList(String path) {
		return section.getMapList(path);
	}

	@Override
	public ArrayList<Short> getShortList(String path) {
		List<Short> l = section.getShortList(path);
		if (l instanceof ArrayList)
			return (ArrayList<Short>) l;
		else
			return new ArrayList<Short>(l);
	}

	@Override
	public void addDefault(String path, Object value) {
		section.addDefault(path, value);
	}

	@Override
	public boolean contains(String path) {
		return section.contains(path);
	}

	@Override
	public boolean contains(String path, boolean ignoreDefault) {
		return section.contains(path, ignoreDefault);
	}

	@Override
	public boolean getBoolean(String path) {
		return section.getBoolean(path);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return section.getBoolean(path, def);
	}

	@Override
	public List<Character> getCharacterList(String path) {
		return section.getCharacterList(path);
	}

	@Override
	public Color getColor(String path) {
		return section.getColor(path);
	}

	@Override
	public Color getColor(String path, Color def) {
		return section.getColor(path, def);
	}

	@Override
	public String getCurrentPath() {
		return section.getCurrentPath();
	}

	@Override
	public double getDouble(String path) {
		return section.getDouble(path);
	}

	@Override
	public double getDouble(String path, double def) {
		return section.getDouble(path, def);
	}

	@Override
	public int getInt(String path) {
		return section.getInt(path);
	}

	@Override
	public int getInt(String path, int def) {
		return section.getInt(path, def);
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return section.getKeys(deep);
	}

	@Override
	public long getLong(String path) {
		return section.getLong(path);
	}

	@Override
	public long getLong(String path, long def) {
		return section.getLong(path, def);
	}

	@Override
	public String getName() {
		return section.getName();
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path) {
		return section.getOfflinePlayer(path);
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
		return section.getOfflinePlayer(path, def);
	}

	@Override
	public ConfigSection getParent() {
		if (section.getParent() instanceof ConfigSection)
			return (ConfigSection) section.getParent();
		else if (section.getParent() instanceof MemorySection)
			return new YMLSection(parent, (MemorySection) section.getParent());
		throw new IllegalArgumentException();
	}

	@Override
	public YMLConfig getRoot() {
		return parent;
	}

	@Override
	public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz) {
		return section.getSerializable(path, clazz);
	}

	@Override
	public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz, T def) {
		return section.getSerializable(path, clazz, def);
	}

	@Override
	public String getString(String path) {
		return section.getString(path);
	}

	@Override
	public String getString(String path, String def) {
		return section.getString(path, def);
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		return section.getValues(deep);
	}

	@Override
	public Vector getVector(String path) {
		return section.getVector(path);
	}

	@Override
	public Vector getVector(String path, Vector def) {
		return section.getVector(path, def);
	}

	@Override
	public boolean isBoolean(String path) {
		return section.isBoolean(path);
	}

	@Override
	public boolean isColor(String path) {
		return section.isColor(path);
	}

	@Override
	public boolean isConfigurationSection(String path) {
		return section.isConfigurationSection(path);
	}

	@Override
	public boolean isDouble(String path) {
		return section.isDouble(path);
	}

	@Override
	public boolean isInt(String path) {
		return section.isInt(path);
	}

	@Override
	public boolean isItemStack(String path) {
		return section.isItemStack(path);
	}

	@Override
	public boolean isList(String path) {
		return section.isList(path);
	}

	@Override
	public boolean isLong(String path) {
		return section.isLong(path);
	}

	@Override
	public boolean isOfflinePlayer(String path) {
		return section.isOfflinePlayer(path);
	}

	@Override
	public boolean isSet(String path) {
		return section.isSet(path);
	}

	@Override
	public boolean isString(String path) {
		return section.isString(path);
	}

	@Override
	public boolean isVector(String path) {
		return section.isVector(path);
	}

	@Override
	public void set(String path, Object value) {
		if (value instanceof YMLSection)
			value = ((YMLSection) value).section;
		section.set(path, value);
	}
	
	@Override
	public ConfigSection loadSection(String path) {
		ConfigSection value = getConfigurationSection(path);
		if (value==null) {
			return createSection(path);
		}
		if (value instanceof MemorySection) 
			return new YMLSection(parent,(MemorySection) value);
		if (value instanceof ConfigSection)
			return value;
		throw new IllegalArgumentException();
	}

}
