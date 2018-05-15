package emanondev.quests.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import emanondev.quests.Quests;

public class CmdManager extends SubCmdManager implements TabExecutor {

	public CmdManager(String commandName, List<String> aliases, String permission, SubCmdManager... subs) {
		super(aliases,permission,subs);
		if (commandName==null || commandName.isEmpty())
			throw new NullPointerException();
		if (commandName.contains(" "))
			throw new IllegalArgumentException("Command Name '"+commandName+"' contains spaces");
		this.name = commandName.toLowerCase();

		Quests.getInstance().registerCommand(this);
	}
	private final String name;
	public String getName() {
		return name;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> params = new ArrayList<String>();
		if(!hasPermission(sender))
			return params;
		if (playersOnly() && !(sender instanceof Player))
			return params;
		for (String arg : args)
			params.add(arg);
		
		return onTab(params,sender,label,args);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!hasPermission(sender)) {
			CmdUtils.lackPermission(sender, getPermission() );
			return true;
		}
		if (playersOnly() && !(sender instanceof Player)) {
			CmdUtils.playersOnly(sender);
			return true;
		}
		ArrayList<String> params = new ArrayList<String>();
		for (String arg : args)
			params.add(arg);
		
		onCmd(params,sender,label,args);
		return true;
	}
	

}
