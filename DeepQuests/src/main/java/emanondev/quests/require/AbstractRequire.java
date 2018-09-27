package emanondev.quests.require;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.AbstractApplyable;
import emanondev.quests.utils.QCWithCooldown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class AbstractRequire extends AbstractApplyable<QCWithCooldown> implements Require {

	public AbstractRequire(ConfigSection section, QCWithCooldown parent) {
		super(section, parent);
	}

	private static final BaseComponent[] changeDescriptionHelp = new ComponentBuilder(
			ChatColor.GOLD + "Click suggest the command and the old description\n\n" + ChatColor.GOLD
					+ "Change override old description with new description\n" + ChatColor.YELLOW
					+ "/questtext <new description>\n\n").create();
	@Override
	public RequireEditor createEditorGui(Player p,Gui parent) {
		return new RequireEditor(p,parent);
	}
	
	protected class RequireEditor extends AbstractApplayableEditor {

		public RequireEditor(Player p, Gui previusHolder) {
			super("&9Require &8(" + AbstractRequire.this.getKey() + ")", p, previusHolder);
		}
		@Override
		protected BaseComponent[] getChangeDescriptionHelp() {
			return changeDescriptionHelp;
		}
	
		@Override
		protected ArrayList<String> getDescriptionButtonDisplay() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lRequire Description Editor");
			desc.add("&6Click to edit");
			desc.add("&7Current value:");
			if (getDescription() != null)
				desc.add("&7'&f" + getDescription() + "&7'");
			else
				desc.add("&7no description is set");
			desc.add("");
			desc.add("&7Represent the description of the Require");
			return desc;
		}
	}
}
