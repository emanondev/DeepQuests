package emanondev.quests.interfaces.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import emanondev.quests.interfaces.Paths;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class MythicMobsData extends QuestComponentData {
	private final HashSet<String> internalNames = new HashSet<String>();
	private boolean internalNamesIsWhitelist = true;
	private int minLv = 0;
	private int maxLv = 1000;
	private boolean checkLv = false;
	
	public MythicMobsData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			checkLv = (boolean) map.getOrDefault(Paths.MYTHICMOBDATA_CHECK_LV,checkLv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			internalNamesIsWhitelist = (boolean) map.getOrDefault(Paths.MYTHICMOBDATA_INTERNAL_NAMES_IS_WHITELIST,internalNamesIsWhitelist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			minLv = (int) map.getOrDefault(Paths.MYTHICMOBDATA_MIN_LV,minLv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			maxLv = (int) map.getOrDefault(Paths.MYTHICMOBDATA_MAX_LV,maxLv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) map.getOrDefault(Paths.MYTHICMOBDATA_INTERNAL_NAMES,new ArrayList<>());
			for (String name:list) {
				if (name!=null && !name.isEmpty())
					internalNames.add(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean isValidMythicMob(ActiveMob mob) {
		if (mob == null)
			return false;
		if (checkLv)
			if (mob.getLevel()<minLv || mob.getLevel() >maxLv)
				return false;
		
		if (internalNamesIsWhitelist)
			return internalNames.contains(mob.getType().getInternalName());
		return !internalNames.contains(mob.getType().getInternalName());
	}
	
	public boolean toggleInternalName(String internalName) {
		if (internalName==null || internalName.isEmpty())
			return false;
		if (internalNames.contains(internalName))
			internalNames.remove(internalName);
		else
			internalNames.add(internalName);
		return true;
	}
	
	public boolean setMaxLv(int lv) {
		lv = Math.max(Math.max(minLv,1),lv);
		if (lv == maxLv)
			return false;
		maxLv = lv;
		return true;
	}
	public boolean setMinLv(int lv) {
		lv = Math.min(Math.max(lv, 0),maxLv);
		if (lv == minLv)
			return false;
		minLv = lv;
		return true;
	}
	
	public boolean setCheckLv(boolean value) {
		if (value == checkLv)
			return false;
		checkLv = value;
		return true;
	}
	public boolean setInternalNameIsWhitelist(boolean value) {
		if (internalNamesIsWhitelist== value)
			return false;
		internalNamesIsWhitelist = value;
		return true;
	}
	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.MYTHICMOBDATA_CHECK_LV,checkLv);
		map.put(Paths.MYTHICMOBDATA_INTERNAL_NAMES_IS_WHITELIST,internalNamesIsWhitelist);
		map.put(Paths.MYTHICMOBDATA_MIN_LV,minLv);
		map.put(Paths.MYTHICMOBDATA_MAX_LV,maxLv);
		map.put(Paths.MYTHICMOBDATA_INTERNAL_NAMES,new ArrayList<>(internalNames));
		return map;
	}
}
