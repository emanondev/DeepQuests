package emanondev.quests.reward;

import org.bukkit.entity.Player;

import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.Applyable;
import emanondev.quests.utils.QuestComponent;

public interface Reward extends Applyable<QuestComponent> {
	/**
	 * 
	 * @return the Type
	 */
	public RewardType getType();

	public default void applyReward(QuestPlayer qPlayer) {
		applyReward(qPlayer,1);
	}
	public void applyReward(QuestPlayer qPlayer,int amount);

	public Gui createEditorGui(Player player, Gui missionEditor);
}
