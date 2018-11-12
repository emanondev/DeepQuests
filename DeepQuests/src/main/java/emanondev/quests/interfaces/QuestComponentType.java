package emanondev.quests.interfaces;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public interface QuestComponentType<T extends User<T>,E extends QuestComponent<T>> {
	
	/**
	 * 
	 * @return an item for display utility
	 */
	public ItemStack getGuiItem();
	/**
	 * 
	 * @return description of how this works
	 */
	public List<String> getDescription();
	/**
	 * 
	 * @return an unique key for this
	 */
	public String getID();

	/**
	 * 
	 * @param map - values
	 * @return an instance for this
	 */
	public E getInstance(Map<String,Object> map);

}
