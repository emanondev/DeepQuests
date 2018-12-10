package emanondev.quests.data;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.hooks.Hooks;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.Utils;

public class VirginBlockData extends QCData {
	private boolean checkVirgin;
	public VirginBlockData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		checkVirgin = getSection().getBoolean(PATH_CHECK_VIRGIN, true);
	}

	private final static String PATH_CHECK_VIRGIN = "check-virgin-block";
	
	public boolean isValidBlock(Block block) {
		if (checkVirgin==false)
			return true;
		return Hooks.isBlockVirgin(block);
	}
	
	public boolean setVirginCheck(boolean value) {
		if (checkVirgin == value)
			return false;
		checkVirgin = value;
		getSection().getBoolean(PATH_CHECK_VIRGIN, checkVirgin);
		setDirty(true);
		return true;
	}

	public boolean isVirginCheckEnabled() {
		return checkVirgin;
	}
	
	public Button getVirginCheckButton(Gui gui) {
		return new VirginCheckButton(gui);
	}

	private class VirginCheckButton extends StaticFlagButton {
		public VirginCheckButton(Gui parent) {
			super(Utils.setDescription(new ItemBuilder(Material.WHITE_WOOL).setGuiProperty().setDamage(14).build(),
					Arrays.asList("&6&lVirgin Block Flag", "&6Click to toggle",
							"&7No restrintions on broken blocks",
							"&7Blocks previusly placed by players are &aAllowed"),
					null, true),
					Utils.setDescription(new ItemBuilder(Material.WHITE_WOOL).setGuiProperty().setDamage(5).build(),
							Arrays.asList("&6&lVirgin Block Flag", "&6Click to toggle",
									"&7Now only blocks naturally generated",
									"&7or growed blocks (like trees) are allowed",
									"&7All blocks previusly placed by players", "&7are &cnot allowed"),
							null, true),
					parent);
		}

		@Override
		public boolean getCurrentValue() {
			return checkVirgin;
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			return setVirginCheck(!checkVirgin);
		}

	}

}
