package emanondev.quests.interfaces.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.interfaces.Paths;
import emanondev.quests.utils.Utils;

public class ToolData extends QuestComponentData {

	private boolean enabled = false;
	private ItemStack item = null;
	private boolean checkMaterial = true;
	private boolean checkEnchant = true;
	private boolean checkAmount = true;
	private boolean checkLore = true;
	private boolean checkDisplayName = true;
	private boolean checkLocalizedName = true;
	private boolean usePlaceHolder = true;
	private boolean checkFlags = true;
	private boolean checkUnbreakable = true;
	private boolean checkAll = true;
	private boolean checkAttributes = true;
	private HashMap<Enchantment, EnchantCheckType> enchantCheckMap = new HashMap<>();
	private EnchantCheckType defaultEnchantCheck = EnchantCheckType.EQUALS;
	
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isValidTool(ItemStack tool, Player p) {
		if (!enabled)
			return true;
		if (item == null && tool == null)
			return true;
		if (item == null || tool == null)
			return false;
		if (checkAmount && (tool.getAmount() != item.getAmount()))
			return false;

		if (checkAll == true) {
			if (item.isSimilar(tool))
				return true;
			if (usePlaceHolder == false)
				return false;
			if (!item.hasItemMeta() || !tool.hasItemMeta())
				return false;

			ItemStack itemClone = item.clone();
			itemClone.setItemMeta(fixMeta(item.getItemMeta(), p));

			if (itemClone.isSimilar(tool))
				return true;
			return false;
		}

		if (checkMaterial && item.getType() != tool.getType())
			return false;
		ItemMeta itemMeta;
		if (usePlaceHolder)
			itemMeta = fixMeta(item.getItemMeta(), p);
		else
			itemMeta = item.getItemMeta();

		ItemMeta toolMeta = tool.getItemMeta();

		if (checkLore && !toolMeta.getLore().equals(itemMeta.getLore()))
			return false;
		if (checkDisplayName && !toolMeta.getDisplayName().equals(itemMeta.getDisplayName()))
			return false;
		if (checkLocalizedName && !toolMeta.getLocalizedName().equals(itemMeta.getLocalizedName()))
			return false;
		if (checkFlags && !toolMeta.getItemFlags().equals(itemMeta.getItemFlags()))
			return false;
		if (checkUnbreakable && !toolMeta.isUnbreakable() == itemMeta.isUnbreakable())
			return false;
		if (checkAttributes && !toolMeta.getAttributeModifiers().equals(itemMeta.getAttributeModifiers()))
			return false;
		return enchantCheck(itemMeta, toolMeta);
	}

	private boolean enchantCheck(ItemMeta itemMeta, ItemMeta toolMeta) {
		if (checkEnchant) {
			
			
			if (defaultEnchantCheck == EnchantCheckType.NONE) {
				for (Enchantment ench : enchantCheckMap.keySet())
					if (!checkEnchType(ench,
							enchantCheckMap.get(ench) == null ? defaultEnchantCheck : enchantCheckMap.get(ench),
							itemMeta.getEnchantLevel(ench), toolMeta.getEnchantLevel(ench)))
						return false;
			} else {
				for (Enchantment ench : Enchantment.values()) 
					if (!checkEnchType(ench,
							enchantCheckMap.get(ench) == null ? defaultEnchantCheck : enchantCheckMap.get(ench),
							itemMeta.getEnchantLevel(ench), toolMeta.getEnchantLevel(ench)))
						return false;
			}
		}
		return true;
	}

	private boolean checkEnchType(Enchantment ench, EnchantCheckType checkType, int checkLv, int toolLv) {
		switch (checkType) {
		case DIFFERENT:
			return checkLv != toolLv;
		case EQUALS:
			return checkLv == toolLv;
		case EQUALS_OR_HIGHTER:
			return checkLv <= toolLv;
		case EQUALS_OR_LOWER:
			return checkLv >= toolLv;
		case NONE:
			return true;
		default:
		}
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(Paths.TOOL_ENABLE_CHECK,enabled);
		map.put(Paths.TOOL_INFO_ITEM,item);
		map.put(Paths.TOOL_INFO_CHECK_MATERIAL,checkMaterial);
		map.put(Paths.TOOL_INFO_CHECK_ENCHANT,checkEnchant);
		map.put(Paths.TOOL_INFO_CHECK_AMOUNT,checkAmount);
		map.put(Paths.TOOL_INFO_CHECK_LORE,checkLore);
		map.put(Paths.TOOL_INFO_CHECK_DISPLAY_NAME,checkDisplayName);
		map.put(Paths.TOOL_INFO_CHECK_LOCALIZED_NAME,checkLocalizedName);
		map.put(Paths.TOOL_INFO_CHECK_FLAGS,checkFlags);
		map.put(Paths.TOOL_INFO_CHECK_UNBREAKABLE,checkUnbreakable);
		map.put(Paths.TOOL_INFO_CHECK_ALL,checkAll);
		map.put(Paths.TOOL_INFO_CHECK_ATTRIBUTES,checkAttributes);
		map.put(Paths.TOOL_INFO_USE_PLACE_HOLDER,usePlaceHolder);
		map.put(Paths.TOOL_INFO_DEFAULT_ENCHANT_CHECK,defaultEnchantCheck.toString());
		for (Enchantment ench:enchantCheckMap.keySet())
			map.put(Paths.TOOL_INFO_ENCHANT_CHECK_TYPE+"."+ench.getKey().getKey(),enchantCheckMap.get(ench).toString());
		return map;
	}
	
	public ToolData(Map<String,Object> map){
		if (map==null)
			return;
		try {
			enabled = (boolean) map.getOrDefault(Paths.TOOL_ENABLE_CHECK,enabled);
			item = (ItemStack) map.getOrDefault(Paths.TOOL_INFO_ITEM,item);
			checkMaterial = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_MATERIAL,checkMaterial);
			checkEnchant = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_ENCHANT,checkEnchant);
			checkAmount = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_AMOUNT,checkAmount);
			checkLore = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_LORE,checkLore);
			checkDisplayName = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_DISPLAY_NAME,checkDisplayName);
			checkLocalizedName = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_LOCALIZED_NAME,checkLocalizedName);
			checkFlags = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_FLAGS,checkFlags);
			checkUnbreakable = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_UNBREAKABLE,checkUnbreakable);
			checkAll = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_ALL,checkAll);
			checkAttributes = (boolean) map.getOrDefault(Paths.TOOL_INFO_CHECK_ATTRIBUTES,checkAttributes);
			usePlaceHolder = (boolean) map.getOrDefault(Paths.TOOL_INFO_USE_PLACE_HOLDER,usePlaceHolder);
			try {
				defaultEnchantCheck = EnchantCheckType.valueOf((String) map.getOrDefault(Paths.TOOL_INFO_DEFAULT_ENCHANT_CHECK,defaultEnchantCheck.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (map.containsKey(Paths.TOOL_INFO_ENCHANT_CHECK_TYPE)) {
				try {
					@SuppressWarnings("unchecked")
					Map<String,?> subMap = (Map<String,?>) map.get(Paths.TOOL_INFO_ENCHANT_CHECK_TYPE);
					for (Enchantment ench:Enchantment.values())
						if (subMap.containsKey(ench.getKey().getKey()))
							try{
								EnchantCheckType type = EnchantCheckType.valueOf((String) subMap.get(ench.getKey().getKey()));
								enchantCheckMap.put(ench, type);
							}catch (Exception e) {
								e.printStackTrace();
							}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ItemMeta fixMeta(ItemMeta meta, Player p) {
		if (meta.hasDisplayName()) {
			meta.setDisplayName(Utils.fixString(meta.getDisplayName(), p, false));
		}
		if (meta.hasLocalizedName()) {
			meta.setLocalizedName(Utils.fixString(meta.getLocalizedName(), p, false));
		}
		if (meta.hasLore()) {
			meta.setLore(Utils.fixList(meta.getLore(), p, false));
		}
		return meta;
	}

	private enum EnchantCheckType {
		/**
		 * no check
		 */
		NONE,

		/**
		 * must be equals
		 */
		EQUALS,

		/**
		 * all enchants must be lower or equals
		 */
		EQUALS_OR_LOWER,

		/**
		 * tool might have highter or equals enchant levels only on
		 */
		EQUALS_OR_HIGHTER,

		/**
		 * tool must not have enchant or have different enchants
		 */
		DIFFERENT,
	}

}
