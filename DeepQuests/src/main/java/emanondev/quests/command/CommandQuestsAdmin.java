package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.Completer;

public class CommandQuestsAdmin implements TabExecutor {
	public CommandQuestsAdmin() {
		Quests.getInstance().registerCommand("questsadmin", this,
				"qa","questadmin","questsadmin","qadmin");
	}

	
	/*
	 * qa
	 * 		reload
	 * 		listquest
	 * 		addquest
	 * 		deletequest
	 * 			<quest>
	 * 		quest
	 * 			<quest>
	 * 				info
	 * 				require
	 * 				reward
	 * 				reset
	 * 					<player>
	 * 				lock
	 * 					<player>
	 * 				title
	 * 				mission
	 * 					<mission>
	 * 						info
	 * 						require
	 * 						reward
	 * 						reset
	 * 							<player>
	 * 						lock
	 * 							<player>
	 * 						title
	 * 						task
	 * 							<task>
	 * 								info
	 * 						listtask
	 * 						addtask
	 * 						deletetask
	 * 							<task>
	 * 				listmission
	 * 				addmission
	 * 				deletemission
	 * 					<mission>
	 * 		
	 * 		
	 */
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		String prefix = args[args.length-1];
		switch (args.length) {
		case 1:
			Completer.complete(list, prefix, "reload","quest","listquest","addquest","deletequest");
			return list;
		case 2: 
			if (args[0].equalsIgnoreCase("quest")||args[0].equalsIgnoreCase("deletequest"))
				Completer.completeQuests(list, prefix, 
						Quests.getInstance().getQuestManager().getQuests());
			return list;
		case 3:
			if (args[0].equalsIgnoreCase("quest"))
				Completer.complete(list, prefix,"mission","info","require","reward","reset","lock",
						"title","listmission","addmission","deletemission");
			return list;
		case 4:
			if (args[0].equalsIgnoreCase("quest")) {
				if (args[2].equalsIgnoreCase("mission")||args[2].equalsIgnoreCase("deletemission")) {
					Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
					if (q==null)
						return list;
					Completer.completeMissions(list, prefix,q.getMissions());
				}
				else
					if (args[2].equalsIgnoreCase("reset")||args[2].equalsIgnoreCase("lock"))
						Completer.completePlayerNames(list,prefix);
			}
			return list;
		case 5:
			if (args[0].equalsIgnoreCase("quest")&&args[2].equalsIgnoreCase("mission"))
				list.addAll(Arrays.asList("listtask","addtask","deletetask","task","info"));
			return list;
		case 6:
			if (args[0].equalsIgnoreCase("quest") && args[2].equalsIgnoreCase("mission")) {
				if (args[4].equalsIgnoreCase("task")||args[4].equalsIgnoreCase("deletetask")) {
					Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
					if (q==null)
						return list;
					Mission m = q.getMissionByNameID(args[3]);
					if (m==null)
						return list;
					Completer.completeTasks(list, prefix,m.getTasks());
				}
				else
					if (args[2].equalsIgnoreCase("reset")||args[2].equalsIgnoreCase("lock"))
						Completer.completePlayerNames(list,prefix);
			}
				
		}
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==0) {
			help(sender);
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		case "reload":
			Quests.getInstance().reload();
			sender.sendMessage("Plugin Reloaded");
			return true;
		case "quest":
			quest(sender,args);
		default:
			help(sender);
			return true;
		}
	}

	private void quest(CommandSender sender, String[] args) {
		if (args.length<=1)
			return;
		Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
		if (q == null) {
			sender.sendMessage("quest "+args[1]+" not found");
			return;
		}
		if (args.length==2 || args[2].equalsIgnoreCase("info")) {
			sender.spigot().sendMessage(q.toComponent());
			return;
		}
			
		if (args.length==5) {
			Mission m = q.getMissionByNameID(args[3]);
			if (m==null) {
				sender.sendMessage("mission "+args[3]+" not found inside quest "+args[1]);
				return;
			}
				
			sender.spigot().sendMessage(m.toComponent());
		}
		
	}

	private void help(CommandSender sender) {
		// TODO Auto-generated method stub
		
	}

}
