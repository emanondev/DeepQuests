package emanondev.quests.utils;

public interface Savable {
	/**
	 * 
	 * @return true when the object has been changed and changes aren't saved on disk
	 */
	public boolean isDirty();
	/**
	 * 
	 * @param object should be overridden on disk?
	 */
	public void setDirty(boolean value);
}
