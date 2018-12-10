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

public class LevelData extends QCData {
	private final static String PATH_LEVEL = "level";
	private int level;

	public LevelData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		level = getSection().getInt(PATH_LEVEL,1);
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean setLevel(int level) {
		if (level <= 0)
			level = 1;
		if (level == this.level)
			return false;
		this.level = level;
		getSection().set(PATH_LEVEL,level);
		getParent().setDirty(true);
		return true;
	}
	
	public Button getLevelEditorButton(Gui parent) {
		return new LevelEditorButton(parent);
	}
	
	

	private class LevelEditorButton extends AmountSelectorButton {

		public LevelEditorButton(Gui parent) {
			super("Level Editor", new ItemBuilder(Material.EXPERIENCE_BOTTLE).setGuiProperty().build(), parent);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lLevel Editor");
			desc.add("&6Click to edit");
			desc.add("&7Lv required is &e"+level);
			return desc;
		}

		@Override
		public long getCurrentAmount() {
			return level;
		}

		@Override
		public boolean onAmountChangeRequest(long i) {
			return setLevel((int) i);
		}
		
	}
}
