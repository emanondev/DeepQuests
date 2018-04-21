package emanondev.quests.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import emanondev.quests.Quests;

public class CommandQuests implements TabExecutor {
	public CommandQuests() {
		Quests.getInstance().registerCommand("quests", this,"quest");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("this command is for players Only");//TODO
			return true;
		}
		Player p = (Player) sender;
		p.openInventory(Quests.getInstance().getGuiManager().getQuestsInventory(p, 1));
		return true;
	}

}
