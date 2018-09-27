package emanondev.quests.newgui.gui;

public interface PagedGui extends Gui {
	
	public int getPage();
	
	public boolean setPage(int i);
	
	public default boolean incPage() {
		return setPage(getPage()+1);
	}
	
	public default boolean decPage() {
		return setPage(getPage()-1);
	}

	/**
	 * returns the biggest slot used <br> 
	 * (used to calculate max page)
	 * 
	 * @return
	 */
	public abstract int getMaxSlot();
	
	public default int getMaxPage() {
		return getMaxSlot()/getInventorySize()+1;
	}

}