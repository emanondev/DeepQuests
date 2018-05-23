package emanondev.quests.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.Completer;
import net.md_5.bungee.api.ChatColor;

class SubTask extends SubCmdManager {
	SubTask() {
		super("task",Perms.ADMIN_QUEST_MISSION_TASK,
				new SubTaskSubEditor(),
				new SubTaskSubInfo(),
				//new SubTaskSubWorlds(),
				new SubTaskSubSetDisplayName()
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
class SubTaskSubSetDisplayName extends SubCmdManager {
	SubTaskSubSetDisplayName(){
		super("setdisplayname",Perms.ADMIN_EDITOR
				);
		this.setDescription(ChatColor.GOLD+"Set a new display name for selected task");
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
		Task t = m.getTaskByNameID(args[5]);
		if (t==null) {
			sender.sendMessage(ChatColor.RED+"Task with ID '"+args[5]+"' not found inside mission '"+q.getNameID()+"' inside quest '"+q.getNameID()+"'");
			return;
		}
		String displayName = null;
		if (params.size()>0) {
			StringBuilder text = new StringBuilder("");
			for (String word : params)
				text.append(" "+word);
			displayName = text.toString().replaceFirst(" ","");
		}
		if (t.setDisplayName(displayName))
			sender.sendMessage(ChatColor.GREEN+"Task ["+t.getNameID()+"] of mission '"+m.getDisplayName()
				+ChatColor.GREEN+"' ["+m.getNameID()+"] of quest '"+q.getDisplayName() 
				+ChatColor.GREEN+"' ["+q.getNameID()+"] has now '"+t.getDisplayName()
				+ChatColor.GREEN+"' as display name");
		else
			sender.sendMessage(ChatColor.RED+"Task ["+t.getNameID()+"] of mission '"+m.getDisplayName()
				+ChatColor.RED+"' ["+m.getNameID()+"] of quest "+q.getDisplayName() 
				+ChatColor.RED+" ["+q.getNameID()+"] has already '"+t.getDisplayName()
				+ChatColor.RED+"' as display name");
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
/*
class SubTaskSubWorlds extends SubCmdManager {
	SubTaskSubWorlds() {
		super("worlds",Perms.ADMIN_QUEST_MISSION_TASK_WORLDS,
				new SubTaskSubWorldsSubAdd(),
				new SubTaskSubWorldsSubDelete(),
				new SubTaskSubWorldsSubSetBlacklist());
		this.setDescription(ChatColor.GOLD+"Edit allowes/unallowed worlds for the task");
	}
}

class SubTaskSubWorldsSubAdd extends SubCmdManager {
	SubTaskSubWorldsSubAdd(){
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
		if (t.addWorldToWorldList(params.get(0)))
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
class SubTaskSubWorldsSubDelete extends SubCmdManager {
	SubTaskSubWorldsSubDelete(){
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
		if (t.removeWorldToWorldList(params.get(0)))
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
class SubTaskSubWorldsSubSetBlacklist extends SubCmdManager {
	SubTaskSubWorldsSubSetBlacklist(){
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
		if (t.setWorldListBlackList(value))
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
*/