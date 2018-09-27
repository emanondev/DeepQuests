package emanondev.quests.data;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.Savable;

public class QCData implements Savable {
	private final QuestComponent parent;
	private final ConfigSection section;
	public QCData(ConfigSection section, QuestComponent parent) {
		if (parent==null || section == null)
			throw new NullPointerException();
		this.section = section;
		this.parent = parent;
	}
	
	public ConfigSection getSection() {
		return section;
	}
	public QuestComponent getParent() {
		return parent;
	}
	
	public boolean isDirty() {
		return section.isDirty();
	}

	public void setDirty(boolean value) {
		section.setDirty(value);
	}
	
	private boolean dirtyLoad = false;

	public void setDirtyLoad() {
		dirtyLoad = true;
	}

	public boolean isLoadDirty() {
		return dirtyLoad;
	}
	
	public QuestManager getQuestManager() {
		return parent.getQuestManager();
	}


}
