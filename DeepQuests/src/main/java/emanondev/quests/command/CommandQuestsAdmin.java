package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import emanondev.quests.Perms;
import emanondev.quests.Quests;
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
class SubQuest extends SubCmdManager {
	SubQuest() {
		super("quest",Perms.ADMIN_QUEST,
				new SubQuestSubInfo(),
				new SubQuestSubListMission(),
				new SubQuestSubMission(),
				new SubQuestSubSetRepeatable(),
				new SubQuestSubSetCooldown(),
				new SubQuestSubSetDisplayName(),
				new SubQuestSubDeleteMission(),
				new SubQuestSubAddMission(),
				new SubQuestSubWorlds()
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

class SubQuestSubWorlds extends SubCmdManager {
	SubQuestSubWorlds() {
		super("worlds",Perms.ADMIN_QUEST_WORLDS,
				new SubQuestSubWorldsSubAdd(),
				new SubQuestSubWorldsSubDelete(),
				new SubQuestSubWorldsSubSetBlacklist());
		this.setDescription(ChatColor.GOLD+"Edit allowes/unallowed worlds for the quest");
	}
}

class SubQuestSubWorldsSubAdd extends SubCmdManager {
	SubQuestSubWorldsSubAdd(){
		super("add",null);
		this.setDescription(ChatColor.GOLD+"Add selected world to list");
		this.setParams("<world>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		if (q.addWorldToWorldList(params.get(0)))
			sender.sendMessage(ChatColor.GREEN+"World '"+params.get(0)+"' added to quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"World '"+params.get(0)+"' is already set on quest '"+q.getDisplayName()
				+ChatColor.RED+"' ["+q.getNameID()+"]");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeWorlds(list, params.get(0), Bukkit.getWorlds());
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
//qa	quest	<questid>	worlds	deleteworld	<world>
//		0		1			2		3			(4)
class SubQuestSubWorldsSubDelete extends SubCmdManager {
	SubQuestSubWorldsSubDelete(){
		super("delete",null);
		this.setDescription(ChatColor.GOLD+"Remove selected world from list");
		this.setParams("<world>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		if (q.removeWorldToWorldList(params.get(0)))
			sender.sendMessage(ChatColor.GREEN+"World '"+params.get(0)+"' removed from quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"World '"+params.get(0)+"' was not found on quest '"+q.getDisplayName()
				+ChatColor.RED+"' ["+q.getNameID()+"]");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
			ArrayList<String> list = new ArrayList<String>();
			if (q==null) {
				return list;
			}
			Completer.complete(list, params.get(0), q.getWorldsList());
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
class SubQuestSubWorldsSubSetBlacklist extends SubCmdManager {
	SubQuestSubWorldsSubSetBlacklist(){
		super("setblacklist",null);
		this.setDescription(ChatColor.GOLD+"Set the worlds as blacklist (true) or as whitelist (false)");
		this.setParams("<true/false>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		boolean value;
		try {
			 if ("true".equalsIgnoreCase(params.get(0)))
				 value = true;
			 else if ("false".equalsIgnoreCase(params.get(0)))
				 value = false;
			 else
				 throw new IllegalArgumentException();
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+params.get(0)+"' is not a valid boolean");
			return;
		}
		if (q.setWorldListBlackList(value))
			if (value == true)
				sender.sendMessage(ChatColor.GREEN+"Worlds on quest "+q.getDisplayName()
					+ChatColor.GREEN+" ["+q.getNameID()+"] are now a BlackList");
			else
				sender.sendMessage(ChatColor.GREEN+"Worlds on quest "+q.getDisplayName()
					+ChatColor.GREEN+" ["+q.getNameID()+"] are now a Whitelist");
		else
			if (value == true)
				sender.sendMessage(ChatColor.RED+"Worlds on quest "+q.getDisplayName()
					+ChatColor.RED+" ["+q.getNameID()+"] is already a BlackList");
			else
				sender.sendMessage(ChatColor.RED+"Worlds on quest "+q.getDisplayName()
					+ChatColor.RED+" ["+q.getNameID()+"] is already a Whitelist");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeBoolean(list, params.get(0));
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
	
}
//qa	quest	<questid>	addmission	<id>	[displayname]
//		0		1			2			3		4+
class SubQuestSubAddMission extends SubCmdManager {
	SubQuestSubAddMission() {
		super("addmission",Perms.ADMIN_QUEST_ADDMISSION);
		this.setDescription(ChatColor.GOLD+"Create a new quest with given id and name");
		this.setParams("<missionID> [display name]");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0) {
			onHelp(params,sender,label,args);
			return;
		}
		String id = params.get(0).toLowerCase();
		if (id.contains (":") || id.contains(".")){
			sender.sendMessage(ChatColor.RED+"Invalid mission ID '"+id+"'");
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		if (q.getMissionByNameID(id)!=null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+id+"' on quest "+q.getDisplayName()+
					ChatColor.RED+" ["+q.getNameID()+"] already exist");
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
		if (q.addMission(id, displayName))
			sender.sendMessage(ChatColor.GREEN+"Mission '"+id+"' added on quest "+q.getDisplayName()+
					ChatColor.GREEN+" ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"Can't create Mission '"+id+"' on quest "+q.getDisplayName()+
					ChatColor.RED+" ["+q.getNameID()+"]");
	}
	
}
//qa	quest	<questid>	deletemission	<id>
//		0		1			2				3
class SubQuestSubDeleteMission extends SubCmdManager {
	SubQuestSubDeleteMission() {
		super("deletemission",Perms.ADMIN_QUEST_DELETEMISSION);
		this.setDescription(ChatColor.GOLD+"Delete selected quest\n"
					+ChatColor.RED+"Deleting a mission erase it forever\n"
					+ChatColor.RED+"Deleting can't be undone\n"
					+ChatColor.RED+"Also delete mission's and tasks");
		this.setParams("<questID>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params,sender,label,args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found on Quest '"+q.getDisplayName()
				+ChatColor.RED+"' ["+q.getNameID()+"]");
			return;
		}
		q.deleteMission(m);
		sender.sendMessage(ChatColor.GREEN+"Mission '"+m.getDisplayName()
			+ChatColor.GREEN+"' ["+m.getNameID()+"] deleted from Quest '"+q.getDisplayName()
			+ChatColor.GREEN+"' ["+q.getNameID()+"]");
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
//qa 	quest 	<id> 	setdisplayname 	[name]
//		0		1		2				3+
class SubQuestSubSetDisplayName extends SubCmdManager {
	SubQuestSubSetDisplayName(){
		super("setdisplayname",Perms.ADMIN_QUEST_SETDISPLAYNAME
				);
		this.setDescription(ChatColor.GOLD+"Set a new display name for selected quest");
		this.setParams("[display name]");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0) {
			onHelp(params,sender, label, args);
			return;
		}
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		String displayName = null;
		if (params.size()>0) {
			StringBuilder text = new StringBuilder("");
			for (String word : params)
				text.append(" "+word);
			displayName = text.toString().replaceFirst(" ","");
		}
		if (q.setDisplayName(displayName))
			sender.sendMessage(ChatColor.GREEN+"Quest ["+q.getNameID()+"] has now '"+q.getDisplayName()
					+ChatColor.GREEN+"' as display name");
		else
			sender.sendMessage(ChatColor.RED+"Quest ["+q.getNameID()+"] has already '"+q.getDisplayName()
					+ChatColor.RED+"' as display name");
	}
}
//qa 	quest 	<id> 	setrepeatable 	<true/false>
//		0		1		2				3
class SubQuestSubSetRepeatable extends SubCmdManager {
	SubQuestSubSetRepeatable(){
		super("setrepeatable",Perms.ADMIN_QUEST_SETREPEATABLE
				);
		this.setDescription(ChatColor.GOLD+"Set the quest as repeatable");
		this.setParams("<true/false>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (params.size() != 1) {
			onHelp(params,sender,label,args);
			return;
		}
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		boolean value;
		try {
			 if ("true".equalsIgnoreCase(args[3]))
				 value = true;
			 else if ("false".equalsIgnoreCase(args[3]))
				 value = false;
			 else
				 throw new IllegalArgumentException();
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+args[3]+"' is not a valid boolean");
			return;
		}
		if (q.setRepeatable(value))
			if (value == true)
				sender.sendMessage(ChatColor.GREEN+"Quest "+q.getDisplayName()
							+ChatColor.GREEN+" ["+q.getNameID()+"] is now repeatable");
			else
				sender.sendMessage(ChatColor.GREEN+"Quest "+q.getDisplayName()
					+ChatColor.GREEN+" ["+q.getNameID()+"] is no more repeatable");
		else
			if (value == true)
				sender.sendMessage(ChatColor.RED+"Quest "+q.getDisplayName()
					+ChatColor.RED+" ["+q.getNameID()+"] is already repeatable");
			else
				sender.sendMessage(ChatColor.RED+"Quest "+q.getDisplayName()
					+ChatColor.RED+" ["+q.getNameID()+"] is already not repeatable");
				
	}

	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeBoolean(list, params.get(0));
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
//qa 	quest 	<id> 	setcooldown 	<minutes>
//		0		1		2				3
class SubQuestSubSetCooldown extends SubCmdManager {
	SubQuestSubSetCooldown(){
		super("setcooldown",Perms.ADMIN_QUEST_SETCOOLDOWN
				);
		this.setDescription(ChatColor.GOLD+"Set the quest cooldown (minutes)");
		this.setParams("<minutes>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (params.size() != 1) {
			onHelp(params,sender,label,args);
			return;
		}
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		int minutes;
		try {
			minutes = Integer.valueOf(args[3]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+args[3]+"' is not a number");
			return;
		}
		if (q.setCooldownTime(minutes))
			sender.sendMessage(ChatColor.GREEN+"Quest "+q.getDisplayName()
				+ChatColor.GREEN+" ["+q.getNameID()+"] has now "+Math.max(0, minutes)+" minutes of cooldown");
		else
			sender.sendMessage(ChatColor.RED+"Quest "+q.getDisplayName()
				+ChatColor.RED+" ["+q.getNameID()+"] has already "+Math.max(0, minutes)+" minutes of cooldown");
				
	}
}

//qa 	quest 	<id> 	listmission
//		0		1		2
class SubQuestSubListMission extends SubCmdManager {
	SubQuestSubListMission() {
		super("listmission",Perms.ADMIN_QUEST_LISTMISSION);
		this.setDescription(ChatColor.GOLD+"Shows registered missions list on the quest");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		
		ComponentBuilder comp = new ComponentBuilder(
				""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"  Mission List  "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----\n");
		boolean val = false;
		for ( Mission mission : q.getMissions()) {
			if (val==false)
				comp.append(ChatColor.YELLOW+mission.getDisplayName()+" ");
			else
				comp.append(ChatColor.GOLD+mission.getDisplayName()+" ");
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(
					ChatColor.GOLD+"Click to examine Mission\n"+
					ChatColor.DARK_AQUA+"ID: "+ChatColor.AQUA+mission.getNameID()+"\n"+
					ChatColor.DARK_AQUA+"Tasks: "+ChatColor.AQUA+mission.getTasks().size()
					).create()));
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/qa quest "+q.getNameID()+" mission "+mission.getNameID()+" info"));
			val = !val;
		}
		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"  Mission List  "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		sender.spigot().sendMessage(comp.create());
	}
}
//qa 	quest 	<id> 	mission		<id>
//		0		1		2			(3)
class SubQuestSubMission extends SubCmdManager {
	SubQuestSubMission() {
		super("mission",Perms.ADMIN_QUEST_MISSION,
				new SubQuestSubMissionSubInfo(),
				new SubQuestSubMissionSubSetCooldown(),
				new SubQuestSubMissionSubSetRepeatable(),
				new SubQuestSubMissionSubSetDisplayName()
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

//qa 	quest 	<id> 	setdisplayname 	[name]
//		0		1		2				3+
class SubQuestSubMissionSubSetDisplayName extends SubCmdManager {
	SubQuestSubMissionSubSetDisplayName(){
		super("setdisplayname",Perms.ADMIN_QUEST_MISSION_SETDISPLAYNAME
				);
		this.setDescription(ChatColor.GOLD+"Set a new display name for selected mission");
		this.setParams("[display name]");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0) {
			onHelp(params,sender, label, args);
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
		String displayName = null;
		if (params.size()>0) {
			StringBuilder text = new StringBuilder("");
			for (String word : params)
				text.append(" "+word);
			displayName = text.toString().replaceFirst(" ","");
		}
		if (q.setDisplayName(displayName))
			sender.sendMessage(ChatColor.GREEN+"Mission ["+m.getNameID()+"] of quest "+q.getDisplayName() 
				+ChatColor.GREEN+" ["+q.getNameID()+"] has now '"+m.getDisplayName()
				+ChatColor.GREEN+"' as display name");
		else
			sender.sendMessage(ChatColor.RED+"Mission ["+m.getNameID()+"] of quest "+q.getDisplayName() 
				+ChatColor.RED+" ["+q.getNameID()+"] has already '"+m.getDisplayName()
				+ChatColor.RED+"' as display name");
	}
}
//qa 	quest 	<id> 	mission		<id>	info
//		0		1		2			3		4
class SubQuestSubMissionSubInfo extends SubCmdManager {
	SubQuestSubMissionSubInfo() {
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


//qa 	quest 	<id> 	mission		<missid>	setrepeatable 	<true/false>
//		0		1		2			3			4				5
class SubQuestSubMissionSubSetRepeatable extends SubCmdManager {
	SubQuestSubMissionSubSetRepeatable(){
		super("setrepeatable",Perms.ADMIN_QUEST_MISSION_SETREPEATABLE
				);
		this.setDescription(ChatColor.GOLD+"Set the mission as repeatable");
		this.setParams("<true/false>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (params.size() != 1) {
			onHelp(params,sender,label,args);
			return;
		}
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		boolean value;
		try {
			 if ("true".equalsIgnoreCase(args[5]))
				 value = true;
			 else if ("false".equalsIgnoreCase(args[5]))
				 value = false;
			 else
				 throw new IllegalArgumentException();
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+args[5]+"' is not a valid boolean");
			return;
		}
		if (q.setRepeatable(value))
			if (value == true)
				sender.sendMessage(ChatColor.GREEN+"Mission "+m.getDisplayName()
					+ChatColor.GREEN+" ["+m.getNameID()+"] of quest "+q.getDisplayName() 
					+ChatColor.GREEN+" ["+q.getNameID()+"] is now repeatable");
			else
				sender.sendMessage(ChatColor.GREEN+"Mission "+m.getDisplayName()
					+ChatColor.GREEN+" ["+m.getNameID()+"] of quest "+q.getDisplayName() 
					+ChatColor.GREEN+" ["+q.getNameID()+"] is no more repeatable");
		else
			if (value == true)
				sender.sendMessage(ChatColor.RED+"Mission "+m.getDisplayName()
					+ChatColor.RED+" ["+m.getNameID()+"] of quest "+q.getDisplayName() 
					+ChatColor.RED+" ["+q.getNameID()+"] is already repeatable");
			else
				sender.sendMessage(ChatColor.RED+"Mission "+m.getDisplayName()
					+ChatColor.RED+" ["+m.getNameID()+"] of quest "+q.getDisplayName() 
					+ChatColor.RED+" ["+q.getNameID()+"] is already not repeatable");
				
	}

	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeBoolean(list, params.get(0));
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
//qa 	quest 	<id> 	mission		<missid>	setcooldown 	<minutes>
//		0		1		2			3			4				5
class SubQuestSubMissionSubSetCooldown extends SubCmdManager {
	SubQuestSubMissionSubSetCooldown(){
		super("setcooldown",Perms.ADMIN_QUEST_MISSION_SETCOOLDOWN
				);
		this.setDescription(ChatColor.GOLD+"Set the mission cooldown (minutes)");
		this.setParams("<minutes>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (params.size() != 1) {
			onHelp(params,sender,label,args);
			return;
		}
		if (q==null) {
			sender.sendMessage(ChatColor.RED+"Quest with ID '"+args[1]+"' not found");
			return;
		}
		Mission m = q.getMissionByNameID(args[3]);
		if (m==null) {
			sender.sendMessage(ChatColor.RED+"Mission with ID '"+args[3]+"' not found inside quest '"+q.getNameID()+"'");
			return;
		}
		int minutes;
		try {
			minutes = Integer.valueOf(args[5]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+args[5]+"' is not a number");
			return;
		}
		if (q.setCooldownTime(minutes))
			sender.sendMessage(ChatColor.GREEN+"Mission "+m.getDisplayName()
				+ChatColor.GREEN+" ["+m.getNameID()+"] of quest "+q.getDisplayName() 
				+ChatColor.GREEN+" ["+q.getNameID()+"] has now "+Math.max(0, minutes)+" minutes of cooldown");
		else
			sender.sendMessage(ChatColor.RED+"Mission "+m.getDisplayName()
				+ChatColor.RED+" ["+m.getNameID()+"] of quest "+q.getDisplayName() 
				+ChatColor.RED+" ["+q.getNameID()+"] has already "+Math.max(0, minutes)+" minutes of cooldown");
				
	}
}

class SubQuestSubMissionSubWorlds extends SubCmdManager {
	SubQuestSubMissionSubWorlds() {
		super("worlds",Perms.ADMIN_QUEST_MISSION_WORLDS,
				new SubQuestSubMissionSubWorldsSubAdd(),
				new SubQuestSubMissionSubWorldsSubDelete(),
				new SubQuestSubMissionSubWorldsSubSetBlacklist());
		this.setDescription(ChatColor.GOLD+"Edit allowes/unallowed worlds for the mission");
	}
}

class SubQuestSubMissionSubWorldsSubAdd extends SubCmdManager {
	SubQuestSubMissionSubWorldsSubAdd(){
		super("add",null);
		this.setDescription(ChatColor.GOLD+"Add selected world to list");
		this.setParams("<world>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
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
		if (m.addWorldToWorldList(params.get(0)))
			sender.sendMessage(ChatColor.GREEN+"World '"+params.get(0)+"' added to mission '"+m.getDisplayName()
					+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"World '"+params.get(0)+"' is already set on mission '"+m.getDisplayName() 
					+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.RED+"' ["+q.getNameID()+"]");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeWorlds(list, params.get(0), Bukkit.getWorlds());
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
//qa	quest	<questid>	worlds	deleteworld	<world>
//		0		1			2		3			(4)
class SubQuestSubMissionSubWorldsSubDelete extends SubCmdManager {
	SubQuestSubMissionSubWorldsSubDelete(){
		super("delete",null);
		this.setDescription(ChatColor.GOLD+"Remove selected world from list");
		this.setParams("<world>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
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
		if (q.removeWorldToWorldList(params.get(0)))
			sender.sendMessage(ChatColor.GREEN+"World '"+params.get(0)+"' removed from mission '"+m.getDisplayName()
				+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
				+ChatColor.GREEN+"' ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"World '"+params.get(0)+"' was not found on mission '"+m.getDisplayName() 
				+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
				+ChatColor.RED+"' ["+q.getNameID()+"]");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
			ArrayList<String> list = new ArrayList<String>();
			if (q==null) {
				return list;
			}
			Completer.complete(list, params.get(0), q.getWorldsList());
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
class SubQuestSubMissionSubWorldsSubSetBlacklist extends SubCmdManager {
	SubQuestSubMissionSubWorldsSubSetBlacklist(){
		super("setblacklist",null);
		this.setDescription(ChatColor.GOLD+"Set the worlds as blacklist (true) or as whitelist (false)");
		this.setParams("<true/false>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
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
		boolean value;
		try {
			 if ("true".equalsIgnoreCase(params.get(0)))
				 value = true;
			 else if ("false".equalsIgnoreCase(params.get(0)))
				 value = false;
			 else
				 throw new IllegalArgumentException();
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+params.get(0)+"' is not a valid boolean");
			return;
		}
		if (q.setWorldListBlackList(value))
			if (value == true)
				sender.sendMessage(ChatColor.GREEN+"Worlds on mission '"+m.getDisplayName()
					+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"] are now a BlackList");
			else
				sender.sendMessage(ChatColor.GREEN+"Worlds on mission '"+m.getDisplayName()
					+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"] are now a Whitelist");
		else
			if (value == true)
				sender.sendMessage(ChatColor.RED+"Worlds on mission '"+m.getDisplayName()
					+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.RED+"' ["+q.getNameID()+"] is already a BlackList");
			else
				sender.sendMessage(ChatColor.RED+"Worlds on mission '"+m.getDisplayName()
					+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.RED+"' ["+q.getNameID()+"] is already a Whitelist");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeBoolean(list, params.get(0));
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
	
}

class SubQuestSubMissionSubTask extends SubCmdManager {
	SubQuestSubMissionSubTask() {
		super("task",Perms.ADMIN_QUEST_MISSION_TASK,
				new SubQuestSubMissionSubTaskSubInfo()
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
class SubQuestSubMissionSubTaskSubInfo extends SubCmdManager {
	SubQuestSubMissionSubTaskSubInfo() {
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

class SubQuestSubMissionSubTaskSubWorlds extends SubCmdManager {
	SubQuestSubMissionSubTaskSubWorlds() {
		super("worlds",Perms.ADMIN_QUEST_MISSION_TASK_WORLDS,
				new SubQuestSubMissionSubTaskSubWorldsSubAdd(),
				new SubQuestSubMissionSubTaskSubWorldsSubDelete(),
				new SubQuestSubMissionSubTaskSubWorldsSubSetBlacklist());
		this.setDescription(ChatColor.GOLD+"Edit allowes/unallowed worlds for the task");
	}
}

class SubQuestSubMissionSubTaskSubWorldsSubAdd extends SubCmdManager {
	SubQuestSubMissionSubTaskSubWorldsSubAdd(){
		super("add",null);
		this.setDescription(ChatColor.GOLD+"Add selected world to list");
		this.setParams("<world>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
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
		if (m.addWorldToWorldList(params.get(0)))
			sender.sendMessage(
					ChatColor.GREEN+"World '"+params.get(0)+"' added to task '"+t.getDisplayName()
					+ChatColor.GREEN+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName()
					+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"World '"+params.get(0)+"' is already set on task '"+t.getDisplayName()
					+ChatColor.RED+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName() 
					+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.RED+"' ["+q.getNameID()+"]");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeWorlds(list, params.get(0), Bukkit.getWorlds());
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
//qa	quest	<questid>	worlds	deleteworld	<world>
//		0		1			2		3			(4)
class SubQuestSubMissionSubTaskSubWorldsSubDelete extends SubCmdManager {
	SubQuestSubMissionSubTaskSubWorldsSubDelete(){
		super("delete",null);
		this.setDescription(ChatColor.GOLD+"Remove selected world from list");
		this.setParams("<world>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
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
		if (q.removeWorldToWorldList(params.get(0)))
			sender.sendMessage(ChatColor.GREEN+"World '"+params.get(0)+"' removed from task '"+t.getDisplayName()
				+ChatColor.GREEN+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName()
				+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
				+ChatColor.GREEN+"' ["+q.getNameID()+"]");
		else
			sender.sendMessage(ChatColor.RED+"World '"+params.get(0)+"' was not found on task '"+t.getDisplayName()
				+ChatColor.RED+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName() 
				+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
				+ChatColor.RED+"' ["+q.getNameID()+"]");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==0)
			return new ArrayList<String>();
		if (params.size()==1) {
			Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
			ArrayList<String> list = new ArrayList<String>();
			if (q==null) {
				return list;
			}
			Completer.complete(list, params.get(0), q.getWorldsList());
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
}
class SubQuestSubMissionSubTaskSubWorldsSubSetBlacklist extends SubCmdManager {
	SubQuestSubMissionSubTaskSubWorldsSubSetBlacklist(){
		super("setblacklist",null);
		this.setDescription(ChatColor.GOLD+"Set the worlds as blacklist (true) or as whitelist (false)");
		this.setParams("<true/false>");
	}
	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()!=1) {
			onHelp(params, sender, label, args);
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
		boolean value;
		try {
			 if ("true".equalsIgnoreCase(params.get(0)))
				 value = true;
			 else if ("false".equalsIgnoreCase(params.get(0)))
				 value = false;
			 else
				 throw new IllegalArgumentException();
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED+"Value '"+params.get(0)+"' is not a valid boolean");
			return;
		}
		if (q.setWorldListBlackList(value))
			if (value == true)
				sender.sendMessage(ChatColor.GREEN+"Worlds on task '"+t.getDisplayName()
					+ChatColor.GREEN+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName()
					+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"] are now a BlackList");
			else
				sender.sendMessage(ChatColor.GREEN+"Worlds on task '"+t.getDisplayName()
					+ChatColor.RED+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName()
					+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.GREEN+"' ["+q.getNameID()+"] are now a Whitelist");
		else
			if (value == true)
				sender.sendMessage(ChatColor.RED+"Worlds on task '"+t.getDisplayName()
					+ChatColor.RED+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName()
					+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.RED+"' ["+q.getNameID()+"] is already a BlackList");
			else
				sender.sendMessage(ChatColor.RED+"Worlds on task '"+t.getDisplayName()
					+ChatColor.RED+"' ["+t.getNameID()+"] of mission '"+m.getDisplayName()
					+ChatColor.RED+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName()
					+ChatColor.RED+"' ["+q.getNameID()+"] is already a Whitelist");
	}
	@Override
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			Completer.completeBoolean(list, params.get(0));
			return list;
		}
		return super.onTab(params, sender, label, args);
	}
	
}