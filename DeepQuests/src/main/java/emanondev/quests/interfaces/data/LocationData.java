package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import emanondev.quests.interfaces.Paths;

public class LocationData extends QuestComponentData {
	
	private String worldName = null;
	private int x = 0;
	private int y = 0;
	private int z = 0;
	
	public LocationData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			x = (int) map.getOrDefault(Paths.DATA_LOCATION_X, 0);
			y = (int) map.getOrDefault(Paths.DATA_LOCATION_Y, 0);
			z = (int) map.getOrDefault(Paths.DATA_LOCATION_Z, 0);
			
			worldName = (String) map.getOrDefault(Paths.DATA_LOCATION_WORLD, null);
			if (worldName!=null && worldName.isEmpty())
				throw new IllegalArgumentException("Illegal worldName value");
		} catch (Exception e) {
			e.printStackTrace();
			worldName = null;
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (worldName !=null)
			map.put(Paths.DATA_LOCATION_WORLD,worldName);
		if (x != 0)
			map.put(Paths.DATA_LOCATION_X,x);
		if (y != 0)
			map.put(Paths.DATA_LOCATION_Y,y);
		if (z != 0)
			map.put(Paths.DATA_LOCATION_Z,z);
		return map;
	}
	
	public Location getLocation() {
		if (worldName==null)
			return null;
		World world = Bukkit.getServer().getWorld(worldName);
		if (world ==null)
			return null;
		return new Location(world,x,y,z);
	}
	public boolean setWorld(World world) {
		if (world ==null)
			return setWorld((String) null);
		return setWorld(world.getName());
	}
	
	public boolean setWorld(String value) {
		if (this.worldName==value)
			return false;
		if (value!=null && value.isEmpty())
			return false;
		if (this.worldName!=null && this.worldName.equals(value)) 
			return false;
		this.worldName = value;
		return true;
	}
	
	public World getWorld() {
		if (worldName == null)
			return null;
		return Bukkit.getServer().getWorld(worldName);
	}
	public String getWorldName() {
		return worldName;
	}
	public int getX() {
		return this.x;
	}
	public boolean setX(int value) {
		if (this.x == value)
			return false;
		this.x = value;
		return true;
	}
	public int getY() {
		return this.y;
	}
	public boolean setY(int value) {
		if (this.y == value)
			return false;
		this.y = value;
		return true;
	}
	public int getZ() {
		return this.z;
	}
	public boolean setZ(int value) {
		if (this.z == value)
			return false;
		this.z = value;
		return true;
	}
	
	public boolean isValidLocation(Location loc) {
		if (loc==null)
			return false;
		if (!loc.getWorld().getName().equals(worldName))
			return false;
		if (loc.getBlockX()!=x)
			return false;
		if (loc.getBlockZ()!=z)
			return false;
		if (loc.getBlockY()!=y)
			return false;
		return true;
	}
}