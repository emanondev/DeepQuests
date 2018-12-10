package emanondev.quests.interfaces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import emanondev.quests.interfaces.Paths;

public class BlockTypeData extends QuestComponentData {
	
	private EnumSet<Material> materials = EnumSet.noneOf(Material.class);
	private boolean isWhitelist = true;
	
	@SuppressWarnings("unchecked")
	public BlockTypeData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			isWhitelist = (boolean) map.getOrDefault(Paths.DATA_BLOCK_TYPE_IS_WHITELIST,true);
			List<String> materialList = (List<String>) map.getOrDefault(Paths.DATA_BLOCK_TYPE_LIST, null);
			if (materialList!=null)
				for (String rawMat : materialList) {
					try {
						materials.add(Material.valueOf(rawMat));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_BLOCK_TYPE_IS_WHITELIST,isWhitelist);
		if (materials.size()>0) {
			List<String> list = new ArrayList<>();
			for (Material mat : materials) {
				list.add(mat.toString());
			}
			map.put(Paths.DATA_BLOCK_TYPE_LIST,list);
		}
		return map;
	}
	
	public boolean isValidMaterial(Material material) {
		if (isWhitelist)
			return materials.contains(material);
		else
			return !materials.contains(material);
	}
	
	public boolean toggleMaterial(Material mat) {
		if (mat==null)
			return false;
		if (materials.contains(mat))
			materials.remove(mat);
		else
			materials.add(mat);
		return true;
	}
	
	public Set<Material> getMaterials(){
		return Collections.unmodifiableSet(materials);
	}
	
	public boolean areMaterialsWhitelist() {
		return isWhitelist;
	}
	
	
}