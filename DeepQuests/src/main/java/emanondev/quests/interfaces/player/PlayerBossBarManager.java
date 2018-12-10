package emanondev.quests.interfaces.player;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;

import emanondev.quests.interfaces.ABossBarManager;
import emanondev.quests.interfaces.QuestManager;
import emanondev.quests.interfaces.Task;

public class PlayerBossBarManager extends ABossBarManager<QuestPlayer>{

	public PlayerBossBarManager(QuestManager<QuestPlayer> qm) {
		super(qm);
	}

	@Override
	public Collection<Player> getPlayers(QuestPlayer user, Task<QuestPlayer> t) {
		ArrayList<Player> list = new ArrayList<>();
		list.add(user.getPlayer());
		return list;
	}

}
