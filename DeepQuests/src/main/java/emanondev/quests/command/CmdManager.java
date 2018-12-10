package emanondev.quests.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CmdManager extends SubCmdManager implements TabExecutor {

	/**
	 * 
	 * @param commandName - nome del comando (ex: /heal <- nome = "heal")
	 * @param aliases - lista degli alias del comando (facoltativa può essere null)
	 * @param permission - se il comando richiede un permesso specificarlo qui altrimenti null
	 * @param subs - lista dei sottocomandi (SubCmdManager)
	 */
	public CmdManager(String commandName, List<String> aliases, String permission, SubCmdManager... subs) {
		super(aliases,permission,subs);
		if (commandName==null || commandName.isEmpty())
			throw new NullPointerException();
		if (commandName.contains(" "))
			throw new IllegalArgumentException("Command Name '"+commandName+"' contains spaces");
		this.name = commandName.toLowerCase();
	}
	private final String name;
	/**
	 * @return restituisce il nome del comando
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * già implementato per compilare la lista dei sottocomandi,
	 * se si intende modificarlo sovrascrivere #onTab
	 */
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
	/**
	 * già implementato per compilare la lista dei sottocomandi,
	 * se si intende modificarlo sovrascrivere #onCmd
	 */
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
