package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class AbstractApplyable<T extends QuestComponent> extends AQuestComponent implements QuestComponent,Applyable<T> {
	private static final String PATH_DESCRIPTION = "description";
	private String description;

	public AbstractApplyable(ConfigSection section, T parent) {
		super(section,parent);
		this.description = section.getString(PATH_DESCRIPTION);
	}

	@SuppressWarnings({ "unchecked" })
	public T getParent() {
		return (T) super.getParent();
	}

	
	public final String getKey() {
		return getType().getKey();
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
		getSection().set(PATH_DESCRIPTION, description);
		getParent().setDirty(true);
		return true;
	}

	
	
	protected abstract class AbstractApplayableEditor extends MapGui {

		public AbstractApplayableEditor(String title,Player p, Gui previusHolder) {
			super(title, 6, p, previusHolder);
			this.putButton(53, new BackButton(this));
			this.putButton(0, new DisplayNameButton());
		}
		private class DisplayNameButton extends emanondev.quests.newgui.button.TextEditorButton {

			public DisplayNameButton() {
				super(new ItemStack(Material.NAME_TAG),AbstractApplayableEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDescription Editor",
						"&6Click to edit",
						"&7Current Description '&r" + getDescription() + "&7'");
			}

			@Override
			public void onReicevedText(String text) {
				if (text == null)
					text = "";
				if (setDescription(text)) {
					getParent().updateInventory();
				} else
					getTargetPlayer().sendMessage(StringUtils.fixColorsAndHolders("&cSelected description was not a valid description"));
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, Utils.revertColors(getDescription()), getChangeDescriptionHelp());
			}
		}

		protected abstract BaseComponent[] getChangeDescriptionHelp();

		protected abstract ArrayList<String> getDescriptionButtonDisplay();
	}

	

}
