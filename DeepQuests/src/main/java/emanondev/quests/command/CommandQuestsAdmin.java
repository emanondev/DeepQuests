package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.citizenbinds.CitizenBindManager;
import emanondev.quests.hooks.Hooks;
import emanondev.quests.newgui.gui.AdminPlayerManagerGui;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.QuestsMenu;
import emanondev.quests.player.OfflineQuestPlayer;
import emanondev.quests.utils.Completer;
import net.md_5.bungee.api.ChatColor;

public class CommandQuestsAdmin extends CmdManager {
	public CommandQuestsAdmin() {
		super("questsadmin", Arrays.asList("qa","questadmin","questsadmin","qadmin"),null,
				new SubReload(),
				new SubEditor(),
				new SubPlayer(),
				new SubCitizen()
				);
	}
	/* TODO
	 * qa
	 * 		player
	 * 			<player>
	 * 				resetQuest
	 * 				resetMission
	 * 				erase
	 * 				opengui
	 * 				questpoint
	 * 				liststartedquests
	 * 				listcompletedquests
	 */
	
}

class SubPlayer extends SubCmdManager {
	SubPlayer() {
		super("player",Perms.ADMIN_PLAYER,
				new SubPlayerOpenGui(),
				//new SubPlayerResetQuest(),
				new SubPlayerResetMission(),
				new SubPlayerSwap()
				);
		this.setDescription(ChatColor.GOLD+"commands related to players");
		this.setParams("<player>");
	}
	
	//qa 	player	<player>
	//		0		1
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.isEmpty()) {
			String[] args2 = new String[args.length+1];
			for (int i = 0; i< args.length;i++)
				args2[i]= args[i];
			args2[args2.length-1]= "<player>";
				
			onHelp( params, sender, label, args2);
			return;
		}
		params.remove(0);
		if (params.size()>0) {
			super.onCmd( params, sender, label, args);
			return;
		}
		Player player = Bukkit.getPlayer(args[1]);
		if (player==null) {
			sender.sendMessage(ChatColor.RED+"Player '"+args[1]+"' not found");
			return;
		}
		//onHelp(params, sender, label, args);
		if (!(sender instanceof Player)) {
			CmdUtils.playersOnly(sender);
			return;
		}
		((Player) sender).openInventory(new AdminPlayerManagerGui(
				(Player) sender,
				Quests.get().getQuestManager().getQuestsEditor((Player) sender, null),
				player).getInventory());
		return;
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completePlayerNames(list, params.get(0));
			return list;
		}
		params.remove(0);
		return super.onTab(params, sender, label, args);	
	}
}
class SubPlayerSwap extends SubCmdManager {
	SubPlayerSwap() {
		super("swap",null);
		this.setDescription(ChatColor.GOLD+"command to move quest data of player1 to player2");
		this.setParams("<player>");
	}
	
	//qa 	player	<player> 	pass 	<player>
	//		0		1			2		3
	@SuppressWarnings("deprecation")
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.isEmpty()) {
			String[] args2 = new String[args.length+1];
			for (int i = 0; i< args.length;i++)
				args2[i]= args[i];
			args2[args2.length-1]= "<player>";
				
			onHelp( params, sender, label, args2);
			return;
		}
		params.remove(0);
		if (params.size()>0) {
			super.onCmd( params, sender, label, args);
			return;
		}
		OfflinePlayer player1 = Bukkit.getOfflinePlayer(args[1]);
		if (player1==null) {
			sender.sendMessage(ChatColor.RED+"Player '"+args[1]+"' not found");
			return;
		}
		OfflinePlayer player2 = Bukkit.getOfflinePlayer(args[3]);
		if (player2==null) {
			sender.sendMessage(ChatColor.RED+"Player '"+args[3]+"' not found");
			return;
		}
		if (player1==player2) {
			sender.sendMessage(ChatColor.RED+"Can't Swap Quest Data for "+ChatColor.YELLOW+player1.getName()+ChatColor.RED+" and himself");
			return;
		}
		OfflineQuestPlayer off1 = Quests.get().getPlayerManager().getOfflineQuestPlayer(player1);
		OfflineQuestPlayer off2 = Quests.get().getPlayerManager().getOfflineQuestPlayer(player2);
		if (off1.passTo(off2))
			sender.sendMessage(ChatColor.GREEN+"Swapped Quest Data for "+ChatColor.YELLOW+player1.getName()+ChatColor.GREEN+" and "+ChatColor.YELLOW+player2.getName());
		else
			sender.sendMessage(ChatColor.RED+"Unable to Swap Quest Data for "+ChatColor.YELLOW+player1.getName()+ChatColor.RED+" and "+ChatColor.YELLOW+player2.getName());
		return;
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completePlayerNames(list, params.get(0));
			return list;
		}
		params.remove(0);
		return super.onTab(params, sender, label, args);	
	}
}
class SubPlayerOpenGui extends SubCmdManager {

	SubPlayerOpenGui() {
		super("opengui", null);
		this.setPlayersOnly(true);
		this.setDescription(ChatColor.GOLD+"Open the Quests gui of the player");
	}
	//qa 	player 	<player> 	opengui
	//		0		1			2
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Player target = Bukkit.getPlayer(args[1]);
		if (target==null) {
			sender.sendMessage(ChatColor.RED+"Player '"+args[1]+"' not found");
			return;
		}
		Player p = (Player) sender;
		p.openInventory(new QuestsMenu(target, null, Quests.get().getQuestManager()).getInventory());
		return;
	}
}/*
class SubPlayerResetQuest extends SubCmdManager {

	SubPlayerResetQuest() {
		super("resetquest", null);
		this.setPlayersOnly(true);
		this.setDescription(ChatColor.GOLD+"Reset Quests for the player with a click");
	}
	//qa 	player 	<player> 	ResetQuest
	//		0		1			2
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Player target = Bukkit.getPlayer(args[1]);
		if (target==null) {
			sender.sendMessage(ChatColor.RED+"Player '"+args[1]+"' not found");
			return;
		}
		Player p = (Player) sender;
		p.openInventory(Quests.get().getGuiManager().getQuestsResetGui(target, Quests.get().getQuestManager()));
		return;
	}
}*/
class SubPlayerResetMission extends SubCmdManager {

	SubPlayerResetMission() {
		super("resetmission", null);
		this.setPlayersOnly(true);
		this.setDescription(ChatColor.GOLD+"Reset Missions for the player with a click");
	}
	//qa 	player 	<player> 	ResetMission
	//		0		1			2
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Player target = Bukkit.getPlayer(args[1]);
		if (target==null) {
			sender.sendMessage(ChatColor.RED+"Player '"+args[1]+"' not found");
			return;
		}
		Player p = (Player) sender;
		p.openInventory(Quests.get().getGuiManager().getMissionsResetGui(target, Quests.get().getQuestManager()));
		return;
	}
}
class SubCitizen extends SubCmdManager {
	SubCitizen() {
		super("citizen",Perms.ADMIN_CITIZEN,
				new SubCitizenBindQuest()
				);
		this.setDescription(ChatColor.GOLD+"Edit/show info of the selected quest");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (!Hooks.isCitizenEnabled()) {
			sender.sendMessage(ChatColor.RED+"Citizen plugin not enabled");
			return;
		}
		super.onCmd(params,sender,label,args);
	}
}
class SubCitizenBindQuest extends SubCmdManager {
	SubCitizenBindQuest() {
		super("bindquest",null);
		this.setDescription(ChatColor.GOLD+"Bind a quest to an npc");
		this.setPlayersOnly(true);
	}
	//qa 	citizen 	bindquest
	//		0			1
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		CitizenBindManager manager = Quests.get().getCitizenBindManager();
		if (manager==null) {
			sender.sendMessage(ChatColor.RED+"Cound not find CitizenBindManager");
			return;
		}
		manager.openEditor((Player) sender);
	}
}
class SubReload extends SubCmdManager {
	SubReload() {
		super("reload",Perms.ADMIN_RELOAD);
		this.setDescription(ChatColor.GOLD+"Reload the plugin configuration");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quests.get().reload();
		sender.sendMessage(ChatColor.GREEN+"Plugin Reloaded");
	}
}
class SubEditor extends SubCmdManager {
	SubEditor() {
		super("editor",Perms.ADMIN_EDITOR);
		this.setDescription(ChatColor.GOLD+"Open Gui Editor for Quests");
		this.setPlayersOnly(true);
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Gui gui = Quests.get().getQuestManager().getQuestsEditor((Player) sender,null);
		((Player) sender).openInventory(gui.getInventory());
	}
}
