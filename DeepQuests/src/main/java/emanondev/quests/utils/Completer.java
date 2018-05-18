package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;

public class Completer {
	public static final int MAX_COMPLETES = 75;
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param enumClass - the class of the enums
	 */
	public static void complete(List<String> l,String prefix,Class<? extends Enum<?>> enumClass){
		prefix = prefix.toUpperCase();
		Enum<?>[] list = enumClass.getEnumConstants();
		for (int i = 0,c = 0; i < list.length && c < MAX_COMPLETES; i++)
			if (list[i].toString().startsWith(prefix)){
				l.add(list[i].toString().toLowerCase());
				c++;
			}
		return;
	}
	
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param list - the list of possible results
	 */
	public static void complete(List<String> l,String prefix,List<String> list){
		String tempPrefix = prefix.toLowerCase();
		list.forEach((key)->{
			if (l.size()<=MAX_COMPLETES && key.toLowerCase().startsWith(tempPrefix)){
				l.add(key);
			}
		});
		return;
	}
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param list - the list of possible results
	 */
	public static void complete(List<String> l,String prefix,String... list){
		prefix = prefix.toLowerCase();
		for (int i = 0,c = 0; i < list.length && c < MAX_COMPLETES; i++)
			if (list[i].toLowerCase().startsWith(prefix)){
				l.add(list[i]);
				c++;
			}
		return;
	}
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param coll - the list of possible results
	 */
	public static void complete(List<String> l,String prefix,Collection<String> coll){
		String tempPrefix = prefix.toLowerCase();
		coll.forEach((key)->{
			if (l.size()<=MAX_COMPLETES && key.toLowerCase().startsWith(tempPrefix)){
				l.add(key);
			}
		});
		return;
	}
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param quests - the list of possible results
	 */
	public static void completeQuests(List<String> l,String prefix,Collection<Quest> quests){
		String tempPrefix = prefix.toLowerCase();
		quests.forEach((quest)->{
			if (l.size()<=MAX_COMPLETES && 
					quest.getNameID().toLowerCase().startsWith(tempPrefix)){
				l.add(quest.getNameID());
			}
		});
		return;
	}
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param missions - the list of possible results
	 */
	public static void completeMissions(List<String> l,String prefix,Collection<Mission> missions){
		String tempPrefix = prefix.toLowerCase();
		missions.forEach((mission)->{
			if (l.size()<=MAX_COMPLETES && 
					mission.getNameID().toLowerCase().startsWith(tempPrefix)){
				l.add(mission.getNameID());
			}
		});
		return;
	}
	/**
	 * 
	 * @param l - where to put results
	 * @param prefix - the prefix to complete
	 * @param tasks - the list of possible results
	 */
	public static void completeTasks(List<String> l,String prefix,Collection<Task> tasks){
		String tempPrefix = prefix.toLowerCase();
		tasks.forEach((task)->{
			if (l.size()<=MAX_COMPLETES && 
					task.getNameID().toLowerCase().startsWith(tempPrefix)){
				l.add(task.getNameID());
			}
		});
		return;
	}
	
	/**
	 * @return 
	 * 
	 */
	public static void completePlayerNames(List<String> l,String prefix){
		String text = prefix.toLowerCase();
		Bukkit.getOnlinePlayers().forEach((p)->{
			if (l.size()<Completer.MAX_COMPLETES && p.getName().toLowerCase().startsWith(text))
				l.add(p.getName());
		});
		return;
	}

	public static void completeWorlds(ArrayList<String> l, String prefix, List<World> worlds) {
		String text = prefix.toLowerCase();
		worlds.forEach((w)->{
			if (l.size()<Completer.MAX_COMPLETES && w.getName().toLowerCase().startsWith(text))
				l.add(w.getName());
		});
		return;
		
	}
	private static final String[] boolValues = new String[]{"true","false"};
	public static void completeBoolean(ArrayList<String> l, String prefix) {
		complete(l,prefix,boolValues);
	}
}
