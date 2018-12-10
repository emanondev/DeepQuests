package emanondev.quests.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class ARequire<T extends User<T>> extends AQuestComponent<T> implements Require<T> {
	public ARequire(Map<String, Object> map) {
		super(map);
	}
	
	public List<String> getInfo(){
		List<String> info = new ArrayList<>();
		info.add("&9&lRequire: &6"+ this.getDisplayName());
		info.add("&8Type: &7"+getType().getID());

		info.add("&8ID: "+ this.getKey());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		if (getParent() instanceof Task<?>) {
			info.add("&9Quest: &e"+((Task<T>) getParent()).getParent().getParent().getDisplayName());
			info.add("&9Mission: &e"+((Task<T>) getParent()).getParent().getDisplayName());
			info.add("&9Task: &e"+((Task<T>) getParent()).getDisplayName());
		}
		else if (getParent() instanceof Mission<?>) {
			info.add("&9Quest: &e"+((Mission<T>) getParent()).getParent().getDisplayName());
			info.add("&9Mission: &e"+((Mission<T>) getParent()).getDisplayName());
		}
		else if (getParent() instanceof Quest<?>) {
			info.add("&9Quest: &e"+((Quest<T>) getParent()).getDisplayName());
		}
		
		return info;
	}
}
