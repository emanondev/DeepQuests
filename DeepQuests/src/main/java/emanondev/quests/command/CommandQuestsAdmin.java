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
import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.Completer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class CommandQuestsAdmin extends CmdManager {
	public CommandQuestsAdmin() {
		super("questsadmin", Arrays.asList("qa","questadmin","questsadmin","qadmin"),null,
				new SubReload(),
				new SubListQuest(),
				new SubQuest(),
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
		p.openInventory(Quests.getInstance().getGuiManager().getQuestsInventory(target, Quests.getInstance().getQuestManager(), false));
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
		p.openInventory(Quests.getInstance().getGuiManager().getQuestsResetGui(target, Quests.getInstance().getQuestManager()));
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
		p.openInventory(Quests.getInstance().getGuiManager().getMissionsResetGui(target, Quests.getInstance().getQuestManager()));
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
		CitizenBindManager manager = Quests.getInstance().getCitizenBindManager();
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
		Quests.getInstance().reload();
		sender.sendMessage(ChatColor.GREEN+"Plugin Reloaded");
	}
}
class SubListQuest extends SubCmdManager {
	SubListQuest() {
		super("listquest",Perms.ADMIN_LISTQUEST);
		this.setDescription(ChatColor.GOLD+"Shows registered quests list");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		ComponentBuilder comp = new ComponentBuilder(
				""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"  Quests List  "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----\n");
		boolean val = false;
		for ( Quest quest : Quests.getInstance().getQuestManager().getQuests()) {
			if (val==false)
				comp.append(ChatColor.YELLOW+quest.getDisplayName()+" ");
			else
				comp.append(ChatColor.GOLD+quest.getDisplayName()+" ");
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(
					ChatColor.GOLD+"Click to examine Quest\n"+
					ChatColor.DARK_AQUA+"ID: "+ChatColor.AQUA+quest.getNameID()+"\n"+
					ChatColor.DARK_AQUA+"Missions: "+ChatColor.AQUA+quest.getMissions().size()
					).create()));
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/qa quest "+quest.getNameID()+" info"));
			val = !val;
		}
		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"  Quests List  "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		sender.spigot().sendMessage(comp.create());
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
		Quests.getInstance().getQuestManager().openEditorGui((Player) sender);
	}
}

//qa 	quest 	<id> 	mission		<id>
//		0		1		2			(3)
class SubQuest extends SubCmdManager {
	SubQuest() {
		super("quest",Perms.ADMIN_QUEST,
				new SubQuestSubInfo(),
				new SubMission(),
				new SubQuestSubEditor()
				);
		this.setDescription(ChatColor.GOLD+"Edit/show info of the selected quest");
		this.setParams("<questID>");
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.isEmpty()) {
			String[] args2 = new String[args.length+1];
			for (int i = 0; i< args.length;i++)
				args2[i]= args[i];
			args2[args2.length-1]= "<questID>";
				
			onHelp( params, sender, label, args2);
			return;
		}
		params.remove(0);
		if (params.size()>0) {
			super.onCmd(params,sender,label,args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		onHelp( params, sender, label, args);
		return;
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeQuests(list, params.get(0), Quests.getInstance().getQuestManager().getQuests());
			return list;
		}
		
		params.remove(0);
		return super.onTab(params, sender, label, args);	
	}
}
class SubQuestSubEditor extends SubCmdManager {
	SubQuestSubEditor() {
		super("editor",Perms.ADMIN_EDITOR);
		this.setDescription(ChatColor.GOLD+"Open Gui Editor for Quest");
		this.setPlayersOnly(true);
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		q.openEditorGui((Player) sender);
	}
}
//qa 	quest 	<id> 	info
//		0		1		2
class SubQuestSubInfo extends SubCmdManager {
	SubQuestSubInfo(){
		super("info",Perms.ADMIN_QUEST_INFO
				
				);
		this.setDescription(ChatColor.GOLD+"Shows selected quest info");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		sender.spigot().sendMessage(q.toComponent());
	}
}
class SubMission extends SubCmdManager {
	SubMission() {
		super("mission",Perms.ADMIN_QUEST_MISSION,
				new SubMissionSubEditor(),
				new SubMissionSubInfo(),
				new SubTask()
				);
		this.setDescription(ChatColor.GOLD+"Shows quest's missions menu");
	}
	
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.isEmpty()) {
			String[] args2 = new String[args.length+1];
			for (int i = 0; i< args.length;i++)
				args2[i]= args[i];
			args2[args2.length-1]= "<missionID>";
				
			onHelp( params, sender, label, args2);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		params.remove(0);
		if (params.size()>0) {
			super.onCmd(params,sender,label,args);
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		onHelp( params, sender, label, args);
		return;
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
			if (q==null) {
				return list;
			}
			Completer.completeMissions(list, params.get(0), q.getMissions());
			return list;
		}
		params.remove(0);
		return super.onTab(params, sender, label, args);	
	}
}
class SubMissionSubEditor extends SubCmdManager {
	SubMissionSubEditor() {
		super("editor",Perms.ADMIN_EDITOR);
		this.setDescription(ChatColor.GOLD+"Open Gui Editor for Mission");
		this.setPlayersOnly(true);
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		m.openEditorGui((Player) sender);
	}
}
//qa 	quest 	<id> 	mission		<id>	info
//		0		1		2			3		4
class SubMissionSubInfo extends SubCmdManager {
	SubMissionSubInfo() {
		super("info",Perms.ADMIN_QUEST_MISSION_INFO);
		this.setDescription(ChatColor.GOLD+"Shows mission info");
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		sender.spigot().sendMessage(m.toComponent());
	}
}
class SubTask extends SubCmdManager {
	SubTask() {
		super("task",Perms.ADMIN_QUEST_MISSION_TASK,
				new SubTaskSubEditor(),
				new SubTaskSubInfo()
				);
		this.setDescription(ChatColor.GOLD+"Shows quest's missions menu");
	}
	
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.isEmpty()) {
			String[] args2 = new String[args.length+1];
			for (int i = 0; i< args.length;i++)
				args2[i]= args[i];
			args2[args2.length-1]= "<taskID>";
				
			onHelp( params, sender, label, args2);
			return;
		}
		params.remove(0);
		if (params.size()>0) {
			super.onCmd(params,sender,label,args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		Task t = m.getTaskByNameID(args[5]);
		if (t==null) {
			sender.sendMessage(ChatColor.RED+"Task with ID '"+args[5]+"' not found inside mission '"+q.getNameID()+"' inside quest '"+q.getNameID()+"'");
			return;
		}
		onHelp( params, sender, label, args);
		return;
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
			if (q==null) {
				return list;
			}
			Mission m = q.getMissionByNameID(args[3]);
			if (m==null) {
				return list;
			}
			Completer.completeTasks(list, params.get(0), m.getTasks());
			return list;
		}
		params.remove(0);
		return super.onTab(params, sender, label, args);	
	}
}
class SubTaskSubEditor extends SubCmdManager {
	SubTaskSubEditor() {
		super("editor",Perms.ADMIN_EDITOR);
		this.setDescription(ChatColor.GOLD+"Open Gui Editor for Task");
		this.setPlayersOnly(true);
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		Task t = m.getTaskByNameID(args[5]);
		if (t==null) {
			sender.sendMessage(ChatColor.RED+"Task with ID '"+args[5]+"' not found inside mission '"+q.getNameID()+"' inside quest '"+q.getNameID()+"'");
			return;
		}
		t.openEditorGui((Player) sender);
	}
}
class SubTaskSubInfo extends SubCmdManager {
	SubTaskSubInfo() {
		super("info",Perms.ADMIN_QUEST_MISSION_TASK_INFO);
		this.setDescription(ChatColor.GOLD+"Shows mission info");
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		Task t = m.getTaskByNameID(args[5]);
		if (t==null) {
			sender.sendMessage(ChatColor.RED+"Task with ID '"+args[5]+"' not found inside mission '"+q.getNameID()+"' inside quest '"+q.getNameID()+"'");
			return;
		}
		sender.spigot().sendMessage(t.toComponent());
	}
}
