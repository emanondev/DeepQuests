package emanondev.quests.task;

import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.hooks.Hooks;
import emanondev.quests.mission.Mission;

public class BreakBlockTask extends AbstractBlockTask {

	public BreakBlockTask(MemorySection m, Mission parent) {
		super(m, parent);
	}
	@Override
	public boolean isValidBlock(Block block) {
		return super.isValidBlock(block)&&Hooks.isBlockVirgin(block);
	}
	
	
}
