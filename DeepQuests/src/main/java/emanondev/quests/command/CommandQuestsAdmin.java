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

public class CommandQuestsAdmin implements TabExecutor {
	public CommandQuestsAdmin() {
		Quests.getInstance().registerCommand("questsadmin", this,
				"qa","questadmin","questsadmin","qadmin");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		switch (args.length) {
		case 1:
			list.addAll(Arrays.asList("reload","quest"));
			return list;
		case 2: 
			if (args[0].equalsIgnoreCase("quest"))
				for (Quest q : Quests.getInstance().getQuestManager().getQuests())
					list.add(q.getNameID());
			return list;
		case 3:
			if (args[0].equalsIgnoreCase("quest"))
				list.addAll(Arrays.asList("mission","info"));
			return list;
		case 4:
			if (args[0].equalsIgnoreCase("quest")&&args[2].equalsIgnoreCase("mission")) {
				Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[2]);
				if (q==null)
					return list;
				for (Mission m : q.getMissions())
					list.add(m.getNameID());
			}
			return list;
		case 5:
			if (args[0].equalsIgnoreCase("quest")&&args[2].equalsIgnoreCase("mission"))
				list.addAll(Arrays.asList("task","info"));

			return list;
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
		if (args.length==5) {
			Quest q = Quests.getInstance().getQuestManager().getQuestByNameID(args[1]);
			Mission m = q.getMissionByNameID(args[3]);
			sender.sendMessage("&lMission &r"+m.getDisplayName()
					+ "\nID "+m.getNameID()
					+ "\nRepeatable "+m.isRepetable()
					+ "\nRepeatTime "+m.getCooldownTime()
					//+ "\n"+m.);
					);
		}
		
	}

	private void help(CommandSender sender) {
		// TODO Auto-generated method stub
		
	}

}
