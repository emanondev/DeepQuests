package emanondev.quests.interfaces;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.HandlerList;

public class MissionCompleteEvent<T extends User<T>> extends UserEvent<T> {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Mission<T> mission;
	private final Map<Reward<T>,Integer> rewards = new HashMap<>();
	public MissionCompleteEvent(T user, Mission<T> mission) {
		super(user);
		this.mission = mission;
		for (Reward<T> reward:mission.getCompleteRewards())
			rewards.put(reward,1);
	}
	public Mission<T> getMission(){
		return mission;
	}
	public Map<Reward<T>, Integer> getRewards() {
		return rewards;
	}
}