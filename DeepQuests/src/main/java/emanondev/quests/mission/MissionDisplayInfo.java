package emanondev.quests.mission;

import java.util.List;

import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.DisplayStateInfo;
import emanondev.quests.utils.StringUtils;

public class MissionDisplayInfo extends DisplayStateInfo{

	public MissionDisplayInfo(MemorySection m, Mission mission) {
		super(m, mission);
	}
	
	public Mission getParent() {
		return (Mission) super.getParent();
	}

	@Override
	protected boolean shouldHideAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldHideAutogen(state);
	}

	@Override
	protected boolean shouldItemAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldItemAutogen(state);
	}

	@Override
	protected boolean shouldLoreAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldLoreAutogen(state);
	}

	@Override
	protected boolean shouldTitleAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldTitleAutogen(state);
	}

	@Override
	protected ItemStack getDefaultItem(DisplayState state) {
		return Defaults.MissionDef.getDefaultItem(state);
	}

	@Override
	protected List<String> getDefaultLore(DisplayState state) {
		List<String> lore = Defaults.MissionDef.getDefaultLore(state);
		return lore;
	}
	

	@Override
	protected boolean getDefaultHide(DisplayState state) {
		return Defaults.MissionDef.getDefaultHide(state);
	}
	@Override
	protected String getDefaultTitle(DisplayState state) {
		return Defaults.MissionDef.getDefaultTitle(state);
	}
	@Override
	public ItemStack getGuiItem(Player p, DisplayState state) {
		ItemStack item = getItem(state);
		ItemMeta meta = item.getItemMeta();
		
		meta.setLore(StringUtils.convertList(p, getLore(state), 
				getParent().getHolders(p,state) ));
		meta.setDisplayName(StringUtils.convertText(p, getTitle(state), 
				getParent().getHolders(p,state) ));
		if (!Quests.getInstance().getPlayerManager().getQuestPlayer(p).getMissionData(getParent()).isPaused())
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
		item.setItemMeta(meta);
		
		return item;
	}
	

}
