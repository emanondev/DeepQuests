package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.quest.Quest;
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
				new SubAddQuest(),
				new SubDeleteQuest()
				
				
				);
	}

	
	/* TODO
	 * qa
	 * 		quest <-- in sviluppo
	 * 			<quest>
	 * 				require
	 * 				reward
	 *				mission
	 * 					<mission> <- in sviluppo
	 * 						require
	 * 						reward
	 * 						task
	 * 							<task>
	 * 								setdisplayname
	 * 								worlds
	 * 									add
	 * 									remove
	 * 									setblacklist
	 * 						listtask
	 * 						addtask <taskid> <tasktype> <maxprogress> <displayname>
	 * 						deletetask
	 * 							<task>
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
//qa	addquest	<id>	[name...]
//		0			1		2+
class SubAddQuest extends SubCmdManager {
	SubAddQuest() {
		super("addquest",Perms.ADMIN_ADDQUEST);
		this.setDescription(ChatColor.GOLD+"Create a new quest with given id and name");
		this.setParams("<questID> [display name]");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0) {
			onHelp(params,sender,label,args);
			return;
		}
		String id = params.get(0).toLowerCase();
		if (id.contains (":") || id.contains(".")){
			sender.sendMessage(ChatColor.RED+"Invalid quest ID '"+id+"'");
			return;
		}
		if (Quests.getInstance().getQuestManager().getQuestByNameID(id)!=null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+id+"' already exist");
			return;
		}
		String displayName = null;
		if (params.size()>1) {
			params.remove(0);
			StringBuilder text = new StringBuilder("");
			for (String word : params)
				text.append(" "+word);
			displayName = text.toString().replaceFirst(" ","");
		}
		if (Quests.getInstance().getQuestManager().addQuest(id, displayName))
			sender.sendMessage(ChatColor.GREEN+"Quest added");
		else
			sender.sendMessage(ChatColor.RED+"Can't create quest");
	}
	
}
//qa	addquest	<id>
//		0			1
class SubDeleteQuest extends SubCmdManager {
	SubDeleteQuest() {
		super("deletequest",Perms.ADMIN_DELETEQUEST);
		this.setDescription(ChatColor.GOLD+"Delete selected quest\n"
				+ChatColor.RED+"Deleting a quest erase it forever\n"
				+ChatColor.RED+"Deleting can't be undone\n"
				+ChatColor.RED+"Also delete quest's missions and tasks");
		this.setParams("<questID>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params,sender,label,args);
			return;
		}
		
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(params.get(0));
		if (q == null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+params.get(0)+"' do not exist");
			return;
		}
		Quests.getInstance().getQuestManager().deleteQuest(q);
		sender.sendMessage(ChatColor.GREEN+"Quest '"+q.getNameID()+"' deleted");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		if (params.size()==1) {
			Completer.completeQuests(list, params.get(0), Quests.getInstance().getQuestManager().getQuests());
		}
		return list;
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
//qa 	quest 	<questID>
//		0		1

//qa 	quest 	<id> 	mission		<id>
//		0		1		2			(3)


