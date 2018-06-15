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

/**
 * This class is responsible to keep load players files when they login or when required<br>
 * also this class is responsible of saving players files when they log out or when saveAll() is called<br>
 * 
 * this class also provide access to Player Infos
 * 
 * @author emanon
 *
 */
public class PlayerManager implements Listener {
	private static final HashMap<Player,QuestPlayer> players = new HashMap<Player,QuestPlayer>();
	private static boolean registered = false;

	public PlayerManager() {
		if (!registered) {
			Quests.getInstance().registerListener(this);
			registered = true;
		}
		players.clear();
		Bukkit.getOnlinePlayers().forEach((p)->{
			players.put(p, new QuestPlayer(p));
		});
	}
	
	public void reload(){
		saveAll();
		players.clear();
		Bukkit.getOnlinePlayers().forEach((p)->{
			players.put(p, new QuestPlayer(p));
		});
		
	}

	/**
	 * 
	 * @param p - player
	 * @return the QuestPlayer associated to player p 
	 */
	public QuestPlayer getQuestPlayer(Player p) {
		if (players.containsKey(p))
			return players.get(p);
		return null;
	}
	/**
	 * 
	 * @param p - offlineplayer
	 * @return if p is online this will return getQuestPlayer(p)<br>else the file associated to p is loaded from the Disk
	 */
	public OfflineQuestPlayer getOfflineQuestPlayer(OfflinePlayer p) {
		if (p.isOnline())
			p = p.getPlayer();
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
	/**
	 * Save all QuestPlayer progress when called
	 */
	public void saveAll() {
		players.values().forEach((qPlayer)->{
			if (qPlayer.shouldSave())
				qPlayer.save();
		});
		
	}
}
