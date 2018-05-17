package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;

public abstract class YmlLoadable implements Savable {
	public static final String PATH_DISPLAY_NAME = "name";
	public static final String PATH_WORLDS_LIST = "worlds.list";
	public static final String PATH_WORLDS_IS_BLACKLIST = "worlds.is-blacklist";
	
	
	private String displayName;
	private final String name;
	private final HashSet<String> worlds = new HashSet<String>();
	private boolean useWorldsAsBlackList;
	
	private final MemorySection section;
	protected MemorySection getSection() {
		return section;
	}
	
	protected boolean dirty = false;
	public boolean isDirty(){
		return dirty;
	}
	public void setDirty(boolean b) {
		dirty = b;
	}
	
	
	public YmlLoadable(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		this.section = m;
		this.name = loadName(m).toLowerCase();
		this.displayName = loadDisplayName(m);
		this.worlds.addAll(loadWorlds(m));
		this.useWorldsAsBlackList = loadUseWorldsAsBlackList(m);
		
	}
	public void setWorldsList(Collection<String> coll) {
		if (coll == null)
			return;
		this.worlds.clear();
		this.worlds.addAll(coll);
		section.set(PATH_WORLDS_LIST, new ArrayList<String>(this.worlds));
		this.setDirty(true);
	}
	public boolean addWorldToWorldList(String name) {
		if (name == null || name.isEmpty() || this.worlds.contains(name))
			return false;
		this.worlds.add(name);
		section.set(PATH_WORLDS_LIST, new ArrayList<String>(this.worlds));
		this.setDirty(true);
		return true;
	}
	public boolean removeWorldToWorldList(String name) {
		if (name == null || name.isEmpty() || !this.worlds.contains(name))
			return false;
		this.worlds.remove(name);
		section.set(PATH_WORLDS_LIST, new ArrayList<String>(this.worlds));
		this.setDirty(true);
		return true;
	}
	public Set<String> getWorldsList(){
		return Collections.unmodifiableSet(this.worlds);
	}
	public boolean isWorldListBlackList() {
		return this.useWorldsAsBlackList;
	}
	public void setWorldListBlackList(boolean isBlackList) {
		this.useWorldsAsBlackList = isBlackList;
	}
	
	public boolean isWorldAllowed(World w) {
		if (w == null)
			throw new NullPointerException();
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
	public boolean setDisplayName(String name) {
		if (name==null)
			return false;
		if (name.equals(displayName))
			return false;
		this.displayName = name;
		setDirty(true);
		return true;
	}
	
	
	
	
	
	
	private HashSet<String> loadWorlds(MemorySection m) {
		HashSet<String> worlds = new HashSet<String>();
		List<String> tempList = MemoryUtils.getStringList(m, PATH_WORLDS_LIST);
		
		if (tempList==null||tempList.isEmpty()) {
			if (shouldWorldsAutogen() == true && !m.isList(PATH_WORLDS_LIST))
				tempList = getWorldsListDefault();
			
			if (tempList==null || tempList.isEmpty()) {
				return worlds;
			}
			else {
				if (tempList!=null)
					worlds.addAll(tempList);
				ArrayList<String> values = new ArrayList<String>(worlds);
				m.set(PATH_WORLDS_LIST,values);
				dirty = true;
			}
		}
		else {
			if (tempList!=null)
				worlds.addAll(tempList);
		}
		return worlds;
	}
	
	private boolean loadUseWorldsAsBlackList(MemorySection m) {
		if (worlds.isEmpty())
			return true;
		if (!m.isBoolean(PATH_WORLDS_IS_BLACKLIST)) {
			boolean v = getUseWorldsAsBlackListDefault();
			m.set(PATH_WORLDS_IS_BLACKLIST,v);
			dirty = true;
			return v;
		}
		return m.getBoolean(PATH_WORLDS_IS_BLACKLIST,true);
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
	private String loadDisplayName(MemorySection m) {
		String tempDisplayName = m.getString(PATH_DISPLAY_NAME);
		if (tempDisplayName == null) {
			tempDisplayName = name.replace("_", " ");
			
			if (shouldAutogenDisplayName()) {
				m.set(PATH_DISPLAY_NAME, tempDisplayName);
				dirty = true;
				return ChatColor.translateAlternateColorCodes('&', tempDisplayName);
			}
		}
		return ChatColor.translateAlternateColorCodes('&', tempDisplayName);
	}
	protected abstract boolean shouldAutogenDisplayName();
	
	
	

	protected abstract List<String> getWorldsListDefault();
	protected abstract boolean shouldWorldsAutogen();
	protected abstract boolean getUseWorldsAsBlackListDefault();

}
