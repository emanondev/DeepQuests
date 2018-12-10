package emanondev.quests.interfaces;

import java.util.Map;

public abstract class AReward<T extends User<T>> extends AQuestComponent<T> implements Reward<T> {
	public AReward(Map<String, Object> map) {
		super(map);
	}
}
