package emanondev.quests.interfaces.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import emanondev.quests.interfaces.Paths;

public class RegionsData extends QuestComponentData {
	
	private Set<String> regionNames = new HashSet<>();
	private boolean isRegionListWhitelist = true;
	
	@SuppressWarnings("unchecked")
	public RegionsData(Map<String,Object> map) {
		if (map==null) 
			return;
		try {
			List<String> regions = (List<String>) map.getOrDefault(Paths.DATA_REGION_LIST,null);
			if (regions!=null && regions.size()>0)
				for (String regionName:regions)
					if (regionName!=null&&!regionName.isEmpty()&&!regionName.contains(" "))
						regionNames.add(regionName.toLowerCase());
			isRegionListWhitelist = (boolean) map.getOrDefault(Paths.DATA_REGION_LIST_IS_WHITELIST,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_REGION_LIST,new ArrayList<String>(regionNames));
		map.put(Paths.DATA_REGION_LIST_IS_WHITELIST,isRegionListWhitelist);
		return map;
	}
	
	public boolean isValidRegion(ProtectedRegion region) {
		if (isRegionListWhitelist)
			return regionNames.contains(region.getId().toLowerCase());
		else
			return !regionNames.contains(region.getId().toLowerCase());
	}

}
