package emanondev.quests.interfaces.data;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import emanondev.quests.interfaces.QuestComponent;
import emanondev.quests.interfaces.QuestManager;


public abstract class QuestComponentData implements ConfigurationSerializable {
	private QuestComponent<?> parent = null;
	public void setParent(QuestComponent<?> parent) {
		if (parent==null)
			this.parent = parent;
		else
			new IllegalStateException().printStackTrace();
	}
	public QuestComponent<?> getParent() {
		return parent;
	}
	
	public QuestManager<?> getQuestManager() {
		return parent.getQuestManager();
	}


}