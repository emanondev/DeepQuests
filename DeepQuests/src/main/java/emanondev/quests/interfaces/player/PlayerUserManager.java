package emanondev.quests.interfaces.player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import emanondev.quests.Quests;
import emanondev.quests.interfaces.AUserManager;

public class PlayerUserManager extends AUserManager<QuestPlayer> implements Listener{
	
	private Map<Player,QuestPlayer> users = new HashMap<>();

	public PlayerUserManager(PlayerQuestManager questManager) {
		super(questManager);
	}
	
	@Override
	public PlayerQuestManager getQuestManager() {
		return (PlayerQuestManager) super.getQuestManager();
	}

	@Override
	public Collection<QuestPlayer> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	@Override
	public QuestPlayer getUser(String uuid) {
		return users.get(Bukkit.getPlayer(UUID.fromString(uuid)));
	}

	public QuestPlayer getUser(Player p) {
		return users.get(p);
	}
	
	public void loadAll() {
		for(Player p:Bukkit.getOnlinePlayers()) {
			if (!users.containsKey(p)) {
				QuestPlayer questUser = new QuestPlayer(this,p);
				questUser.loadData();
				users.put(p, questUser);
			}
		}
		Bukkit.getPluginManager().registerEvents(this, Quests.get());
	}
	
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	private void onPlayerJoin(PlayerLoginEvent event) {
		if (event.getResult()==PlayerLoginEvent.Result.ALLOWED) {
			QuestPlayer questUser = new QuestPlayer(this,event.getPlayer());
			questUser.loadData();
			users.put(event.getPlayer(), questUser);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	private void onPlayerJoin(PlayerQuitEvent event) {
		users.remove(event.getPlayer());
	}
	

}
