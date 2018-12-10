package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.TextEditorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class CommandData extends QCData {
	private static final String PATH_COMMAND = "command";
	private String command;

	public CommandData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		command = section.getString(PATH_COMMAND);
	}
	public String getCommand() {
		return command;
	}
	public boolean setCommand(String cmd) {
		if (cmd!=null && cmd.equals(command))
			return false;
		if (cmd==null && command==null)
			return false;
		this.command = cmd;
		getSection().set(PATH_COMMAND,command);
		setDirty(true);
		return true;
	}
	public Button getCommandEditorButton(Gui gui) {
		return new CommandEditorButton(gui);
	}
	
	private class CommandEditorButton extends TextEditorButton {

		public CommandEditorButton(Gui parent) {
			super(new ItemBuilder(Material.COMMAND_BLOCK).setGuiProperty().build(), parent);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lReward Command Editor");
			desc.add("&6Click to edit");
			desc.add("&7Current value:");
			if (command!=null)
				desc.add("&7'&f"+command+"&7'");
			else
				desc.add("&7no command is set");
			desc.add("");
			desc.add("&7Represent the command of the Reward");
			return desc;
		}

		@Override
		public void onReicevedText(String text) {
			setCommand(text);
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			this.requestText(clicker, StringUtils.revertColors(command), changeCommandHelp);
		}
		
	}
	private static final BaseComponent[] changeCommandHelp = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command executed by console to set as reward\n\n"+
			ChatColor.GOLD+"or just Change override old command with new command\n"+
			ChatColor.YELLOW+"/questtext <new command>\n\n"+
			ChatColor.GRAY+"use %player% as playername holder"
			).create();

}
