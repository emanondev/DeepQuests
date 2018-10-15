package emanondev.quests.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
public class ItemStackUtility {
	public static ItemStack craftBase(Material m,short damage,boolean unbreakable) {
		return craftBase(m,damage,null,unbreakable);
	}
	
	public static ItemStack craftBase(Material m,short damage,Byte data,boolean unbreakable) {
		ItemStack item;
		if (data == null)
			item = new ItemStack(m,1);
		else
			item = new ItemStack(m,1,(short) 0,data);
		
		if (damage!=0)
			try {
				item.setDurability(damage);
			} catch (Exception e) {}
		
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
				ItemFlag.HIDE_DESTROYS,
				ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON,
				ItemFlag.HIDE_POTION_EFFECTS,
				ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}

}
