package emanondev.quests.interfaces;

public interface Reward<T extends User<T>> extends QuestComponent<T> {
	/**
	 * 
	 * @return the Type
	 */
	public RewardType<T> getType();
	/**
	 * 
	 * @param user
	 */
	public default void apply(T user) {
		apply(user,1);
	}
	/**
	 * 
	 * @param user
	 */
	public void apply(T user,int amount);

}
