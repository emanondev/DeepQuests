package emanondev.quests.require;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.AbstractApplyable;
import emanondev.quests.utils.QCWithCooldown;

public abstract class AbstractRequire extends AbstractApplyable<QCWithCooldown> implements Require {

	public AbstractRequire(ConfigSection section, QCWithCooldown parent) {
		super(section, parent);
	}

	public List<String> getInfo(){
		List<String> info = new ArrayList<String>();
		info.add("&9&lRequire: &6"+ this.getDisplayName());
		info.add("&8Type: &7"+getType().getKey());

		info.add("&8ID: "+ this.getID());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		if (getParent() instanceof Task) {
			info.add("&9Quest: &e"+((Task) getParent()).getParent().getParent().getDisplayName());
			info.add("&9Mission: &e"+((Task) getParent()).getParent().getDisplayName());
			info.add("&9Task: &e"+((Task) getParent()).getDisplayName());
		}
		else if (getParent() instanceof Mission) {
			info.add("&9Quest: &e"+((Mission) getParent()).getParent().getDisplayName());
			info.add("&9Mission: &e"+((Mission) getParent()).getDisplayName());
		}
		else if (getParent() instanceof Quest) {
			info.add("&9Quest: &e"+((Quest) getParent()).getDisplayName());
		}
		
		return info;
	}
	@Override
	public RequireEditor createEditorGui(Player p,Gui parent) {
		return new RequireEditor(p,parent);
	}
	
	protected class RequireEditor extends AbstractApplayableEditor {

		public RequireEditor(Player p, Gui previusHolder) {
			super("&9Require &8(" + AbstractRequire.this.getKey() + ")", p, previusHolder);
		}
	}
}
