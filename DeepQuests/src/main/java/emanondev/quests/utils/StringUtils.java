package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Language;
import emanondev.quests.hooks.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class StringUtils {
	public static String applyHolder(String target, String holder,String replacer){
		if (target==null)
			return null;
	    return target.replace(holder, replacer);
	}
	public static ArrayList<String> applyHolder(List<String> target, String holder,String replacer){
		if (target==null)
			return null;
		ArrayList<String> result = new ArrayList<String>(target);
	    for(int i = 0; i < result.size(); i++)
	    	result.set(i, applyHolder(result.get(i), holder, replacer));
	    return result;
	}

	public static ArrayList<String> convertList(Player p,List<String> target, String... stuffs){
		if (target == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
	    for (String str : target) {
	      list.add(convertText(p, str, stuffs));
	    }
	    return list;
	}
	public static String convertText(Player p,String target, String... stuffs){
		if (target==null)
			return null;
		if (stuffs!=null && stuffs.length%2!=0)
			throw new IllegalArgumentException("holder withouth replacer");
		if (stuffs!=null)
			for (int i = 0 ; i< stuffs.length; i+=2) {
				target = target.replace(stuffs[i],stuffs[i+1]);
			}
	    if (p!=null && (Hooks.isPAPIEnabled()) && (target.contains("%"))) {
	    	target = PlaceholderAPI.setPlaceholders(p, target);
	    }
	    return target;
	}

	public static ArrayList<String> fixColorsAndHolders(List<String> l,String... stuffs){
	    if (l == null)
	    	return null;
	    ArrayList<String> list = new ArrayList<String>();
	    for (String str : l) {
	    	list.add(fixColorsAndHolders(str,stuffs));
	    }
	    return list;
	}
	
	public static String fixColorsAndHolders(String s,String... stuffs){
	    if (s == null) 
	    	return null;
	    if (stuffs!=null && stuffs.length%2!=0)
			throw new IllegalArgumentException("holder withouth replacer");
		if (stuffs!=null)
			for (int i = 0 ; i< stuffs.length; i+=2) {
				s = s.replace(stuffs[i],stuffs[i+1]);
			}
	    return ChatColor.translateAlternateColorCodes('&', s);
	}
	  
	public static String withoutColor(String s){
	    if (s == null) 
	    	return null;
	    s = fixColorsAndHolders(s);
	    return ChatColor.stripColor(s);
	}
	public static String revertColors(String s){
	    if (s == null) 
	    	return null;
	    return s.replace("§","&");
	}
	public static String getStringCooldown(long cooldown) {
		cooldown = cooldown/1000;
		StringBuilder result = new StringBuilder("");
		if (cooldown>=CooldownFormat.WEEK.seconds) {//week
			int val = (int) (cooldown/CooldownFormat.WEEK.seconds);
			if (val>1)
				result.append(val+" "+CooldownFormat.WEEK.multi);
			else
				result.append(val+" "+CooldownFormat.WEEK.single);
			val =  (int) (cooldown%CooldownFormat.WEEK.seconds/CooldownFormat.DAY.seconds);
			if (val>1)
				result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.DAY.multi);
			else
				if (val==1)
					result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.DAY.single);
			return result.toString();
		}
		if (cooldown>=CooldownFormat.DAY.seconds) {//day
			int val = (int) (cooldown/CooldownFormat.DAY.seconds);
			if (val>1)
				result.append(val+" "+CooldownFormat.DAY.multi);
			else
				result.append(val+" "+CooldownFormat.DAY.single);
			val =  (int) (cooldown%CooldownFormat.DAY.seconds/CooldownFormat.HOUR.seconds);
			if (val>1)
				result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.HOUR.multi);
			else
				if (val==1)
					result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.HOUR.single);
			return result.toString();
		}
		if (cooldown>=CooldownFormat.HOUR.seconds) {//hour
			int val = (int) (cooldown/CooldownFormat.HOUR.seconds);
			if (val>1)
				result.append(val+" "+CooldownFormat.HOUR.multi);
			else
				result.append(val+" "+CooldownFormat.HOUR.single);
			val =  (int) (cooldown%CooldownFormat.HOUR.seconds/CooldownFormat.MINUTE.seconds);
			if (val>1)
				result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.MINUTE.multi);
			else
				if (val==1)
					result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.MINUTE.single);
			return result.toString();
		}
		if (cooldown>=CooldownFormat.MINUTE.seconds) {//minute
			int val = (int) (cooldown/CooldownFormat.MINUTE.seconds);
			if (val>1)
				result.append(val+" "+CooldownFormat.MINUTE.multi);
			else
				result.append(val+" "+CooldownFormat.MINUTE.single);
			val =  (int) (cooldown%CooldownFormat.MINUTE.seconds/CooldownFormat.SECOND.seconds);
			if (val>1)
				result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.SECOND.multi);
			else
				if (val==1)
					result.append(" "+Language.Conjunctions.getConjAnd()+" "+ val+" "+CooldownFormat.SECOND.single);
			return result.toString();
		}
		int val = (int) (cooldown/CooldownFormat.SECOND.seconds);
		if (val>1)
			result.append(val+" "+CooldownFormat.SECOND.multi);
		else
			result.append(val+" "+CooldownFormat.SECOND.single);
		return result.toString();
	}
	public enum CooldownFormat{
		WEEK(604800),
		DAY(86400),
		HOUR(3600),
		MINUTE(60),
		SECOND(1);
		public final String multi;
		public final String single;
		public final long seconds;
		private CooldownFormat(long seconds) {
			this.seconds = seconds;
			this.multi = Language.Time.getMultiTime(this);
			this.single = Language.Time.getSingleTime(this);
		}
		
	}
	
	/*
	public static void setDescription(ItemStack item,ArrayList<String> desc,String... holders) {
		if (item == null||desc == null)
			throw new NullPointerException();
		desc = fixColorsAndHolders(desc,holders);
		setDescriptionNoColors(item,desc);
	}*/
	public static void setDescription(ItemStack item,ArrayList<String> desc,String... holders) {
		setDescription(item, null, desc, holders);
	}
	
	public static void setDescription(ItemStack item,Player p,ArrayList<String> desc,String... holders) {
		if (item == null||desc == null)
			throw new NullPointerException();
		setDescriptionNoColors(item, p, fixColorsAndHolders(desc,holders));
	}
	public static void setDescriptionNoColors(ItemStack item,ArrayList<String> desc,String... holders) {
		setDescriptionNoColors(item, null, desc, holders);
	}
	public static void setDescriptionNoColors(ItemStack item,Player p,ArrayList<String> desc,String... holders) {
		if (item == null||desc == null)
			throw new NullPointerException();
		if (holders!=null)
			desc = convertList(p,desc,holders);
		else
			desc = convertList(p,desc);
		
		ItemMeta meta = item.getItemMeta();
		if (desc.size()>0)
			meta.setDisplayName(desc.remove(0));
		else
			meta.setDisplayName("");
		meta.setLore(desc);
		item.setItemMeta(meta);
	}
	/*
	public static void setDescriptionNoColors(ItemStack item,ArrayList<String> desc,String... holders) {
		if (item == null||desc == null)
			throw new NullPointerException();
		if (holders!=null)
			convertList(null,desc,holders);
		
		ItemMeta meta = item.getItemMeta();
		if (desc.size()>0)
			meta.setDisplayName(desc.remove(0));
		else
			meta.setDisplayName("");
		meta.setLore(desc);
		item.setItemMeta(meta);
	}*/
	public static ArrayList<String> getDescription(ItemStack item){
		if (item == null)
			throw new NullPointerException();
		ArrayList<String> desc = new ArrayList<String>();
		if (!item.hasItemMeta())
			return desc;
		ItemMeta meta = item.getItemMeta();
		if (meta.hasDisplayName())
			desc.add(meta.getDisplayName());
		else
			if (!meta.hasLore())
				return desc;
			else
				desc.add("");
		if (meta.hasLore())
			desc.addAll(meta.getLore());
		return desc;
	}
	public static List<String> getStringList(MemorySection m, String path) {
		if (m.isString(path))
			Arrays.asList(m.getString(path));
		if (!m.isList(path))
			return null;
		else 
			return m.getStringList(path);
	}
}
