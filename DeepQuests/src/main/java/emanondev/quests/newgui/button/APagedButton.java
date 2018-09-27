package emanondev.quests.newgui.button;

import emanondev.quests.newgui.gui.PagedGui;

public abstract class APagedButton extends AButton implements PagedButton {

	public APagedButton(PagedGui parent) {
		super(parent);
	}

	@Override
	public PagedGui getParent() {
		return (PagedGui) super.getParent();
	}

}