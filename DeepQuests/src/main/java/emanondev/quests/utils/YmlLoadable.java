package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;

public abstract class YmlLoadable {
	static final protected String PATH_DISPLAY_NAME = "name";
	static final protected String PATH_WORLDS_LIST = "worlds.list";
	static final protected String PATH_WORLDS_IS_BLACKLIST = "worlds.is-blacklist";
	
	
	private String displayName;
	private final String name;
	private final HashSet<String> worlds;
	private final boolean useWorldsAsBlackList;
	
	protected abstract String getDisplayNameDefaultPrefix();
	protected boolean shouldSave;
	/**
	 * after loading/reloading return shouldSave <- true if a config.save() is suggested/required
	 * @return
	 */
	public boolean shouldSave(){
		return shouldSave;
	}
	
	
	public YmlLoadable(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		this.name = loadName(m);
		this.displayName = loadDisplayName(m);
		this.worlds = loadWorlds(m);
		this.useWorldsAsBlackList = loadUseWorldsAsBlackList(m);
	}
	
	/**
	 * @return the unique name
	 */
	private String loadName(MemorySection m) {
		String name = m.getName();
		if (name==null||name.isEmpty())
			throw new NullPointerException();
		return name;
	}
	/**
	 * @return the displayName
	 */
	protected String loadDisplayName(MemorySection m) {
		String tempDisplayName = m.getString(PATH_DISPLAY_NAME);
		if (tempDisplayName == null) {
			if (getDisplayNameDefaultPrefix()!=null)
				tempDisplayName = getDisplayNameDefaultPrefix()+name.replace("_", " ");
			else
				tempDisplayName = name.replace("_", " ");
			if (shouldAutogenDisplayName()) {
				m.set(PATH_DISPLAY_NAME, tempDisplayName);
				shouldSave = true;
				return ChatColor.translateAlternateColorCodes('&', tempDisplayName);
			}
		}
		return ChatColor.translateAlternateColorCodes('&', tempDisplayName);
	}
	protected abstract boolean shouldAutogenDisplayName();
	
	
	private boolean dirtyLoad = false;
	protected HashSet<String> loadWorlds(MemorySection m) {
		HashSet<String> worlds;
		List<String> tempList = MemoryUtils.getStringList(m, PATH_WORLDS_LIST);
		
		if (tempList==null||tempList.isEmpty()) {
			if (shouldWorldsAutogen() == true)
				tempList = getWorldsListDefault();
			
			if (tempList==null || tempList.isEmpty()) {
				worlds = null;
				//useWorldsAsBlackList = true;
			}
			else {
				worlds = new HashSet<String>();
				for (int i = 0; i< tempList.size(); i++)
					if (tempList.get(i)!=null && !tempList.get(i).isEmpty())
						worlds.add(tempList.get(i).toLowerCase());
				dirtyLoad = true;
				ArrayList<String> values = new ArrayList<String>(worlds);
				m.set(PATH_WORLDS_LIST,values);
				shouldSave = true;
			}
		}
		else {
			worlds = new HashSet<String>();
			for (int i = 0; i< tempList.size(); i++)
				if (tempList.get(i)!=null && !tempList.get(i).isEmpty())
					worlds.add(tempList.get(i).toLowerCase());
			//useWorldsAsBlackList = m.getBoolean(PATH_WORLDS_IS_BLACKLIST,true);
		}
		return worlds;
	}
	
	protected boolean loadUseWorldsAsBlackList(MemorySection m) {
		boolean useWorldsAsBlackList;
		if (worlds==null)
			useWorldsAsBlackList = true;
		if (dirtyLoad) {
			useWorldsAsBlackList = getUseWorldsAsBlackListDefault();
			m.set(PATH_WORLDS_IS_BLACKLIST,useWorldsAsBlackList);
			shouldSave = true;
		}
		else
			useWorldsAsBlackList = m.getBoolean(PATH_WORLDS_IS_BLACKLIST,true);
		return useWorldsAsBlackList;
	}


	protected abstract List<String> getWorldsListDefault();
	protected abstract boolean shouldWorldsAutogen();
	protected abstract boolean getUseWorldsAsBlackListDefault();
	
	public boolean isWorldAllowed(World w) {
		if (w == null)
			throw new NullPointerException();
		if (worlds == null)
			return true;
		if (useWorldsAsBlackList == true)
			return !worlds.contains(w.getName().toLowerCase());
		else
			return worlds.contains(w.getName().toLowerCase());
	}
	public String getNameID() {
		return name;
	}
	public String getDisplayName() {
		return displayName;
	}

}
