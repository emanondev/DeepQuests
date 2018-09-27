package emanondev.quests.newgui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import emanondev.quests.newgui.gui.Gui;

public class GuiHandler implements Listener {
	
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private static void onClick(InventoryClickEvent event) {
		if (!(event.getView().getTopInventory().getHolder() instanceof Gui))
			return;
		event.setCancelled(true);
		if (event.getClickedInventory()!=null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
			Gui holder = (Gui) event.getView().getTopInventory().getHolder();
			if (event.getWhoClicked() instanceof Player)
				holder.onSlotClick((Player) event.getWhoClicked(),
						event.getRawSlot(), event.getClick());
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private static void onDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof Gui)
			event.setCancelled(true);
	}

}