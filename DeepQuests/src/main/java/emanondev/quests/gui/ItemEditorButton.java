package emanondev.quests.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.command.CommandQuestItem;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class ItemEditorButton  extends CustomButton {

	public ItemEditorButton(CustomGui parent) {
		super(parent);
	}
	
	protected void requestItem(Player p,BaseComponent[] description) {
		CommandQuestItem.requestItem(p, description, this);
	}
	
	public abstract void onReicevedItem(ItemStack item);
}
