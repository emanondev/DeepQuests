package emanondev.quests.hooks;

import org.bukkit.block.Block;

import it.hotmail.hflipon.virginblock.VirginBlockAPI;

public class Hooks {
	
	private static boolean papi = false;
	private static boolean virginBlock = false;
	public static boolean isPAPIEnabled() {
		return papi;
	}
	public static boolean isBlockVirgin(Block block) {
		if (!virginBlock)
			return true;
		else
			return VirginBlockAPI.isBlockVirgin(block);
	}

}
