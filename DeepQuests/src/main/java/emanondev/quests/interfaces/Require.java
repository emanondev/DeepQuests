package emanondev.quests.interfaces;

public interface Require<T extends User<T>> extends QuestComponent<T> {
	/**
	 * 
	 * @return the Type
	 */
	public RequireType<T> getType();
	/**
	 * 
	 * @param user
	 * @return true if user satisfy this require
	 */
	public boolean isAllowed(T user);

}
