package emanondev.quests.task;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;

import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.MemoryUtils;

public class BlocksTaskInfo {
	private final static String PATH_BLOCK = "blocks";
	private final MemorySection section;
	private final Task parent;
	private final BlockChecker checker;
	
	public BlocksTaskInfo(MemorySection m,Task parent) {
		this.parent = parent;
		this.section = m;
		this.checker = BlockChecker.getBlockChecker(MemoryUtils.getStringList(m, PATH_BLOCK),this);
	}
	
	public boolean isValidBlock(Block block) {
		return checker.isValidBlock(block);
	}
	
	public boolean setBlocksList(List<String> blocks) {
		section.set(PATH_BLOCK,blocks);
		parent.setDirty(true);
		return true;
	}
	public EditorButtonFactory getBlocksSelectorButtonFactory() {
		return checker.getBlocksSelectorButtonFactory();
	}
}
