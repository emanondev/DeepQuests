package emanondev.quests.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.Completer;
import net.md_5.bungee.api.ChatColor;

class SubMission extends SubCmdManager {
	SubMission() {
		super("mission",Perms.ADMIN_QUEST_MISSION,
				new SubMissionSubEditor(),
				new SubMissionSubInfo(),
				new SubMissionSubSetCooldown(),
				new SubMissionSubSetRepeatable(),
				new SubMissionSubSetDisplayName(),
				new SubMissionSubWorlds(),
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
		super("editor",Perms.ADMIN_QUEST_MISSION_EDITOR);
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
//qa 	quest 	<id> 	setdisplayname 	[name]
//		0		1		2				3+
class SubMissionSubSetDisplayName extends SubCmdManager {
	SubMissionSubSetDisplayName(){
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
		if (m.setDisplayName(displayName))
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


//qa 	quest 	<id> 	mission		<missid>	setrepeatable 	<true/false>
//		0		1		2			3			4				5
class SubMissionSubSetRepeatable extends SubCmdManager {
	SubMissionSubSetRepeatable(){
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
class SubMissionSubSetCooldown extends SubCmdManager {
	SubMissionSubSetCooldown(){
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

class SubMissionSubWorlds extends SubCmdManager {
	SubMissionSubWorlds() {
		super("worlds",Perms.ADMIN_QUEST_MISSION_WORLDS,
				new SubMissionSubWorldsSubAdd(),
				new SubMissionSubWorldsSubDelete(),
				new SubMissionSubWorldsSubSetBlacklist());
		this.setDescription(ChatColor.GOLD+"Edit allowes/unallowed worlds for the mission");
	}
}

class SubMissionSubWorldsSubAdd extends SubCmdManager {
	SubMissionSubWorldsSubAdd(){
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
class SubMissionSubWorldsSubDelete extends SubCmdManager {
	SubMissionSubWorldsSubDelete(){
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
		if (m.removeWorldToWorldList(params.get(0)))
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
class SubMissionSubWorldsSubSetBlacklist extends SubCmdManager {
	SubMissionSubWorldsSubSetBlacklist(){
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
		if (m.setWorldListBlackList(value))
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
