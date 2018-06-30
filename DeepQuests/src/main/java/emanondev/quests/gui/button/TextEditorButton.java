package emanondev.quests.gui.button;

import org.bukkit.entity.Player;

import emanondev.quests.command.CommandQuestText;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class TextEditorButton extends CustomButton {

	public TextEditorButton(CustomGui parent) {
		super(parent);
	}
	
	protected void requestText(Player p,String textBase,BaseComponent[] description) {
		CommandQuestText.requestText(p, textBase, description, this);
	}
	
	public abstract void onReicevedText(String text);
	protected void requestText(Player p,BaseComponent[] message) {
		CommandQuestText.requestText(p, message, this);
	}

}