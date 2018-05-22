package emanondev.quests.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class CustomGuiHandler implements Listener {
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private void onClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof CustomGuiHolder)
			event.setCancelled(true);
		if (event.getClickedInventory()!=null && event.getClickedInventory().equals(event.getView().getTopInventory())) {
			CustomGuiHolder holder = (CustomGuiHolder) event.getView().getTopInventory().getHolder();
			if (event.getWhoClicked() instanceof Player)
				holder.onSlotClick((Player) event.getWhoClicked(),
						event.getRawSlot(), event.getClick());
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	private void onDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().getHolder() instanceof CustomGuiHolder)
			event.setCancelled(true);
	}

}
