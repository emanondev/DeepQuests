package emanondev.quests.quest;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.DisplayStateInfo;
import emanondev.quests.utils.StringUtils;

public class QuestDisplayInfo extends DisplayStateInfo{

	public QuestDisplayInfo(MemorySection m, Quest parent) {
		super(m, parent);
		for (int i = 0; i < DisplayState.values().length; i++) {
			
			this.setLore(DisplayState.values()[i], 
				StringUtils.fixColorsAndHolders(this.getLore(DisplayState.values()[i]),
				Quest.HOLDER_MISSION_NUMBER,""+parent.getMissions().size(),
				Quest.HOLDER_QUEST_NAME,parent.getDisplayName()));
			
			this.setTitle(DisplayState.values()[i],
				StringUtils.fixColorsAndHolders(this.getTitle(DisplayState.values()[i]),
				Quest.HOLDER_MISSION_NUMBER,""+parent.getMissions().size(),
				Quest.HOLDER_QUEST_NAME,parent.getDisplayName()));
		
		}
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
	protected boolean shouldLoreAutogen(DisplayState state) {
		return Defaults.QuestDef.shouldLoreAutogen(state);
	}

	@Override
	protected boolean shouldTitleAutogen(DisplayState state) {
		return Defaults.QuestDef.shouldTitleAutogen(state);
	}

	@Override
	protected String getDefaultTitle(DisplayState state) {
		return Defaults.QuestDef.getDefaultTitle(state);
	}
	@Override
	protected ItemStack getDefaultItem(DisplayState state) {
		return Defaults.QuestDef.getDefaultItem(state);
	}

	@Override
	protected List<String> getDefaultLore(DisplayState state) {
		return Defaults.QuestDef.getDefaultLore(state);
	}
	@Override
	protected boolean getDefaultHide(DisplayState state) {
		return Defaults.QuestDef.getDefaultHide(state);
	}

	@Override
	public ItemStack getGuiItem(Player p, DisplayState state) {
		ItemStack item = getItem(state);
		ItemMeta meta = item.getItemMeta();
		String replacer = ""+Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p).getCompletedMissionAmount(getParent());
		
		if (state == DisplayState.COOLDOWN) {
			meta.setLore(StringUtils.convertList(p, getLore(state), 
				Quest.HOLDER_COMPLETED_MISSION_NUMBER,replacer,
				StringUtils.HOLDER_COOLDOWN,StringUtils.getStringCooldown(Quests.getInstance()
					.getPlayerManager().getQuestPlayer(p).getCooldown(getParent()))));
			meta.setDisplayName(StringUtils.convertText(p, getTitle(state), 
				Quest.HOLDER_COMPLETED_MISSION_NUMBER,replacer,
				StringUtils.HOLDER_COOLDOWN,StringUtils.getStringCooldown(Quests.getInstance()
					.getPlayerManager().getQuestPlayer(p).getCooldown(getParent()))));
		}
		else {
			meta.setLore(StringUtils.convertList(p, getLore(state), 
				Quest.HOLDER_COMPLETED_MISSION_NUMBER,replacer));
			meta.setDisplayName(StringUtils.convertText(p, getTitle(state), 
				Quest.HOLDER_COMPLETED_MISSION_NUMBER,replacer));
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
	@Override
	protected Quest getParent() {
		return (Quest) super.getParent();
	}
	

}
