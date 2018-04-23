package emanondev.quests.reward;

import org.bukkit.Bukkit;

import emanondev.quests.player.QuestPlayer;

public class ConsoleCommandRewardType extends RewardType {
	
	
	
	public ConsoleCommandRewardType() {
		super("COMMAND", ConsoleCommandReward.class);
	}

	public class ConsoleCommandReward implements Reward {
		private final String command;
		public ConsoleCommandReward(String command) {
			if (command == null)
				throw new NullPointerException();
			this.command = command;
		}
		@Override
		public void applyReward(QuestPlayer p) {
			Bukkit.dispatchCommand(
					Bukkit.getConsoleSender(),
					command.replace("%player%", p.getPlayer().getName())
					);
		}
		@Override
		public String getDescription() {
			return "Use: '" +command+"'";
		}
	}
}
