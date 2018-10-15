package emanondev.quests.newgui.button;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.command.CommandQuestItem;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class ItemEditorButton extends AButton {
	protected ItemStack item = new ItemStack(Material.BARRIER);
	public ItemEditorButton(Gui parent) {
		super(parent);
		
	}
	public abstract ItemStack getCurrentItem();
	
	public abstract void onReicevedItem(ItemStack item);

	@Override
	public ItemStack getItem() {
		ItemStack current = getCurrentItem();
		if (current==null || current.getType()==Material.AIR)
			item.setType(Material.BARRIER);
		else
			item.setType(current.getType());
		Utils.updateDescription(item, getButtonDescription(), getTargetPlayer(), true);
		return item;
	}
	
	public abstract List<String> getButtonDescription();

	@Override
	public boolean update() {
		return true;
	}
	protected void requestItem(Player p,BaseComponent[] description) {
		CommandQuestItem.requestItem(p, description, this);
	}
}
