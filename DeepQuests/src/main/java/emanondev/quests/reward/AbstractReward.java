package emanondev.quests.reward;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.quest.Quest;
import emanondev.quests.task.Task;
import emanondev.quests.utils.AbstractApplyable;
import emanondev.quests.utils.QuestComponent;

public abstract class AbstractReward extends AbstractApplyable<QuestComponent> implements Reward {

	public AbstractReward(ConfigSection section, QuestComponent parent) {
		super(section, parent);
	}
	public List<String> getInfo(){
		List<String> info = new ArrayList<String>();
		info.add("&9&lReward: &6"+ this.getDisplayName());
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
	public RewardEditor createEditorGui(Player p,Gui parent) {
		return new RewardEditor(p,parent);
	}
	
	protected class RewardEditor extends AbstractApplayableEditor {

		public RewardEditor(Player p, Gui previusHolder) {
			super("&9Reward &8(" + getKey() + ")", p, previusHolder);
		}
	}
	
	
}
