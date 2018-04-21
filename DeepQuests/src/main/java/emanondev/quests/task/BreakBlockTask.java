package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;
import emanondev.quests.mission.Mission;

public class BreakBlockTask extends Task {

	
	public BreakBlockTask(MemorySection m, Mission parent) {
		super(m, parent);
	}

	@Override
	protected String getDefaultDescription() {
		return "mine blocks";
	}

	
}
