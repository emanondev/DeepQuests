package emanondev.quests.newgui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.newgui.GuiConfig;
import emanondev.quests.newgui.gui.PagedGui;
import emanondev.quests.utils.Utils;

public class NextPageButton extends APagedButton {
	private ItemStack item;
	
	public NextPageButton(PagedGui parent) {
		super(parent);
		this.item = new ItemStack(GuiConfig.Generic.NEXT_PAGE_ITEM);
		Utils.updateDescription(item, GuiConfig.Generic.NEXT_PAGE, getTargetPlayer(), true, 
				GuiConfig.TARGET_PAGE_HOLDER,String.valueOf(getParent().getPage()+1));
	}

	@Override
	public ItemStack getItem() {
		if (getParent().getMaxPage()<=getPage())
			return GuiConfig.Generic.EMPTY_BUTTON_ITEM;
		return item;
	}

	@Override
	public boolean update() {
		Utils.updateDescription(item, GuiConfig.Generic.NEXT_PAGE, getTargetPlayer(), true, 
				GuiConfig.TARGET_PAGE_HOLDER,String.valueOf(getParent().getPage()+1));
		return true;
	}

	@Override
	public void onClick(Player clicker, ClickType click) {
		if (getParent().incPage())
			clicker.openInventory(getParent().getInventory());
	}

}