package emanondev.quests.command;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class CmdUtils {

	public static void lackPermission(CommandSender s, String permission) {
		s.sendMessage(ChatColor.RED+"You lack of permission "+permission);
	}

	public static void notImplemented(CommandSender s) {
		s.sendMessage(ChatColor.RED+"This is not implemented yet");
	}

	public static void success(CommandSender s) {
		s.sendMessage(ChatColor.GREEN+"Command successfully executed");
	}

	public static void fail(CommandSender s) {
		s.sendMessage(ChatColor.RED+"Command generated errors");
	}

	public static void playersOnly(CommandSender s) {
		s.sendMessage(ChatColor.RED+"Command for players only");
	}
}
