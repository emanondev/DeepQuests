package emanondev.quests;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import emanondev.quests.interfaces.storage.IConfig;

public class Translations {
	private static EnumMap<EntityType,String> entities = loadEntities();
	private static EnumMap<Material,String> materials = loadMaterials();
	private static Map<Enchantment,String> enchantments = loadEnchantments();
	private static Map<String,String> actions = new HashMap<>();
	private static Map<String,String> regions = new HashMap<>();
	private static Map<String,String> worlds = new HashMap<>();
	
	private static EnumMap<Material, String> loadMaterials() {
		EnumMap<Material, String> map = new EnumMap<>(Material.class);
		for (Material type:Material.values())
			map.put(type,read("materials."+type.toString().toLowerCase(),type.toString().toLowerCase()));
		return map;
	}
	private static EnumMap<EntityType, String> loadEntities() {
		EnumMap<EntityType, String> map = new EnumMap<>(EntityType.class);
		for (EntityType type:EntityType.values())
			map.put(type,read("entities."+type.toString().toLowerCase(),type.toString().toLowerCase()));
		return map;
	}
	private static Map<Enchantment, String> loadEnchantments() {
		HashMap<Enchantment, String> map = new HashMap<>();
		for (Enchantment ench:Enchantment.values())
			map.put(ench,read("enchants."+ench.getKey().getKey(),ench.getKey().getKey()));
		return map;
	}
	
	public static String translate(EntityType entity) {
		return entities.get(entity);
	}
	public static String translate(Material mat) {
		return materials.get(mat);
	}
	public static String trasnlate(Enchantment ench) {
		return enchantments.get(ench);
	}
	
	public static String translateAction(String actionName) {
		return getOrRead(actions,actionName,"actions",actionName);
	}
	public static String translateRegion(String regionName) {
		return getOrRead(regions,regionName,"regions",regionName);
	}
	public static String translate(World world) {
		return getOrRead(worlds,world.getName(),"worlds",world.getName());
	}
	
	
	private static String getOrRead(Map<String,String> map,String key,String pathNoKey,String defaultName) {
		if (map.containsKey(key))
			return map.get(key);
		String value = read(pathNoKey+"."+key,defaultName);
		map.put(key,value);
		return value;
	}
	private static String read(String path,String defaultName) {
		if (config.getString(path)!=null)
			return config.getString(path);
		config.set(path, defaultName);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defaultName;
	}
	private static IConfig config = IConfig.loadYamlConfiguration(new File(Quests.get().getDataFolder(),"translations.yml"));
}
