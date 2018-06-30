package emanondev.quests.utils;

import org.bukkit.entity.Player;

import emanondev.quests.gui.CustomGui;

public interface Applyable<T extends YmlLoadable> {
	/**
	 * @return util for display purpose
	 * should descript what this object does
	 */
	public String getDescription();
	/**
	 * 
	 * @return the object that has this applied
	 */
	public T getParent();
	/**
	 * 
	 * @return the Type
	 */
	public ApplyableType<T> getType();
	/**
	 * 
	 * @return the Type Key
	 */
	public String getKey();
	/**
	 * Open gui editor of this item
	 * @param player - the player that should open it
	 */
	public void openEditorGui(Player player);
	/**
	 * Open gui editor of this item
	 * @param player - the player that should open it
	 * @param previusHolder - previus gui displayed by Player
	 */
	public void openEditorGui(Player player,CustomGui previusHolder);
	/**
	 * unique key for this object on his parent
	 * @return
	 */
	public String getNameID();
	/**
	 * @return util for display purpose
	 * should descript what this object does
	 */
	public String getInfo();
}