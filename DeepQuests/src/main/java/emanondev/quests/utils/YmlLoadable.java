package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.TextEditorButton;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.EditorButtonFactory;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import emanondev.quests.gui.CustomMultiPageGui;

public abstract class YmlLoadable implements Savable,WithGui {
	public static final String PATH_DISPLAY_NAME = "name";
	public static final String PATH_WORLDS_LIST = "worlds.list";
	public static final String PATH_WORLDS_IS_BLACKLIST = "worlds.is-blacklist";
	
	
	private String displayName;
	private final String name;
	private final HashSet<String> worlds = new HashSet<String>();
	private boolean useWorldsAsBlacklist;
	
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
		this.useWorldsAsBlacklist = loadUseWorldsAsBlacklist(m);
		addToEditor(new DisplayNameEditorButtonFactory());
		addToEditor(new WorldsEditorButtonFactory());
	}
	public void setWorldsList(Collection<String> coll) {
		if (coll == null) {
			this.worlds.clear();
			section.set(PATH_WORLDS_LIST, new ArrayList<String>());
			this.setDirty(true);
			return;
		}
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
	public boolean removeWorldFromWorldList(String name) {
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
	public boolean isWorldListBlacklist() {
		return this.useWorldsAsBlacklist;
	}
	public boolean setWorldListBlacklist(boolean isBlacklist) {
		if (this.useWorldsAsBlacklist == isBlacklist)
			return false;
		this.useWorldsAsBlacklist = isBlacklist;
		this.setDirty(true);
		return true;
	}
	
	public boolean isWorldAllowed(World w) {
		if (w == null)
			throw new NullPointerException();
		if (useWorldsAsBlacklist == true)
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
		name = StringUtils.fixColorsAndHolders(name);
		if (name.equals(displayName))
			return false;
		this.displayName = name;
		section.set(PATH_DISPLAY_NAME,StringUtils.revertColors(displayName));
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
	
	private boolean loadUseWorldsAsBlacklist(MemorySection m) {
		if (worlds.isEmpty())
			return true;
		if (!m.isBoolean(PATH_WORLDS_IS_BLACKLIST)) {
			boolean v = getUseWorldsAsBlacklistDefault();
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
	protected abstract boolean getUseWorldsAsBlacklistDefault();
	
	
	private ArrayList<EditorButtonFactory> tools = new ArrayList<EditorButtonFactory>();
	public void openEditorGui(Player p){
		openEditorGui(p,null);
	}
	public void openEditorGui(Player p,CustomGui previusHolder){
		p.openInventory(new EditorGui(p,this,previusHolder,tools).getInventory());
	}
	public void addToEditor(EditorButtonFactory item) {
		if (item!=null)
			tools.add(item);
	}

	private final static BaseComponent[] changeTitleDesc = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command and the old display name\n\n"+
			ChatColor.GOLD+"Change override old title writing new title\n"+
			ChatColor.YELLOW+"/questtext <new display name>"
			).create();
	private class DisplayNameEditorButtonFactory implements EditorButtonFactory {
		private class DisplayNameEditorButton extends TextEditorButton {
			private ItemStack item = new ItemStack(Material.PAPER);
			public DisplayNameEditorButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lDisplayName editor"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to edit name"));
				lore.add(StringUtils.fixColorsAndHolders("&aDisplayName &r'")
							+getDisplayName()+StringUtils.fixColorsAndHolders("&r'"));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, StringUtils.revertColors(getDisplayName()), changeTitleDesc);
			}
			@Override
			public void onReicevedText(String text) {
				if (text == null)
					text = "";
				if (setDisplayName(text)) {
					update();
					getParent().reloadInventory();
					((EditorGui) getParent()).updateTitle();
				}
				else
					getOwner().sendMessage(StringUtils.fixColorsAndHolders(
							"&cSelected name was not a valid name"));
				
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new DisplayNameEditorButton(parent);
		}
	}
	private class WorldsEditorButtonFactory implements EditorButtonFactory {
		private class WorldsEditorButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.COMPASS);
			public WorldsEditorButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lWorlds editor"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&6Click to edit");
				if (getWorldsList().isEmpty())
					lore.add("&aNo worlds restrictions are set");
				else {
					if (isWorldListBlacklist()) {
						lore.add("&6All listed worlds are &cdisabled");
						for (String world : getWorldsList())
							lore.add(" &6- &c"+world);
					}
					else {
						lore.add("&6All listed worlds are &aenabled");
						for (String world : getWorldsList())
							lore.add(" &6- &a"+world);
					}
				}
				meta.setLore(StringUtils.fixColorsAndHolders(lore));
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new WorldsEditorGui(clicker,(EditorGui) getParent()).getInventory());
			}
			private class WorldsEditorGui extends CustomMultiPageGui<CustomButton> {
				public WorldsEditorGui(Player p, EditorGui previusHolder) {
					super(p,previusHolder, 6,1);
					HashSet<String> set = new HashSet<String>();
					set.addAll(getWorldsList());
					for (World world : Bukkit.getWorlds())
						set.add(world.getName());
					for (String world : set) {
						addButton(new WorldButton(this,world));
					}
					this.setFromEndCloseButtonPosition(8);
					this.setTitle(null, StringUtils.fixColorsAndHolders("&8Restricted Worlds Editor"));
					reloadInventory();
				}
				public void reloadInventory() {
					getInventory().setItem(size()-1,blacklistButton.getItem());
					super.reloadInventory();
				}
				@Override
				public void onSlotClick(Player clicker,int slot,ClickType click) {
					if (slot==size()-1) {
						blacklistButton.onClick(clicker,click);
						return;
					}
					super.onSlotClick(clicker, slot, click);
				}
				private BlacklistButton blacklistButton = new BlacklistButton(this);
				@Override
				public void update() {
					blacklistButton.update();
					super.update();
				}
				private class BlacklistButton extends CustomButton {
					public BlacklistButton(WorldsEditorGui parent) {
						super(parent);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Click to Revert valid Worlds"));
						item.setItemMeta(meta);
						update();
					}
					@Override 
					public void update() {
						if (isWorldListBlacklist()) {
							this.item.setDurability((short) 15);
							ItemMeta meta = item.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(StringUtils.fixColorsAndHolders("&7(Now Blacklist)"));
							item.setItemMeta(meta);
						}
						else {
							this.item.setDurability((short) 0);
							ItemMeta meta = item.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(StringUtils.fixColorsAndHolders("&7(Now Whitelist)"));
							item.setItemMeta(meta);
						}
					}
					private ItemStack item = new ItemStack(Material.WOOL);

					@Override
					public ItemStack getItem() {
						return item;
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						setWorldListBlacklist(!isWorldListBlacklist());
						update();
						getParent().update();
					}
				}
				private class WorldButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.COMPASS);
					private final String worldName;
					public WorldButton(WorldsEditorGui parent,String world) {
						super(parent);
						this.worldName = world;
						update();
					}
					public void update() {
						ItemMeta meta = this.item.getItemMeta();
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lWorld: '"+worldName+"'"));
						ArrayList<String> lore = new ArrayList<String>();
						if (getWorldsList().contains(worldName)) {
							if (isWorldListBlacklist())
								lore.add(StringUtils.fixColorsAndHolders("&6This world is &cUnallowed"));
							else
								lore.add(StringUtils.fixColorsAndHolders("&6This world is &aAllowed"));
							lore.add(StringUtils.fixColorsAndHolders("&7(list contains this world)"));
						}
						else {
							if (!isWorldListBlacklist())
								lore.add(StringUtils.fixColorsAndHolders("&6This world is &cUnallowed"));
							else
								lore.add(StringUtils.fixColorsAndHolders("&6This world is &aAllowed"));
							lore.add(StringUtils.fixColorsAndHolders("&7(list don't contains this world)"));
						}
						meta.setLore(lore);
						item.setItemMeta(meta);
					}
					
					@Override
					public ItemStack getItem() {
						return item;
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						if (getWorldsList().contains(worldName))
							removeWorldFromWorldList(worldName);
						else
							addWorldToWorldList(worldName);
						update();
						getParent().reloadInventory();
					}
				}
			}
		}
		@Override
		public WorldsEditorButton getCustomButton(CustomGui parent) {
			return new WorldsEditorButton(parent);
		}
	}
	
	
	
	

}
