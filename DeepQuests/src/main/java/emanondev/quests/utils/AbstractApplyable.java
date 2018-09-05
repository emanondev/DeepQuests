package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.ApplyableGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.button.TextEditorButton;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class AbstractApplyable<T extends YmlLoadable> implements Applyable<T> {
	private static final String PATH_DESCRIPTION = "description";
	private final String nameID;
	private String description;

	public AbstractApplyable(ConfigSection section, T parent) {
		if (section == null || parent == null)
			throw new NullPointerException();
		this.nameID = loadName(section).toLowerCase();
		this.section = section;
		this.parent = parent;
		this.description = section.getString(PATH_DESCRIPTION);
		this.addToEditor(0, new DescriptionEditorButtonFactory());
	}

	public String getNameID() {
		return nameID;
	}

	/**
	 * @return the unique name
	 */
	private String loadName(ConfigSection m) {
		String name = m.getName();
		if (name == null || name.isEmpty())
			throw new NullPointerException();
		return name;
	}

	private final ConfigSection section;
	private final T parent;

	public T getParent() {
		return parent;
	}

	protected ConfigSection getSection() {
		return section;
	}

	private HashMap<Integer, EditorButtonFactory> tools = new HashMap<Integer, EditorButtonFactory>();

	public void openEditorGui(Player p) {
		openEditorGui(p, null);
	}

	public void openEditorGui(Player p, CustomGui previusHolder) {
		p.openInventory(new ApplyableGui<T>(p, this, previusHolder, tools, getEditorTitle()).getInventory());
	}

	public final String getKey() {
		return getType().getKey();
	}

	public void addToEditor(int slot, EditorButtonFactory item) {
		if (item != null)
			tools.put(slot, item);
	}

	public String getDescription() {
		return description;
	}

	public boolean setDescription(String desc) {
		if (desc != null && desc.equals(description))
			return false;
		if (desc == null && description == null)
			return false;
		this.description = desc;
		section.set(PATH_DESCRIPTION, description);
		parent.setDirty(true);
		return true;
	}

	private class DescriptionEditorButtonFactory implements EditorButtonFactory {
		private class DescriptionEditorButton extends TextEditorButton {
			private ItemStack item = new ItemStack(Material.NAME_TAG);

			public DescriptionEditorButton(CustomGui parent) {
				super(parent);
				update();
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			public void update() {
				StringUtils.setDescription(item, getDescriptionButtonDisplay());
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, StringUtils.revertColors(description), getChangeDescriptionHelp());
			}

			@Override
			public void onReicevedText(String text) {
				if (text == null)
					text = "";
				if (setDescription(text)) {
					update();
					getParent().reloadInventory();
				} else
					getOwner().sendMessage(
							StringUtils.fixColorsAndHolders("&cSelected description was not a valid description"));
			}
		}

		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new DescriptionEditorButton(parent);
		}
	}

	protected abstract String getEditorTitle();

	protected abstract BaseComponent[] getChangeDescriptionHelp();

	protected abstract ArrayList<String> getDescriptionButtonDisplay();

}
