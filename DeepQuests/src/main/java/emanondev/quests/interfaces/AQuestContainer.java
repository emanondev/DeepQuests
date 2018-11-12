package emanondev.quests.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AQuestContainer<T extends User<T>> extends AQuestComponent<T> 
						implements QuestContainer<T> {

	public AQuestContainer(Map<String, Object> map) {
		super(map);
	}
	
	@Override
	public QuestContainer<T> getParent(){
		return this;
	}

	@Override
	public List<String> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	private Map<String,Quest<T>> quests = new HashMap<>();
	
	@Override
	public Quest<T> getQuest(String key) {
		return quests.get(key);
	}

	@Override
	public Collection<Quest<T>> getQuests() {
		return Collections.unmodifiableCollection(quests.values());
	}

	@Override
	public void save() {
		getQuestManager().saveQuestContainer();
	}
	
	@Override
	public abstract QuestManager<T> getQuestManager();

}
