package emanondev.quests.command;

import java.util.ArrayList;

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

