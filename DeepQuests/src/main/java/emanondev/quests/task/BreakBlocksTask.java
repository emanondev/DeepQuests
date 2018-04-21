package emanondev.quests.task;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;

public class BreakBlocksTask extends Task {

	
	public BreakBlocksTask(MemorySection m, Mission parent) {
		super(m, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getDefaultDescription() {
		return "mine blocks";
	}

}
