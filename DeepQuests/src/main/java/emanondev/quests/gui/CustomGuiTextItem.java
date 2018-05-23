package emanondev.quests.gui;

import org.bukkit.entity.Player;

import emanondev.quests.command.CommandQuestText;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class CustomGuiTextItem extends CustomGuiItem {

	public CustomGuiTextItem(CustomGuiHolder parent) {
		super(parent);
	}
	
	protected void requestText(Player p,String textBase,BaseComponent[] description) {
		CommandQuestText.requestText(p, textBase, description, this);
	}
	
	public abstract void onReicevedText(String text);


}
