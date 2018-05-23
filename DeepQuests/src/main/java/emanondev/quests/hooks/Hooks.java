package emanondev.quests.hooks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import emanondev.virginblock.VirginBlockAPI;

public class Hooks {
	
	private static boolean papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
	private static boolean virginBlock = Bukkit.getPluginManager().isPluginEnabled("VirginBlock");
	private static boolean citizens = Bukkit.getPluginManager().isPluginEnabled("Citizens");
	
	
	public static boolean isPAPIEnabled() {
		return papi;
	}
	public static boolean isBlockVirgin(Block block) {
		if (!virginBlock)
			return true;
		else
			return VirginBlockAPI.isBlockVirgin(block);
	}
	public static boolean isCitizenEnabled() {
		return citizens;
	}

}
