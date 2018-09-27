package emanondev.quests.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.datatypes.skills.SkillType;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;

public class MCMMOSkillTypeData extends QCData {

	private final static String PATH_SKILLTYPE = "skilltype";
	
	
	public MCMMOSkillTypeData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		try {
			skillType = SkillType.valueOf(getSection().getString(PATH_SKILLTYPE,null));
		} catch (Exception e) {
			skillType = null;
		}
	}

	private SkillType skillType;
	
	public SkillType getSkillType() {
		return skillType;
	}
	public boolean setSkillType(SkillType skillType) {
		if (skillType==null && this.skillType==null)
			return false;
		if (this.skillType!= null && this.skillType.equals(skillType) )
			return false;
		this.skillType = skillType;
		if (skillType==null)
			getSection().set(PATH_SKILLTYPE,null);
		else
			getSection().set(PATH_SKILLTYPE,skillType.toString());
		getParent().setDirty(true);
		return true;
	}
	
	public Button getSkillTypeSelectorButton(Gui parent) {
		return new SkillTypeSelectorButton(parent);
	}
	
	private class SkillTypeSelectorButton extends SelectOneElementButton<SkillType> {

		public SkillTypeSelectorButton(Gui parent) {
			super("&9MCMMO SkillType Editor", new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build(), parent, EnumSet.allOf(SkillType.class), true, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSkillType editor button");
			desc.add("&6click to edit selected skilltype");
			if (skillType == null)
				desc.add("&cNo skilltype has been selected yet");
			else
				desc.add("&7Current selected skilltype is &a"+skillType.getName());
			return desc;
		}

		@Override
		public List<String> getElementDescription(SkillType skillType) {
			List<String> desc = new ArrayList<String>();
			if (skillType!=null)
				desc.add("&6Click to select Job "+skillType.getName());
			return desc;
		}

		@Override
		public ItemStack getElementItem(SkillType skillType) {
			return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(SkillType skillType) {
			setSkillType(skillType);
		}
		
	}
	

}
