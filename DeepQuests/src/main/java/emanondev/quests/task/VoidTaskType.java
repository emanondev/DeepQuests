package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;

public class VoidTaskType extends TaskType {
	public VoidTaskType() {
		super("VoidTask");
	}
	
	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new VoidTask(m,parent);
	}

	public class VoidTask implements Task {
		private final static String name = "Void Task";
		private final Mission parent;
		private final String taskID;
		
		public VoidTask(MemorySection m,Mission parent) {
			this.parent = parent;
			this.taskID = loadName(m).toLowerCase();
		}
		private String loadName(MemorySection m) {
			String name = m.getName();
			if (name==null||name.isEmpty())
				throw new NullPointerException();
			return name;
		}
		@Override
		public String getNameID() {
			return taskID;
		}
	
		@Override
		public Mission getParent() {
			return parent;
		}
	
		@Override
		public TaskType getTaskType() {
			return VoidTaskType.this;
		}
		
		@Override
		public String getDisplayName() {	return name;	}
		@Override
		public int getMaxProgress() {	return 1;	}
		@Override
		public String getUnstartedDescription() {	return name;	}
		@Override
		public String getProgressDescription() {	return name;	}
		@Override
		public boolean onProgress(QuestPlayer p) {	return false;	}
		@Override
		public boolean onProgress(QuestPlayer p, int amount) {	return false;	}
		private boolean dirty = false;
		@Override
		public boolean isDirty() { return dirty;	}
		@Override
		public void setDirty(boolean value) { dirty = value;	}
	}
}
