package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.task.Task;
import emanondev.quests.utils.ItemBuilder;

public class RegionTaskInfo extends QCData {
	private final static String PATH_REGION_NAME = "region-name";
	private final static String PATH_REGION_NAME_CONTAINS = "region-name-contains";
	private String regionName;
	private String regionNameContains;

	public RegionTaskInfo(ConfigSection m, Task t) {
		super(m, t);
		regionName = m.getString(PATH_REGION_NAME, null);
		regionNameContains = m.getString(PATH_REGION_NAME_CONTAINS, null);
	}

	public String getRegionName() {
		return regionName;
	}

	public boolean setRegionName(String name) {
		if (name!=null && name.isEmpty())
			name = null;
		if (regionName == name || (regionName != null && regionName.equals(name)))
			return false;
		this.regionName = name;
		getSection().set(PATH_REGION_NAME, regionName);
		setDirty(true);
		return true;
	}

	public boolean isValidRegion(ProtectedRegion region) {
		if (regionName != null && regionName.equals(region.getId()))
			return true;
		if (regionNameContains != null && region.getId().contains(regionNameContains))
			return true;
		return false;
	}

	public Button getRegionSelectorButton(Gui parent) {
		return new RegionSelectorButton(parent);
	}

	private static Set<String> getRegionNames(Player p) {
		Set<String> regionNames = new LinkedHashSet<>();
		Set<String> sortedSet = new TreeSet<>();
		//WorldGuardPlugin wg = (WorldGuardPlugin) Quests.get().getServer().getPluginManager().getPlugin("WorldGuard");
		World userWorld = null;
		if (p!=null) {
			userWorld = p.getWorld();
			RegionManager rgManager = getRegionManager(userWorld);
			if (rgManager!=null)
				for (ProtectedRegion region : rgManager.getRegions().values())
					sortedSet.add(region.getId());
			regionNames.addAll(sortedSet);
			sortedSet.clear();
		}
		for (World world : Quests.get().getServer().getWorlds()) {
			if (world.equals(userWorld))
				continue;
			RegionManager rgManager = getRegionManager(world);
			if (rgManager!=null)
				for (ProtectedRegion region : rgManager.getRegions().values())
					sortedSet.add(region.getId());
			regionNames.addAll(sortedSet);
			sortedSet.clear();
		}
		return regionNames;
	}
	
	private static RegionManager getRegionManager(World world) {
		return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
	}

	private class RegionSelectorButton extends SelectOneElementButton<String> {

		public RegionSelectorButton(Gui parent) {
			super("&8Region Selector", new ItemBuilder(Material.END_CRYSTAL).setGuiProperty().build(), parent,
					getRegionNames(parent.getTargetPlayer()), true, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lRegionName Editor");
			desc.add("&6Click to edit");
			if (getRegionName() != null)
				desc.add("&7Current RegionName '&r" + getRegionName() + "&7'");
			else
				desc.add("&7No RegionName is set");
			return desc;
		}

		@Override
		public List<String> getElementDescription(String element) {
			return Arrays.asList("&6Region: '&e" + element + "&6'");
		}

		@Override
		public ItemStack getElementItem(String element) {
			return new ItemBuilder(Material.END_CRYSTAL).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(String element) {
			if (RegionTaskInfo.this.setRegionName(element)) {
				getParent().updateInventory();
				getTargetPlayer().openInventory(getParent().getInventory());
			}
		}

	}

}
