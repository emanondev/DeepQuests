package emanondev.quests.quest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;

public class QuestManager {
	//private final static String PATH_QUESTS = "quests.";
	
	private YMLConfig data = new YMLConfig(Quests.getInstance(),"quests");
	private static HashMap<String,Quest> quests = new HashMap<String,Quest>();
	
	public QuestManager() {
		Set<String> s = data.getValues(false).keySet();
		s.forEach((key)->{
			boolean dirty = false;
			try {
				Quest quest = new Quest((MemorySection) data.get(key));
				quests.put(quest.getNameID(),quest);
				if (quest.isDirty())
					dirty = true;
			}catch (Exception e) {
				e.printStackTrace();
				Quests.getInstance().getLoggerManager().getLogger("errors")
					.log("Error while loading Quests on file quests.yml '"
							+key+"' could not be read as valid quest"
							,ExceptionUtils.getStackTrace(e));
			}
			
			if (dirty)
				data.save();
		});
	}
	
	public Quest getQuestByNameID(String key) {
		return quests.get(key);
	}
	public Collection<Quest> getQuests() {
		return Collections.unmodifiableCollection(quests.values());
	}
}
