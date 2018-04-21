package emanondev.quests.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import emanondev.quests.Quests;

public class PlayerManager implements Listener {
	
	public PlayerManager() {
		Quests.getInstance().registerListener(this);
		Bukkit.getOnlinePlayers().forEach((p)->{
			players.put(p, new QuestPlayer(p));
		});
	}
	
	private final HashMap<Player,QuestPlayer> players = new HashMap<Player,QuestPlayer>();

	public QuestPlayer getQuestPlayer(Player p) {
		if (players.containsKey(p))
			return players.get(p);
		throw new RuntimeException("error player has no questplayer object");
	}
	public OfflineQuestPlayer getOfflineQuestPlayer(OfflinePlayer p) {
		if (players.containsKey(p))
			return players.get(p);
		return new OfflineQuestPlayer(p);
	}
	
	@EventHandler (priority=EventPriority.MONITOR,ignoreCancelled=true)
	private void onJoin(PlayerLoginEvent e) {
		if (e.getResult()==PlayerLoginEvent.Result.ALLOWED)
			players.put(e.getPlayer(), new QuestPlayer(e.getPlayer()));
	}
	@EventHandler (priority=EventPriority.MONITOR)
	private void onQuit(PlayerQuitEvent e) {
		QuestPlayer p = players.remove(e.getPlayer());
		if (p.shouldSave())
			p.save();
	}
	public void saveAll() {
		players.values().forEach((qPlayer)->{
			if (qPlayer.shouldSave())
				qPlayer.save();
		});
		
	}
}
