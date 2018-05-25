package emanondev.quests.quest;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.DisplayStateInfo;
import emanondev.quests.utils.StringUtils;

public class QuestDisplayInfo extends DisplayStateInfo{

	public QuestDisplayInfo(MemorySection m, Quest parent) {
		super(m, parent);
		/*for (int i = 0; i < DisplayState.values().length; i++) {
			
			this.setLore(DisplayState.values()[i], 
				StringUtils.fixColorsAndHolders(this.getLore(DisplayState.values()[i]),
				H.QUEST_MISSION_AMOUNT,""+parent.getMissions().size(),
				H.QUEST_NAME,parent.getDisplayName()));
			
			this.setTitle(DisplayState.values()[i],
				StringUtils.fixColorsAndHolders(this.getTitle(DisplayState.values()[i]),
				H.QUEST_MISSION_AMOUNT,""+parent.getMissions().size(),
				H.QUEST_NAME,parent.getDisplayName()));
		
		}*/
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
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager().getQuestPlayer(p);
		if (state == DisplayState.COOLDOWN) {
			StringUtils.setDescription(item, p, getDescription(state), 
					H.QUEST_COMPLETED_MISSION_AMOUNT,""+qPlayer.getCompletedMissionAmount(getParent()),
					H.QUEST_COOLDOWN_LEFT,StringUtils.getStringCooldown(qPlayer.getCooldown(getParent())),
					H.QUEST_MISSION_AMOUNT,""+getParent().getMissions().size(),
					H.QUEST_NAME,getParent().getDisplayName()
					);
		}
		else {
			StringUtils.setDescription(item, p, getDescription(state), 
					H.QUEST_COMPLETED_MISSION_AMOUNT,""+qPlayer.getCompletedMissionAmount(getParent()),
					H.QUEST_MISSION_AMOUNT,""+getParent().getMissions().size(),
					H.QUEST_NAME,getParent().getDisplayName()
					);
		}/*
		ItemMeta meta = item.getItemMeta();
		QuestPla
		String replacer = ""+Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p).getCompletedMissionAmount(getParent());
		
			meta.setLore(StringUtils.convertList(p, getLore(state), 
				H.QUEST_COMPLETED_MISSION_AMOUNT,replacer,
				H.QUEST_COOLDOWN_LEFT,StringUtils.getStringCooldown(Quests.getInstance()
					.getPlayerManager().getQuestPlayer(p).getCooldown(getParent()))));
			meta.setDisplayName(StringUtils.convertText(p, getTitle(state), 
				H.QUEST_COMPLETED_MISSION_AMOUNT,replacer,
				H.QUEST_COOLDOWN_LEFT,StringUtils.getStringCooldown(Quests.getInstance()
					.getPlayerManager().getQuestPlayer(p).getCooldown(getParent()))));
		}
		else {
			meta.setLore(StringUtils.convertList(p, getLore(state), 
				H.QUEST_COMPLETED_MISSION_AMOUNT,replacer));
			meta.setDisplayName(StringUtils.convertText(p, getTitle(state), 
				H.QUEST_COMPLETED_MISSION_AMOUNT,replacer));
		}
		
		item.setItemMeta(meta);*/
		
		return item;
	}
	@Override
	protected Quest getParent() {
		return (Quest) super.getParent();
	}
	/*
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
	protected ArrayList<String> getDefaultLore(DisplayState state) {
		return Defaults.QuestDef.getDefaultLore(state);
	}*/

	@Override
	protected boolean shouldDescriptionAutogen(DisplayState state) {
		return Defaults.QuestDef.shouldDescriptionAutogen(state);
	}

	@Override
	protected List<String> getDefaultDescription(DisplayState state) {
		return Defaults.QuestDef.getDefaultDescription(state);
	}
	

}
