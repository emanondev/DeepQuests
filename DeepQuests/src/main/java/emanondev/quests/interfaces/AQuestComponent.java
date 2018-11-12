package emanondev.quests.interfaces;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AQuestComponent<T extends User<T>> implements QuestComponent<T> {
	
	private int priority;
	private final String key;
	private QuestComponent<T> parent;
	
	public AQuestComponent(Map<String, Object> map) {
		if (!map.containsKey(Paths.KEY))
			throw new NullPointerException("missing key");
		key = (String) map.get(Paths.KEY);
		if (key == null || key.isEmpty())
			throw new IllegalArgumentException("invalid key");
		if (!Paths.ALPHANUMERIC.matcher(key).matches())
			throw new IllegalArgumentException("key must be alphanumeric");
		priority = map.get(Paths.PRIORITY) == null ? 0 : (int) map.get(Paths.PRIORITY);
		displayName = map.get(Paths.DISPLAY_NAME)==null ? key : (String) map.get(Paths.DISPLAY_NAME);
		
	}
	

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.KEY,key);
		if (priority!=0)
			map.put(Paths.PRIORITY,priority);
		map.put(Paths.DISPLAY_NAME, displayName);
		return map;
	}

	@Override
	public QuestComponent<T> getParent() {
		return parent;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setParent(QuestComponent<T> parent) {
		if (parent!=null)
			throw new IllegalStateException();
		this.parent = parent;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public boolean setPriority(int priority) {
		this.priority = priority;
		return true;
	}

	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	
	public boolean setDisplayName(String name) {
		if (name==null || name.isEmpty())
			return false;
		this.displayName = name;
		return true;
	}

}
