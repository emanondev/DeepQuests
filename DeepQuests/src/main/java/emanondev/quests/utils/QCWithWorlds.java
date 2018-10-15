package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.CollectionElementsSelectorButton;
import emanondev.quests.newgui.gui.Gui;

public abstract class QCWithWorlds extends AQuestComponent implements QuestComponent {
	public static final String PATH_WORLDS_LIST = "worlds.list";
	public static final String PATH_WORLDS_IS_BLACKLIST = "worlds.is-blacklist";

	private final HashSet<String> worlds = new HashSet<String>();
	private boolean useWorldsAsBlacklist;

	public QCWithWorlds(ConfigSection m, Savable parent) {
		super(m, parent);
		this.worlds.addAll(loadWorlds());
		this.useWorldsAsBlacklist = loadUseWorldsAsBlacklist();
	}

	public void setWorldsList(Collection<String> coll) {
		if (coll == null) {
			this.worlds.clear();
			getSection().set(PATH_WORLDS_LIST, new ArrayList<String>());
			this.setDirty(true);
			return;
		}
		this.worlds.clear();
		this.worlds.addAll(coll);
		getSection().set(PATH_WORLDS_LIST, new ArrayList<String>(this.worlds));
		this.setDirty(true);
	}

	private boolean addWorldToWorldList(String name) {
		if (name == null || name.isEmpty() || this.worlds.contains(name))
			return false;
		this.worlds.add(name);
		getSection().set(PATH_WORLDS_LIST, new ArrayList<String>(this.worlds));
		this.setDirty(true);
		return true;
	}

	private boolean removeWorldFromWorldList(String name) {
		if (name == null || name.isEmpty() || !this.worlds.contains(name))
			return false;
		this.worlds.remove(name);
		getSection().set(PATH_WORLDS_LIST, new ArrayList<String>(this.worlds));
		this.setDirty(true);
		return true;
	}

	public boolean toggleWorldFromWorldList(World world) {
		if (world == null)
			throw new NullPointerException();
		if (isWorldAllowed(world))
			if (isWorldListBlacklist())
				return addWorldToWorldList(world.getName());
			else
				return removeWorldFromWorldList(world.getName());
		else if (!isWorldListBlacklist())
			return addWorldToWorldList(world.getName());
		else
			return removeWorldFromWorldList(world.getName());
	}

	public Set<String> getWorldsList() {
		return Collections.unmodifiableSet(this.worlds);
	}

	public Set<World> getWorldsSet() {
		Set<World> set = new HashSet<World>();
		for (String worldName : getWorldsList()) {
			World world = Bukkit.getServer().getWorld(worldName);
			if (world != null)
				set.add(world);
		}
		return set;
	}

	public boolean isWorldListBlacklist() {
		return this.useWorldsAsBlacklist;
	}

	public boolean setWorldListBlacklist(boolean isBlacklist) {
		if (this.useWorldsAsBlacklist == isBlacklist)
			return false;
		this.useWorldsAsBlacklist = isBlacklist;
		getSection().set(PATH_WORLDS_IS_BLACKLIST, useWorldsAsBlacklist);
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

	private HashSet<String> loadWorlds() {
		HashSet<String> worlds = new HashSet<String>();
		List<String> tempList = getSection().getStringList(PATH_WORLDS_LIST);

		if (tempList == null || tempList.isEmpty()) {
			if (shouldWorldsAutogen() == true && !getSection().isList(PATH_WORLDS_LIST))
				tempList = getWorldsListDefault();

			if (tempList == null || tempList.isEmpty()) {
				return worlds;
			} else {
				if (tempList != null)
					worlds.addAll(tempList);
				ArrayList<String> values = new ArrayList<String>(worlds);
				getSection().set(PATH_WORLDS_LIST, values);
				setDirtyLoad();
			}
		} else {
			if (tempList != null)
				worlds.addAll(tempList);
		}
		return worlds;
	}

	private boolean loadUseWorldsAsBlacklist() {
		if (worlds.isEmpty())
			return true;
		if (!getSection().isBoolean(PATH_WORLDS_IS_BLACKLIST)) {
			boolean v = getUseWorldsAsBlacklistDefault();
			getSection().set(PATH_WORLDS_IS_BLACKLIST, v);
			setDirtyLoad();
			return v;
		}
		return getSection().getBoolean(PATH_WORLDS_IS_BLACKLIST, true);
	}

	protected abstract List<String> getWorldsListDefault();

	protected abstract boolean shouldWorldsAutogen();

	protected abstract boolean getUseWorldsAsBlacklistDefault();

	protected class QCWithWorldsEditor extends QCEditor {

		public QCWithWorldsEditor(String title, Player p, Gui previusHolder) {
			super(title, p, previusHolder);
			this.putButton(7, new WorldsSelectorButton());
		}

		private class WorldsSelectorButton extends CollectionElementsSelectorButton<World> {

			public WorldsSelectorButton() {
				super("&6&lWorlds Restrictions Selector", new ItemStack(Material.COMPASS), QCWithWorldsEditor.this,
						Bukkit.getServer().getWorlds(), true);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lWorlds Restrictions Selector");
				desc.add("&6Click to edit");
				if (getWorldsList().isEmpty())
					desc.add("&7No worlds restrictions are set");
				else {
					if (isWorldListBlacklist()) {
						desc.add("&7All listed worlds are &cUnallowed");
						for (String world : getWorldsList())
							desc.add(" &7- &c" + world);
					} else {
						desc.add("&7All listed worlds are &aAllowed");
						for (String world : getWorldsList())
							desc.add(" &7- &a" + world);
					}
				}
				return desc;
			}

			@Override
			public List<String> getElementDescription(World world) {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6World: '&e&l" + world.getName() + "'");
				if (getWorldsList().contains(world.getName())) {
					if (isWorldListBlacklist()) {
						desc.add(StringUtils.fixColorsAndHolders("&6This world is &cUnallowed"));
					} else {
						desc.add(StringUtils.fixColorsAndHolders("&6This world is &aAllowed"));
					}
					desc.add(StringUtils.fixColorsAndHolders("&7(selected)"));
				} else {
					if (!isWorldListBlacklist()) {
						desc.add(StringUtils.fixColorsAndHolders("&6This world is &cUnallowed"));
					} else {
						desc.add(StringUtils.fixColorsAndHolders("&6This world is &aAllowed"));
					}
					desc.add(StringUtils.fixColorsAndHolders("&7(unselected)"));
				}
				return desc;
			}

			@Override
			public ItemStack getElementItem(World world) {
				return new ItemStack(Material.COMPASS);
			}

			@Override
			public boolean getIsWhitelist() {
				return !isWorldListBlacklist();
			}

			@Override
			public boolean onToggleElementRequest(World world) {
				return toggleWorldFromWorldList(world);
			}

			@Override
			public boolean onWhiteListChangeRequest(boolean isWhitelist) {
				return setWorldListBlacklist(!isWhitelist);
			}

			@Override
			public boolean currentCollectionContains(World element) {
				return worlds.contains(element.getName());
			}

		}

	}
}
