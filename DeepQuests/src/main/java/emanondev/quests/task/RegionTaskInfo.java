package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionTaskInfo {
	private final static String PATH_REGION_NAME = "region-name";
	private final static String PATH_REGION_NAME_CONTAINS = "region-name-contains";
	private final String regionName;
	private final String regionNameContains;

	public RegionTaskInfo(MemorySection m) {
		regionName = m.getString(PATH_REGION_NAME,null);
		regionNameContains = m.getString(PATH_REGION_NAME_CONTAINS,null);
		if (regionName == null && regionNameContains == null)
			throw new NullPointerException("No region name selected on "
				+m.getCurrentPath()+m.getName()+PATH_REGION_NAME+" or on "
				+m.getCurrentPath()+m.getName()+PATH_REGION_NAME_CONTAINS);
	}

	public boolean isValidRegion(ProtectedRegion region) {
		if (regionName!=null && regionName.equals(region.getId()))
			return true;
		if (regionNameContains!=null && region.getId().contains(regionNameContains))
			return true;
		return false;
	}

}
