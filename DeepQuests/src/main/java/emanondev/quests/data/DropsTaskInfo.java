package emanondev.quests.data;

import java.util.Arrays;

import org.bukkit.Material;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.task.Task;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.Utils;

public class DropsTaskInfo {

	private final static String PATH_REMOVE_DROP = "remove-drops";
	private final static String PATH_REMOVE_EXP = "remove-exp";
	private boolean removeDrops;
	private boolean removeExp;
	private final ConfigSection section;
	private final Task parent;
	public DropsTaskInfo(ConfigSection m,Task parent) {
		this.section = m;
		this.parent = parent;
		removeDrops = m.getBoolean(PATH_REMOVE_DROP,false);
		removeExp = m.getBoolean(PATH_REMOVE_EXP,false);
	}
	
	public boolean areDropsRemoved() {
		return removeDrops;
	}
	public boolean isExpRemoved() {
		return removeExp;
	}
	public boolean setDropsRemoved(boolean value) {
		if (value == removeDrops)
			return false;
		removeDrops = value;
		section.set(PATH_REMOVE_DROP,removeDrops);
		parent.setDirty(true);
		return true;
	}
	public boolean setExpRemoved(boolean value) {
		if (value == removeExp)
			return false;
		removeExp = value;
		section.set(PATH_REMOVE_EXP,removeExp);
		parent.setDirty(true);
		return true;
	}
	public Button getRemoveDropsButton(Gui parent) {
		return new RemoveDropsButton(parent);
	}
	public Button getRemoveExpButton(Gui parent) {
		return new RemoveExpButton(parent);
	}
	private class RemoveDropsButton extends StaticFlagButton {

		public RemoveDropsButton(Gui parent) {
			super(Utils.setDescription(new ItemBuilder(Material.GOLD_INGOT).setGuiProperty().build(),
					Arrays.asList("&6&lDrop Flag","&6Click to Toggle",
							"&7Drops are not removed","&7(Vanilla behavior)"),
					null,true), 
					Utils.setDescription(new ItemBuilder(Material.GOLD_INGOT).setGuiProperty().build(),
					Arrays.asList("&6&lDrop Flag","&6Click to Toggle",
							"&cDrops are removed"),
					null,true)		, parent);
		}

		@Override
		public boolean getCurrentValue() {
			return removeDrops;
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			setDropsRemoved(!removeDrops);
			return true;
		}
			
	}
	private class RemoveExpButton extends StaticFlagButton {

		public RemoveExpButton(Gui parent) {
			super(Utils.setDescription(new ItemBuilder(Material.EXP_BOTTLE).setGuiProperty().build(),
					Arrays.asList("&6&lExp Drops Flag","&6Click to Toggle",
							"&7Exp Drops are not removed","&7(Vanilla behavior)"),
					null,true),
					Utils.setDescription(new ItemBuilder(Material.EXP_BOTTLE).setGuiProperty().build(),
					Arrays.asList("&6&lExp Drops Flag","&6Click to Toggle","&cExp Drops are removed"),
					null,true)		, parent);
		}

		@Override
		public boolean getCurrentValue() {
			return removeExp;
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			setExpRemoved(!removeExp);
			return true;
		}
		
	}

}
