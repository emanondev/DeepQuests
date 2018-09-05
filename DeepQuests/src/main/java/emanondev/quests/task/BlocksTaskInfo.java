package emanondev.quests.task;

import java.util.List;

import org.bukkit.block.Block;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.EditorButtonFactory;

public class BlocksTaskInfo {
	private final static String PATH_BLOCK = "blocks";
	private final ConfigSection section;
	private final Task parent;
	private final BlockChecker checker;
	
	public BlocksTaskInfo(ConfigSection m,Task parent) {
		this.parent = parent;
		this.section = m;
		this.checker = BlockChecker.getBlockChecker(section.getStringList(PATH_BLOCK),this);
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
