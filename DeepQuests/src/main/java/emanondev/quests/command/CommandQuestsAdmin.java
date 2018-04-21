package emanondev.quests.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import emanondev.quests.Quests;

public class CommandQuestsAdmin implements TabExecutor {
	public CommandQuestsAdmin() {
		Quests.getInstance().registerCommand("questsadmin", this,
				"qa","questadmin","questsadmin","qadmin");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		switch (args.length) {
		case 1:
			ArrayList<String> list = new ArrayList<String>();
			list.add("reload");
			return list;
		default: 
			return new ArrayList<String>();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==0) {
			help(sender);
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		case "reload":
			Quests.getInstance().reload();
			sender.sendMessage("Plugin Reloaded");
			return true;
		default:
			help(sender);
			return true;
		}
	}

	private void help(CommandSender sender) {
		// TODO Auto-generated method stub
		
	}

}
