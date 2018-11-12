package emanondev.quests.interfaces;

import java.util.Collection;
import java.util.Map;

public interface QuestComponentTypeManager<T extends User<T>,K extends QuestComponent<T>,E extends QuestComponentType<T,K>> {
	
	/**
	 * @param id
	 * @return true if Type with id == type.getID() is registered 
	 */
	public default boolean existType(String id) {
		return getType(id)!=null;
	}
	
	/**
	 * 
	 * @param id
	 * @return type with type.getID().equals(id) or null
	 */
	public E getType(String id);
	
	/**
	 * 
	 * @param type - type to register
	 * @throws IllegalArgumentException - if (existType(type.getID()) == true)
	 */
	public void registerType(E type);
	
	/**
	 * 
	 * @param map
	 * @return 
	 */
	public default K read(Map<String,Object> map) {
		E type = getType(((String) map.get("type-id")).toUpperCase());
		
		if (type == null)
			throw new IllegalArgumentException("type "+type+" do not exist");
		return type.getInstance(map);
	}
	/**
	 * @return types
	 */
	public Collection<E> getTypes();
	

}
