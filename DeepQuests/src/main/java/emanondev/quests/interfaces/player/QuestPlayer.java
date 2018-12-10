package emanondev.quests.interfaces.player;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import emanondev.quests.interfaces.User;
import emanondev.quests.interfaces.UserData;
import emanondev.quests.interfaces.UserManager;

public class QuestPlayer extends User<QuestPlayer> {
	
	private final OfflinePlayer offPlayer;

	public QuestPlayer(UserManager<QuestPlayer> manager,OfflinePlayer p) {
		super(manager);
		if (p==null)
			throw new NullPointerException();
		this.offPlayer = p;
	}

	@Override
	public String getUID() {
		return offPlayer.getUniqueId().toString();
	}
	
	public Player getPlayer() {
		return offPlayer.getPlayer();
	}

	@Override
	public UserData<QuestPlayer> getDefaultUserData() {
		return new UserData<QuestPlayer>(new HashMap<String,Object>());
	}


}
