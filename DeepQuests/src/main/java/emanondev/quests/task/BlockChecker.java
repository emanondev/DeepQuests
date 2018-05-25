package emanondev.quests.task;

import java.util.List;

import org.bukkit.block.Block;

import emanondev.quests.gui.EditorButtonFactory;

public interface BlockChecker {
	public boolean isValidBlock(Block block);
	public static BlockChecker getBlockChecker(List<String> list,BlocksTaskInfo parent) {
		return new BlockCheckerV1_12(list,parent);
	}
	public EditorButtonFactory getBlocksSelectorButtonFactory();

}
