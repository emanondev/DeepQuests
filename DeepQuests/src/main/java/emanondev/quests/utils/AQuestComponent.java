package emanondev.quests.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.PriorityData;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.task.Task;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class AQuestComponent implements QuestComponent {
	public static final String PATH_DISPLAY_NAME = "name";
	private final PriorityData priorityData;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AQuestComponent other = (AQuestComponent) obj;
		if (!ID.equals(other.ID))
			return false;
		if (!parent.equals(other.parent))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 79;
		int result = super.hashCode();
		result = prime * result + parent.hashCode();
		return result;
	}

	private final ConfigSection section;

	protected ConfigSection getSection() {
		return section;
	}

	public final boolean isDirty()  {
		return getSection().isDirty();
	}

	public final void setDirty(boolean value)  {
		getSection().setDirty(value);
	}

	private String displayName;
	private final String ID;
	private final Savable parent;
	//private int priority;
	

	public int getPriority() {
		return priorityData.getPriority();
	}
	public boolean setPriority(int priority) {
		return priorityData.setPriority(priority);
	}
	public PriorityData getPriorityData() {
		return priorityData;
	}

	public Savable getParent() {
		return parent;
	}

	public AQuestComponent(ConfigSection m, Savable parent) {
		if (m == null || parent == null)
			throw new NullPointerException();
		this.section = m;
		this.parent = parent;
		this.ID = loadID().toLowerCase();
		this.displayName = loadDisplayName();
		this.priorityData = new PriorityData(m,this);
	}

	public String getID() {
		return ID;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean setDisplayName(String name) {
		if (name == null)
			return false;
		name = StringUtils.fixColorsAndHolders(name);
		if (name.equals(displayName))
			return false;
		this.displayName = name;
		section.set(PATH_DISPLAY_NAME, StringUtils.revertColors(displayName));
		if (this instanceof Task)
			((Task) this).getParent().reloadDisplay();
		setDirty(true);
		return true;
	}

	private boolean dirtyLoad = false;

	public void setDirtyLoad() {
		dirtyLoad = true;
	}

	public boolean isLoadDirty() {
		return dirtyLoad;
	}

	/**
	 * @return the unique name
	 */
	private String loadID() {
		String name = getSection().getName();
		if (name == null || name.isEmpty())
			throw new NullPointerException();
		return name;
	}

	/**
	 * @return the displayName
	 */
	private String loadDisplayName() {
		String tempDisplayName = getSection().getString(PATH_DISPLAY_NAME);
		if (tempDisplayName == null) {
			tempDisplayName = getID().replace("_", " ");

			getSection().set(PATH_DISPLAY_NAME, tempDisplayName);
			setDirtyLoad();
			return ChatColor.translateAlternateColorCodes('&', tempDisplayName);
		}
		return ChatColor.translateAlternateColorCodes('&', tempDisplayName);
	}

	protected class QCEditor extends MapGui {

		public QCEditor(String title, Player p, Gui previusHolder) {
			super(title, 6, p, previusHolder);
			this.putButton(53, new BackButton(this));
			this.putButton(6, new DisplayNameButton());
			this.putButton(16, priorityData.getPriorityEditorButton(this));
		}

		private class DisplayNameButton extends emanondev.quests.newgui.button.TextEditorButton {

			public DisplayNameButton() {
				super(new ItemStack(Material.NAME_TAG), QCEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				return Arrays.asList("&6&lDisplayName Editor", "&6Click to edit",
						"&7Current DisplayName '&r" + getDisplayName() + "&7'");
			}

			@Override
			public void onReicevedText(String text) {
				if (text == null)
					text = "";
				if (setDisplayName(text)) {
					// update();
					getParent().updateInventory();
				} else
					getTargetPlayer()
							.sendMessage(StringUtils.fixColorsAndHolders("&cSelected name was not a valid name"));
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, Utils.revertColors(getDisplayName()), changeTitleDesc);
			}
		}

	}

	private final static BaseComponent[] changeTitleDesc = new ComponentBuilder(ChatColor.GOLD
			+ "Click suggest the command and the old display name\n\n" + ChatColor.GOLD
			+ "Change override old title writing new title\n" + ChatColor.YELLOW + "/questtext <new display name>")
					.create();
}
