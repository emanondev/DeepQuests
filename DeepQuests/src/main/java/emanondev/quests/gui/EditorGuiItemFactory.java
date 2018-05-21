package emanondev.quests.gui;

import emanondev.quests.utils.YmlLoadable;

public interface EditorGuiItemFactory<T extends YmlLoadable> {
	public CustomGuiItem getCustomGuiItem(EditorGui<T> parent);
}
