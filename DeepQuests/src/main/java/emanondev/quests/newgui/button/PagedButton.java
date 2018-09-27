package emanondev.quests.newgui.button;

import emanondev.quests.newgui.gui.PagedGui;

public interface PagedButton extends Button {
	public PagedGui getParent();
	
	public default int getPage() {
		return getParent().getPage();
	}
}