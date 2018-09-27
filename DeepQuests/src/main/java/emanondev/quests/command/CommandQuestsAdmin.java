package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.citizenbinds.CitizenBindManager;
import emanondev.quests.hooks.Hooks;
import emanondev.quests.newgui.gui.Gui;
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
	 * 				reset
	 * 				erase
	 * 				quest
	 * 					<questid>
	 * 						erase
	 * 						reset
	 * 						mission
	 * 							<missionid>
	 * 								erase
	 * 								reset
	 * 								task
	 * 									<taskid>
	 * 										reset
	 * 						liststartedmissions
	 * 						listcompletedmissions
	 * 						missionpoint
	 * 				questpoint
	 * 				liststartedquests
	 * 				listcompletedquests
	 */
	
}

class SubPlayer extends SubCmdManager {
	SubPlayer() {
		super("player",Perms.ADMIN_QUEST,
				new SubPlayerOpenGui(),
				new SubPlayerResetQuest(),
				new SubPlayerResetMission()
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
		onHelp(params, sender, label, args);
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
		p.openInventory(Quests.get().getGuiManager().getQuestsInventory(target, Quests.get().getQuestManager(), false));
		return;
	}
}
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
}
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
		super("citizen",Perms.ADMIN_QUEST,
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
		super("bindquest",Perms.ADMIN_QUEST);
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
