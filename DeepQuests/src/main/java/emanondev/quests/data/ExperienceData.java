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

public class ExperienceData extends QCData {
	private final static String PATH_EXPERIENCE = "experience_reward";
	private long exp;

	public ExperienceData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		exp = getSection().getLong(PATH_EXPERIENCE,1);
	}
	
	public long getExperience() {
		return exp;
	}
	
	public boolean setExperience(long exp) {
		if (exp <= 0)
			exp = 1;
		if (exp == this.exp)
			return false;
		this.exp = exp;
		getSection().set(PATH_EXPERIENCE,exp);
		getParent().setDirty(true);
		return true;
	}

	public Button getExpEditorButton(Gui parent) {
		return new ExpEditorButton(parent);
	}
	private class ExpEditorButton extends AmountSelectorButton {
		public ExpEditorButton(Gui parent) {
			super("Exp Editor", new ItemBuilder(Material.EXPERIENCE_BOTTLE).setGuiProperty().build(), parent);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lExperience Editor");
			desc.add("&6Click to edit");
			desc.add("&7Exp reward is &e"+exp);
			return desc;
		}

		@Override
		public long getCurrentAmount() {
			return exp;
		}

		@Override
		public boolean onAmountChangeRequest(long i) {
			return setExperience(i);
		}
	}

}
