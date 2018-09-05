package emanondev.quests.quest;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.DisplayStateInfo;
import emanondev.quests.utils.StringUtils;

public class QuestDisplayInfo extends DisplayStateInfo {

	public QuestDisplayInfo(ConfigSection m, Quest parent) {
		super(m, parent);
	}

	@Override
	protected boolean shouldHideAutogen(DisplayState state) {
		return Defaults.QuestDef.shouldHideAutogen(state);
	}

	@Override
	protected boolean shouldItemAutogen(DisplayState state) {
		return Defaults.QuestDef.shouldItemAutogen(state);
	}

	@Override
	protected ItemStack getDefaultItem(DisplayState state) {
		return Defaults.QuestDef.getDefaultItem(state);
	}

	@Override
	protected boolean getDefaultHide(DisplayState state) {
		return Defaults.QuestDef.getDefaultHide(state);
	}

	@Override
	public ItemStack getGuiItem(Player p, DisplayState state) {
		ItemStack item = getItem(state);
		StringUtils.setDescription(item, p, getDescription(state), getHolders(p, state));
		return item;
	}

	public String[] getHolders(Player p, DisplayState state) {
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager().getQuestPlayer(p);
		String[] s;
		if (state != DisplayState.COOLDOWN) {
			s = new String[4 * 2];
			s[s.length - 2] = H.QUEST_COOLDOWN_LEFT;
			s[s.length - 1] = StringUtils.getStringCooldown(qPlayer.getCooldown(getParent()));
		} else {
			s = new String[3 * 2];
		}
		s[0] = H.QUEST_COMPLETED_MISSION_AMOUNT;
		s[1] = "" + qPlayer.getCompletedMissionAmount(getParent());
		s[2] = H.QUEST_MISSION_AMOUNT;
		s[3] = "" + getParent().getMissions().size();
		s[4] = H.QUEST_NAME;
		s[5] = getParent().getDisplayName();
		return s;
	}

	@Override
	protected Quest getParent() {
		return (Quest) super.getParent();
	}
	/*
	 * @Override protected boolean shouldLoreAutogen(DisplayState state) { return
	 * Defaults.QuestDef.shouldLoreAutogen(state); }
	 * 
	 * @Override protected boolean shouldTitleAutogen(DisplayState state) { return
	 * Defaults.QuestDef.shouldTitleAutogen(state); }
	 * 
	 * @Override protected String getDefaultTitle(DisplayState state) { return
	 * Defaults.QuestDef.getDefaultTitle(state); }
	 * 
	 * @Override protected ArrayList<String> getDefaultLore(DisplayState state) {
	 * return Defaults.QuestDef.getDefaultLore(state); }
	 */

	@Override
	protected boolean shouldDescriptionAutogen(DisplayState state) {
		return Defaults.QuestDef.shouldDescriptionAutogen(state);
	}

	@Override
	protected List<String> getDefaultDescription(DisplayState state) {
		return Defaults.QuestDef.getDefaultDescription(state);
	}

}
