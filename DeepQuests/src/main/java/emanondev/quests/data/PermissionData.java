package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.TextEditorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class PermissionData extends QCData {
	private final static String PATH_PERMISSION = "permission";
	private String permission;

	public PermissionData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		this.permission = getSection().getString(PATH_PERMISSION);
		if (this.permission != null)
			this.permission = this.permission.toLowerCase();
	}
	public String getPermission() {
		return permission;
	}
	public boolean setPermission(String permission) {
		if (permission!=null && !permission.isEmpty() && permission.contains(" "))
			return false;

		if (permission.isEmpty())
			permission = null;
		this.permission = permission;
		getSection().set(PATH_PERMISSION, this.permission);
		setDirty(true);
		return true;
	}
	public Button getPermissionEditorButton(Gui gui) {
		return new PermissionEditorButton(gui);
	}
	
	private class PermissionEditorButton extends TextEditorButton {

		public PermissionEditorButton(Gui parent) {
			super(new ItemBuilder(Material.TRIPWIRE_HOOK).setGuiProperty().build(), parent);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lPermission Editor");
			desc.add("&6Click to edit");
			if (permission != null)
				desc.add("&7Current Permission '&r" + getPermission() + "&7'");
			else
				desc.add("&cNo Permission has been set");
			return desc;
		}

		@Override
		public void onReicevedText(String text) {
			if (text == null)
				text = "";
			if (setPermission(text)) {
				getParent().updateInventory();
			} else
				getTargetPlayer().sendMessage(
						StringUtils.fixColorsAndHolders("&cSelected permission was not a valid permission"));
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			this.requestText(clicker, StringUtils.revertColors(getPermission()), changeTitleDesc);
		}
		
	}
	private static final BaseComponent[] changeTitleDesc = new ComponentBuilder(ChatColor.GOLD
			+ "Click suggest and type the permission\n\n"
			+ ChatColor.YELLOW 	+ "/questtext <permission>")
					.create();

	

}
