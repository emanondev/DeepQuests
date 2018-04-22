package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import emanondev.quests.hooks.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class StringUtils {

	public static String applyHolder(String target, String holder,String replacer){
		if (target==null)
			return null;
	    return target.replace(holder, replacer);
	}
	public static List<String> applyHolder(List<String> target, String holder,String replacer){
		if (target==null)
			return null;
		ArrayList<String> result = new ArrayList<String>(target);
	    for(int i = 0; i < result.size(); i++)
	    	result.set(i, applyHolder(result.get(i), holder, replacer));
	    return result;
	}

	public static List<String> convertList(Player p,List<String> target, String... stuffs){
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
	    if ((Hooks.isPAPIEnabled()) && (target.contains("%"))) {
	    	target = PlaceholderAPI.setPlaceholders(p, target);
	    }
	    return target;
	}

	public static List<String> fixColorsAndHolders(List<String> l,String... stuffs){
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
				result.append(", "+ val+" "+CooldownFormat.DAY.multi);
			else
				if (val==1)
					result.append(", "+ val+" "+CooldownFormat.DAY.single);
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
				result.append(", "+ val+" "+CooldownFormat.HOUR.multi);
			else
				if (val==1)
					result.append(", "+ val+" "+CooldownFormat.HOUR.single);
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
				result.append(", "+ val+" "+CooldownFormat.MINUTE.multi);
			else
				if (val==1)
					result.append(", "+ val+" "+CooldownFormat.MINUTE.single);
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
				result.append(", "+ val+" "+CooldownFormat.SECOND.multi);
			else
				if (val==1)
					result.append(", "+ val+" "+CooldownFormat.SECOND.single);
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
		WEEK(604800,"weeks","week"),
		DAY(86400,"days","day"),
		HOUR(3600,"hours","hour"),
		MINUTE(60,"minutes","minute"),
		SECOND(1,"seconds","second");
		public final String multi;
		public final String single;
		public final long seconds;
		private CooldownFormat(long seconds,String multi,String single) {
			this.seconds = seconds;
			this.multi = multi;
			this.single = single;
		}
		
	}
}
