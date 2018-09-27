package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.hooks.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class Utils {
	public static void updateDescription(ItemStack item, List<String> desc, Player p, boolean color,
			String... holders) {
		if (item == null)
			return;
		
		// prepare title and lore
		String title;
		ArrayList<String> lore;
		if (  desc == null || desc.isEmpty()) {
			title = null;
			lore = null;
		} else if (desc.size() == 1) {
			if (desc.get(0) != null)
				if (!desc.get(0).startsWith(ChatColor.RESET+""))
					title = ChatColor.RESET + desc.get(0);
				else
					title = desc.get(0);
			else
				title = null;
			lore = null;
		} else {
			if (!desc.get(0).startsWith(ChatColor.RESET+""))
				title = ChatColor.RESET + desc.get(0);
			else
				title = desc.get(0);
			lore = new ArrayList<String>();
			for (int i = 1; i < desc.size(); i++)
				if (desc.get(i) != null)
					if (!desc.get(i).startsWith(ChatColor.RESET+""))
						lore.add(ChatColor.RESET + desc.get(i));
					else
						lore.add(desc.get(i));
				else
					lore.add("");
		}

		// apply holders and colors for title and lore
		title = fixString(title, p, color, holders);
		fixList(lore, p, color, holders);

		// apply title and lore to item
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(lore);
		item.setItemMeta(meta);

	}

	public static ItemStack setDescription(ItemStack item, List<String> desc, Player p, boolean color,
			String... holders) {
		if (item == null || item.getType() == Material.AIR)
			return null;

		ItemStack itemCopy = new ItemStack(item);
		updateDescription(itemCopy,desc,p,color,holders);
		return itemCopy;
	}

	private static void fixList(ArrayList<String> list, Player p, boolean color, String... stuffs) {
		if (list == null || list.isEmpty())
			return;
		for (int i = 0; i < list.size(); i++) {
			list.set(i, fixString(list.get(i), p, color, stuffs));
		}
	}

	public static String fixString(String text, Player p, boolean color, String... stuffs) {
		if (text == null)
			return null;

		// holders
		if (stuffs != null && stuffs.length % 2 != 0)
			throw new IllegalArgumentException("holder withouth replacer");
		if (stuffs != null && stuffs.length > 0)
			for (int i = 0; i < stuffs.length; i += 2)
				text = text.replace(stuffs[i], stuffs[i + 1]);

		// papi
		if (p != null && Hooks.isPAPIEnabled())
			text = PlaceholderAPI.setPlaceholders(p, text);

		// colore
		if (color)
			text = ChatColor.translateAlternateColorCodes('&', text);

		return text;
	}

	public static String revertColors(String s) {
		if (s == null)
			return null;
		return s.replace("§", "&");
	}

	/*
	public static boolean testIsPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return true;
		sender.sendMessage(C.GuiGeneric.COMMAND_FOR_PLAYERS_ONLY);
		return false;
	}

	public static boolean testHasPermission(Player player, String perm) {
		if (player.hasPermission(perm))
			return true;
		player.sendMessage(Utils.fixString(C.GuiGeneric.COMMAND_FOR_PLAYERS_ONLY,player,true,C.PERMISSION_HOLDER,perm));
		return false;
	}*/

}