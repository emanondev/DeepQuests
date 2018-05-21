package emanondev.quests.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import emanondev.quests.utils.YmlLoadable;

public class EditorGui<T extends YmlLoadable> extends CustomMultiPageGuiHolder<CustomGuiItem> {
	private final T loadable;

	public EditorGui(Player p, T loadable, CustomGuiHolder previusHolder,
			ArrayList<EditorGuiItemFactory<T>> facts) {
		super(p, previusHolder, 6, 1);
		if (loadable==null)
			throw new NullPointerException();
		this.loadable = loadable;
		for (EditorGuiItemFactory<T> factory : facts)
			this.addButton(factory.getCustomGuiItem(this));
		loadInventory();
	}
	
	public T getLoadable() {
		return loadable;
	}
}
