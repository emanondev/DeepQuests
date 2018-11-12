package emanondev.quests.interfaces;

import org.bukkit.World;

public interface QuestComponentWithWorlds<T extends User<T>> extends QuestComponent<T> {

	/**
	 * 
	 * @param world
	 * @return true if this should be seen on selected world
	 */
	public boolean isWorldAllowed(World world);
}
