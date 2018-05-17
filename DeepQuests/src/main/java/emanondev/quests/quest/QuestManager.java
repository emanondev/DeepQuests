package emanondev.quests.quest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.YmlLoadable;

public class QuestManager implements Savable {
	
	private final YMLConfig data = new YMLConfig(Quests.getInstance(),"quests");
	private static final HashMap<String,Quest> quests = new HashMap<String,Quest>();
	
	public QuestManager() {
		reload();
	}
	public void reload() {
		quests.clear();
		data.reload();
		Set<String> s = data.getValues(false).keySet();
		s.forEach((key)->{
			boolean dirty = false;
			try {
				Quest quest = new Quest((MemorySection) data.get(key),this);
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
			for (Quest quest : quests.values()){
				if (isDirty())
					break;
				if (quest.isDirty())
					setDirty(true);
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

	private boolean dirty = false;
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean value) {
		if (dirty == value)
			return;
		dirty = value;
		if (dirty == true) {
			Bukkit.getScheduler().runTaskLater(Quests.getInstance(), new Runnable() {
				@Override
				public void run() {
					QuestManager man = Quests.getInstance().getQuestManager();
					if (!man.isDirty())
						return;
					man.data.save();
					man.setDirty(false);
				}
			}, 20);
		}
		else {
			for (Quest quest : quests.values()) {
				quest.setDirty(false);
			}
		}
		
	}
	
	public boolean addQuest(String id,String displayName) {
		if (id == null || id.isEmpty() || 
				id.contains(" ")||id.contains(".")||id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_"," ");
		id = id.toLowerCase();
		if (quests.containsKey(id))
			return false;
		data.set(id+"."+YmlLoadable.PATH_DISPLAY_NAME,displayName);
		Quest q = new Quest((MemorySection) data.get(id),this);
		quests.put(q.getNameID(), q);
		q.setDirty(true);
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public boolean deleteQuest(Quest quest) {
		if (quest == null || !quests.containsKey(quest.getNameID()) )
			return false;
		data.set(quest.getNameID(),null);
		quests.remove(quest.getNameID());
		setDirty(true);
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}
}
