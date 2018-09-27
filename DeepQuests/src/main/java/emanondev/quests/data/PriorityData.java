package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.AmountSelectorButton;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;

public class PriorityData extends QCData {
	public static final String PATH_PRIORITY = "priority";
	private int priority;
	public PriorityData(ConfigSection section, QuestComponent qc) {
		super(section,qc);
		priority = getSection().getInt(PATH_PRIORITY);
	}
	public int getPriority() {
		return priority;
	}
	public boolean setPriority(int priority) {
		if (priority == this.priority)
			return false;
		this.priority = priority;
		getSection().set(PATH_PRIORITY,priority);
		setDirty(true);
		return true;
	}
	public Button getPriorityEditorButton(Gui gui) {
		return new PriorityEditorButton(gui);
	}
	private class PriorityEditorButton extends AmountSelectorButton {

		public PriorityEditorButton( Gui parent) {
			super("&8Priority Editor", new ItemBuilder(Material.GOLD_NUGGET).setGuiProperty().build(), parent);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lPriority Editor Button");
			desc.add("&6Click to edit priority");
			desc.add("");
			desc.add("&6Current Priority: &e"+priority);
			desc.add("");
			desc.add("&7Highter Priority Items comes first");
			return desc;
		}

		@Override
		public long getCurrentAmount() {
			return priority;
		}

		@Override
		public boolean onAmountChangeRequest(long i) {
			return setPriority((int) i);
		}
		
	}

}
