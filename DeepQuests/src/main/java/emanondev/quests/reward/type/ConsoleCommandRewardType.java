package emanondev.quests.reward.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.CommandData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;

public class ConsoleCommandRewardType extends AbstractRewardType implements RewardType {
	
	public ConsoleCommandRewardType() {
		super("CONSOLECOMMAND");
	}

	public class ConsoleCommandReward extends AbstractReward implements Reward {
		private CommandData cmd;
		public ConsoleCommandReward(ConfigSection section,QuestComponent gui) {
			super(section, gui);
			cmd = new CommandData(section,this);
		}
		@Override
		public RewardType getType() {
			return ConsoleCommandRewardType.this;
		}
		
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (cmd.getCommand() == null)
				info.add("&9Command: &cnot setted");
			else
				info.add("&9Command: &e/" + cmd.getCommand());
			return info;
		}
		
		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			if (cmd.getCommand()==null)
				return;
			String command = cmd.getCommand().replace("%player%", qPlayer.getPlayer().getName()).replace("%player_name%", qPlayer.getPlayer().getName());
			for (int i = 0; i<amount ; i++)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
		}
		
		@Override
		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(1, cmd.getCommandEditorButton(parent));
			return gui;
		}
		
		
	}
	
		
	@Override
	public Material getGuiItemMaterial() {
		return Material.COMMAND;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(
				"&7Perform a console command,",
				"&7%player% is replaced with player name",
				"&7Command should not contain the starting '/'",
				"&7Example: 'give %player diamond'%"
				);
	}
	@Override
	public Reward getInstance(ConfigSection section,QuestComponent parent) {
		return new ConsoleCommandReward(section,parent);
	}
}
