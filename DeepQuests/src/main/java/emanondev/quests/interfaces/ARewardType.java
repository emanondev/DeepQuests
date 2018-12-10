package emanondev.quests.interfaces;

public abstract class ARewardType<T extends User<T>> extends AType<T,Reward<T>> implements RewardType<T> {

	public ARewardType(String id) {
		super(id);
	}
}
