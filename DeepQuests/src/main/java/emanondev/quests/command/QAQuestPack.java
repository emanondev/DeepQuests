package emanondev.quests.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.Perms;
import emanondev.quests.Quests;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.Completer;
import net.md_5.bungee.api.ChatColor;

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
