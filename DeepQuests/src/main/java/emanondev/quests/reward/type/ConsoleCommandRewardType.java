package emanondev.quests.reward.type;

import org.bukkit.Bukkit;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.reward.AbstractRewardType;

public class ConsoleCommandRewardType extends AbstractRewardType implements RewardType {
	
	public ConsoleCommandRewardType() {
		super("COMMAND");
	}

	@Override
	public Reward getRewardInstance(String info) {
		return new ConsoleCommandReward(info);
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
