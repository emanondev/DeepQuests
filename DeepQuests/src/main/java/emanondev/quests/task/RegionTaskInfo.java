package emanondev.quests.task;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.button.TextEditorButton;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class RegionTaskInfo {
	private final static String PATH_REGION_NAME = "region-name";
	private final static String PATH_REGION_NAME_CONTAINS = "region-name-contains";
	private String regionName;
	private String regionNameContains;
	private final ConfigSection section;
	private final Task parent;

	public RegionTaskInfo(ConfigSection m, Task t) {
		if (m == null || t == null)
			throw new NullPointerException();
		this.section = m;
		this.parent = t;
		regionName = m.getString(PATH_REGION_NAME, null);
		regionNameContains = m.getString(PATH_REGION_NAME_CONTAINS, null);
		/*
		 * if (regionName == null && regionNameContains == null) throw new
		 * NullPointerException("No region name selected on "
		 * +m.getCurrentPath()+m.getName()+PATH_REGION_NAME+" or on "
		 * +m.getCurrentPath()+m.getName()+PATH_REGION_NAME_CONTAINS);
		 */
	}

	public String getRegionName() {
		return regionName;
	}

	public boolean setRegionName(String name) {
		if (name.isEmpty())
			name = null;
		if (regionName == name || (regionName != null && regionName.equals(name)))
			return false;
		this.regionName = name;
		this.section.set(PATH_REGION_NAME, regionName);
		this.parent.setDirty(true);
		return true;
	}

	public boolean isValidRegion(ProtectedRegion region) {
		if (regionName != null && regionName.equals(region.getId()))
			return true;
		if (regionNameContains != null && region.getId().contains(regionNameContains))
			return true;
		return false;
	}

	private final static BaseComponent[] changeTitleDesc = new ComponentBuilder(
			ChatColor.GOLD + "Click suggest the command\n\n" + ChatColor.GOLD + "type the new Region Name\n"
					+ ChatColor.YELLOW + "/questtext <new display name>").create();

	public EditorButtonFactory getRegionNameEditorButtonFactory() {
		return new RegionNameEditorButtonFactory();
	}

	private class RegionNameEditorButtonFactory implements EditorButtonFactory {
		private class RegionNameEditorButton extends TextEditorButton {
			private ItemStack item = new ItemStack(Material.NAME_TAG);

			public RegionNameEditorButton(CustomGui parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lRegionName Editor"));
				item.setItemMeta(meta);
				update();
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			public void update() {
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&6Click to edit");
				if (getRegionName() != null) {
					lore.add("&7Current RegionName '&r" + getRegionName() + "&7'");
				} else {
					lore.add("&7No RegionName is set");
				}
				meta.setLore(StringUtils.fixColorsAndHolders(lore));
				item.setItemMeta(meta);
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, StringUtils.revertColors(getRegionName()), changeTitleDesc);
			}

			@Override
			public void onReicevedText(String text) {
				if (setRegionName(text)) {
					update();
					getParent().reloadInventory();
				} else
					getOwner().sendMessage(StringUtils.fixColorsAndHolders("&cSelected name was not a valid name"));
			}
		}

		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new RegionNameEditorButton(parent);
		}
	}

}
