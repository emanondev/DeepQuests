package emanondev.quests.interfaces;

import org.bukkit.event.HandlerList;

public class TaskCompleteEvent<T extends User<T>> extends UserEvent<T> {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	private final Task<T> task;
	//private final Map<Reward<T>,Integer> rewards = new HashMap<>();
	public TaskCompleteEvent(T user, Task<T> task) {
		super(user);
		this.task = task;
		//for (Reward<T> reward:mission.getFailRewards())
		//	rewards.put(reward,1);
	}
	public Task<T> getTask(){
		return task;
	}
	/*public Map<Reward<T>, Integer> getRewards() {
		return rewards;
	}*/
}