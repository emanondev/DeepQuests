package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import emanondev.quests.interfaces.Paths;

public class ItemStackData extends QuestComponentData {
	private ItemStack item = null;

	public ItemStackData(Map<String,Object> map) {
		try {
			item = (ItemStack) map.getOrDefault(Paths.ITEMSTACK_INFO,item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean setItem(ItemStack item) {
		if (this.item == item)
			return false;
		if (item==null) {
			this.item = null;
			return true;
		}
		if (this.item==null) {
			this.item = new ItemStack(item);
			this.item.setAmount(1);
			return true;
		}
		if (this.item.isSimilar(item))
			return false;
		this.item = new ItemStack(item);
		this.item.setAmount(1);
		return true;
	}

	public ItemStack getItem() {
		if (item == null)
			return null;
		return new ItemStack(item);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(Paths.ITEMSTACK_INFO,item);
		return map;
	}

}
