package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.AButton;
import emanondev.quests.newgui.button.AmountSelectorButton;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.Utils;

public class LocationData extends QCData{
	public LocationData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		this.x = getSection().getInt(PATH_X,0);
		this.y = getSection().getInt(PATH_Y,0);
		this.z = getSection().getInt(PATH_Z,0);
		this.world = getSection().getString(PATH_WORLD,null);
	}
	private final static String PATH_X = "location.x";
	private final static String PATH_Y = "location.y";
	private final static String PATH_Z = "location.z";
	private final static String PATH_WORLD = "location.world";
	
	private int x;
	private int y;
	private int z;
	private String world;
	public boolean isValidLocation(Location loc) {
		if (loc==null || world == null)
			return false;
		return world.equals(loc.getWorld().getName()) && x==loc.getBlockX() && y==loc.getBlockY() && z==loc.getBlockZ();
	}
	public boolean setX(int x) {
		if (x == this.x)
			return false;
		this.x = x;
		getSection().set(PATH_X,x);
		setDirty(true);
		return true;
	}
	public int getX() {
		return x;
	}
	public boolean setY(int y) {
		if (y == this.y)
			return false;
		this.y = y;
		getSection().set(PATH_Y,y);
		setDirty(true);
		return true;
	}
	public int getY() {
		return y;
	}
	public boolean setZ(int z) {
		if (z == this.z)
			return false;
		this.z = z;
		getSection().set(PATH_Z,z);
		setDirty(true);
		return true;
	}
	public int getZ() {
		return z;
	}
	public String getWorldName() {
		return world;
	}
	public boolean setWorld(World w) {
		if (w.getName() == this.world)
			return false;
		this.world = w.getName();
		getSection().set(PATH_WORLD,world);
		setDirty(true);
		return true;
		
	}
	public Button getLocationSelectorButton(Gui gui) {
		return new LocationSelectorButton(gui);
	}

	private class LocationSelectorButton extends AButton {
		private ItemStack item;

		public LocationSelectorButton(Gui parent) {
			super(parent);
			item = new ItemBuilder(Material.COMPASS).setGuiProperty().build();
			update();
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public boolean update() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lLocation Editor Button");
			desc.add("&6Click to edit");
			if (world==null)
				desc.add("&7No Location is set");
			else {
				desc.add("&6Current location:");
				desc.add("&e"+world+" "+x+" "+y+" "+z);
			}
			Utils.updateDescription(item,desc,null,true);
			return true;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new LocationGui(clicker,getParent()).getInventory());
		}
		private class LocationGui extends MapGui {

			public LocationGui(Player p, Gui previusHolder) {
				super("&8Location Editor", 6, p, previusHolder);
				this.putButton(53,new BackButton(this));
				this.putButton(4,new WorldSelector(this));
				this.putButton(19,new XSelector(this));
				this.putButton(22,new YSelector(this));
				this.putButton(25,new ZSelector(this));
			}
			public class WorldSelector extends SelectOneElementButton<World> {

				public WorldSelector(Gui parent) {
					super("&8World Selector", new ItemBuilder(Material.COMPASS).setGuiProperty().build(), parent, Bukkit.getWorlds(), true, true, false);
				}

				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lWorld Selector");
					desc.add("&6Click to edit");
					desc.add("&6Current world: &e"+world);
					return desc;
				}

				@Override
				public List<String> getElementDescription(World element) {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6World: &e"+element.getName());
					return desc;
				}

				@Override
				public ItemStack getElementItem(World element) {
					return new ItemBuilder(Material.COMPASS).setGuiProperty().build();
				}

				@Override
				public void onElementSelectRequest(World element) {
					if (setWorld(element)) {
						updateInventory();
						getTargetPlayer().openInventory(getParent().getInventory());
					}
				}
				
			}
			public class XSelector extends AmountSelectorButton {
				public XSelector(Gui parent) {
					super("&9X Selector", new ItemBuilder(Material.WOOL).setDamage(1).setGuiProperty().build(), parent);
				}
				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lX Coordinate Editor");
					desc.add("&6Click to edit");
					desc.add("&6Current value: &e"+x);
					return desc;
				}
				@Override
				public long getCurrentAmount() {
					return x;
				}
				@Override
				public boolean onAmountChangeRequest(long i) {
					return setX((int) i);
				}
			}
			public class YSelector extends AmountSelectorButton {
				public YSelector(Gui parent) {
					super("&9Y Selector", new ItemBuilder(Material.WOOL).setDamage(1).setGuiProperty().build(), parent);
				}
				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lY Coordinate Editor");
					desc.add("&6Click to edit");
					desc.add("&6Current value: &e"+y);
					return desc;
				}
				@Override
				public long getCurrentAmount() {
					return y;
				}
				@Override
				public boolean onAmountChangeRequest(long i) {
					return setY((int) i);
				}
			}
			public class ZSelector extends AmountSelectorButton {
				public ZSelector(Gui parent) {
					super("&9Z Selector", new ItemBuilder(Material.WOOL).setDamage(1).setGuiProperty().build(), parent);
				}
				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lZ Coordinate Editor");
					desc.add("&6Click to edit");
					desc.add("&6Current value: &e"+z);
					return desc;
				}
				@Override
				public long getCurrentAmount() {
					return z;
				}
				@Override
				public boolean onAmountChangeRequest(long i) {
					return setZ((int) i);
				}
			}
			
			
		}
	}
}
