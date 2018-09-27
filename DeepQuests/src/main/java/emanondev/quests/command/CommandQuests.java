package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.Quests;
import emanondev.quests.newgui.gui.QuestsMenu;

public class CommandQuests extends CmdManager {
	public CommandQuests() {
		super("quests",Arrays.asList("quest","q"),null);
		this.setPlayersOnly(true);
	}

	@Override
	public void onCmd(ArrayList<String> params,CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		p.openInventory(new QuestsMenu(p,null,Quests.get().getQuestManager()).getInventory());
	}
	

}
