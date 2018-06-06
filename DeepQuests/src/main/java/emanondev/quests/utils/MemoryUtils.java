package emanondev.quests.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Defaults;

public class MemoryUtils {
	
	/**
	 * 
	 * @param m
	 * @param path
	 * @return if target is a string return a list with that string as element<br>
	 * if target is null or list is empty returns null<br>
	 * else return the list
	 * 			
	 */
	public static List<String> getStringList(MemorySection m,String path){
		if (m==null)
			return null;
		//List<String> list;
		if (m.isString(path)) {
			return Arrays.asList(m.getString(path));
			
			/*
			String res = m.getString(path);
			if (res==null)
				return null;
			list = new ArrayList<String>();
			list.add(res);
			return list;*/
		}
		if (m.isList(path))
			return m.getStringList(path);
		return null;
		/*
		if (list==null || list.isEmpty())
			return null;
		return list;
		*/
	}
	

	/**
	 * format for 1.13+ should be:
	 * material<br>material:damage<br>material:damage:bool<br><br>
	 * format for 1.12- should be:
	 * material<br>material:data<br>material:data:damage<br>material:data:damage:bool<br><br>
	 * 
	 * where bool value is true if the item should be unbreakable
	 * @return
	 */
	public static ItemStack getGuiItem(String tempItem) {
		Material m = null;
		short damage;
		boolean unbreakable;
		String[] values = tempItem.split(":");
		
		if (values.length == 0)
			throw new IllegalArgumentException();
		try {
			if (values.length>=1) {
				m = Material.valueOf(values[0].toUpperCase());
			}
		} catch(Exception e) {
			throw new IllegalArgumentException("'"+values[0]+"' is not a valid material type");
		}
		
		if (isPre113) {
			byte data;
			if (values.length == 2) {
				if (values[1]==null || values[1].isEmpty())
					data = 0;
				else
					try {
						data = Byte.valueOf(values[1]);
					} catch(Exception e) {
						throw new IllegalArgumentException("'"+values[1]+", is not a valid data value");
					}
			}
			else
				data = 0;
			
			if (values.length >= 3) {
				if (values[2]==null || values[2].isEmpty())
					damage = 0;
				else
					try {
						damage = Short.valueOf(values[2]);
						if (damage < 0)
							throw new IllegalArgumentException();
					} catch(Exception e) {
						throw new IllegalArgumentException("'"+values[2]+", is not a valid damage value");
					}
			}
			else
				damage = 0;
			
			if (values.length >= 4) {
				if (values[3]==null || values[3].isEmpty())
					unbreakable = getDefaultUnbreakable();
				else {
					if (values[3].equalsIgnoreCase("true"))
						unbreakable = true;
					else
						if (values[3].equalsIgnoreCase("false"))
							unbreakable = false;
						else
							throw new IllegalArgumentException("'"+values[3]+", is not a valid boolean value");
				}
			}
			else
				unbreakable = getDefaultUnbreakable();
			
			return ItemStackUtility.craftBase(m, damage, data, unbreakable);
		}
		//if version is post 1.13
		
		if (values.length >= 2) {
			if (values[1]==null || values[1].isEmpty())
				damage = 0;
			else
				try {
					damage = Short.valueOf(values[1]);
					if (damage < 0)
						throw new IllegalArgumentException();
				} catch(Exception e) {
					throw new IllegalArgumentException("'"+values[1]+", is not a valid damage value");
				}
		}
		else
			damage = 0;
		
		if (values.length >= 3) {
			if (values[3]==null || values[2].isEmpty())
				unbreakable = getDefaultUnbreakable();
			else {
				if (values[2].equalsIgnoreCase("true"))
					unbreakable = true;
				else
					if (values[2].equalsIgnoreCase("false"))
						unbreakable = false;
					else
						throw new IllegalArgumentException("'"+values[2]+", is not a valid boolean value");
			}
		}
		else
			unbreakable = getDefaultUnbreakable();
		return ItemStackUtility.craftBase(m, damage, unbreakable);
	}
	private static boolean getDefaultUnbreakable() {
		return Defaults.shouldGuiItemsBeUnbreakable();
	}
	
	public static final boolean isPre113 = isPre113();
	private static boolean isPre113() {
		String txt = Bukkit.getServer().getClass().getPackage().getName();
		txt = txt.substring(txt.lastIndexOf(".") + 1).toUpperCase();
		switch (txt) {
		case "V1_8_R1":
		case "V1_8_R2":
		case "V1_8_R3":
		case "V1_9_R1":
		case "V1_9_R2":
		case "V1_10_R1":
		case "V1_11_R1":
		case "V1_12_R1":
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param tempStack
	 * @return return the valid string to represent the intem on config
	 */
	@SuppressWarnings("deprecation")
	public static String getGuiItemString(ItemStack tempStack) {
		StringBuilder text = new StringBuilder(tempStack.getType().toString());
		if (isPre113) {

			if (tempStack.hasItemMeta()&&tempStack.getItemMeta().isUnbreakable()) {
				text.append(":"+String.valueOf(tempStack.getData().getData())
					+":"+String.valueOf(tempStack.getDurability())+":true");
				return text.toString();
			}
			
			if (tempStack.getDurability()!=0) {
				text.append(":"+String.valueOf(tempStack.getData().getData())
						+":"+String.valueOf(tempStack.getDurability()));
				return text.toString();
			}
			if (tempStack.getData().getData()!=0) {
				text.append(":"+String.valueOf(tempStack.getData().getData()));
				return text.toString();
			}
			return text.toString();
		}
		//post 1.13
		if (tempStack.hasItemMeta()&&tempStack.getItemMeta().isUnbreakable()) {
			text.append(":"+String.valueOf(tempStack.getDurability())+":true");
			return text.toString();
		}
		if (tempStack.getDurability()!=0) {
			text.append(":"+String.valueOf(tempStack.getDurability()));
			return text.toString();
		}
		return text.toString();
	}
	

}
