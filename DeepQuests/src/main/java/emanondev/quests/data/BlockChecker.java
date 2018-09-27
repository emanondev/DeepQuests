package emanondev.quests.data;

import java.util.List;

import org.bukkit.block.Block;

import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.gui.Gui;

public interface BlockChecker {
	public boolean isValidBlock(Block block);
	public static BlockChecker getBlockChecker(List<String> list,BlocksTaskInfo parent) {
		return new BlockCheckerV1_12(list,parent);
	}
	public Button getBlockSelectorButton(Gui parent);

}
