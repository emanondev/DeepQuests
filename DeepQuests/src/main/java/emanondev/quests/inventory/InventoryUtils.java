package emanondev.quests.inventory;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;

public class InventoryUtils {
	/**
	 * @param p
	 * @param item
	 *            note: item.getAmount() is ignored
	 * @param amount
	 * @param mode
	 * @return the remaining amount given (or given + dropped)
	 */
	public static int giveAmount(HumanEntity p, ItemStack item, final int amount, final ExcessManage mode) {
		if (p == null || mode == null || item == null)
			throw new NullPointerException();
		if (amount < 0)
			throw new IllegalArgumentException();
		if (amount == 0)
			return 0;
		int remains = amount;
		// ItemStack[] snapshot;
		// if (mode == ExcessManage.CANCEL)
		// snapshot = p.getInventory().getContents();
		while (remains > 0) {
			item.setAmount(Math.min(item.getMaxStackSize(), remains));
			HashMap<Integer, ItemStack> map = p.getInventory().addItem(item);
			remains = remains - Math.min(item.getMaxStackSize(), remains);
			if (map.isEmpty())
				continue;
			remains = remains + map.get(0).getAmount();
			break;
		}

		updateInventory(p);

		if (remains == 0)
			return amount;

		switch (mode) {
		case DELETE_EXCESS:
			return amount - remains;
		case DROP_EXCESS:
			while (remains > 0) {
				int drop = Math.min(remains, 64);
				item.setAmount(drop);
				p.getWorld().dropItem(p.getEyeLocation(), item);
				remains -= drop;
			}
			return amount;
		case CANCEL:
			// p.getInventory().get.forEach((slot,item)->{

			// });

			removeAmount(p, item, amount - remains, LackManage.REMOVE_MAX_POSSIBLE);
			// item.setAmount(amount-remains);
			// p.getInventory().removeItem(item);
			return 0;
		}
		throw new IllegalArgumentException();
	}

	private static void updateInventory(HumanEntity h) {
		if (h instanceof Player)
			Bukkit.getScheduler().runTaskLater(Quests.get(), new Runnable() {
				@Override
				public void run() {
					((Player) h).updateInventory();
				}
			}, 1L);
	}

	/**
	 * @param p
	 * @param item
	 *            note: item.getAmount() is ignored
	 * @param amount
	 * @param mode
	 * @return the removed amount
	 */
	public static int removeAmount(HumanEntity p, ItemStack item, final int amount, final LackManage mode) {
		if (p == null || mode == null || item == null)
			throw new NullPointerException();
		if (amount < 0)
			throw new IllegalArgumentException();
		if (amount == 0)
			return 0;
		switch (mode) {
		case REMOVE_MAX_POSSIBLE: {
			item.setAmount(amount);
			HashMap<Integer, ItemStack> map = p.getInventory().removeItem(item);

			updateInventory(p);

			if (map.isEmpty())
				return amount;
			else
				return amount - map.get(0).getAmount();

		}
		case CANCEL: {
			if (p.getInventory().containsAtLeast(item, amount)) {
				item.setAmount(amount);
				HashMap<Integer, ItemStack> map = p.getInventory().removeItem(item);

				updateInventory(p);

				if (map.isEmpty())
					return amount;
				else
					return amount - map.get(0).getAmount();
			}

			return 0;
		}
		}
		throw new IllegalArgumentException();
	}

	public enum ExcessManage {
		/**
		 * drops if front of the player any items that can't be holded by the player
		 */
		DROP_EXCESS,
		/**
		 * remove any items that can't be holded by the player
		 */
		DELETE_EXCESS,
		/**
		 * if player has not enough space nothing is given to the player
		 */
		CANCEL,
	}

	public enum LackManage {
		/**
		 * remove the max number of items up to amount
		 */
		REMOVE_MAX_POSSIBLE,
		/**
		 * if there aren't enough items to remove, nothing is removed
		 */
		CANCEL,
	}

	public static boolean removeOneFromSlot(HumanEntity p, EquipmentSlot hand, ItemStack item) {
		ItemStack finalItem;
		switch (hand) {
		case HAND:
			finalItem = p.getInventory().getItemInMainHand();
			break;
		case OFF_HAND:
			finalItem = p.getInventory().getItemInOffHand();
		default:
			return false;
		}
		if (!finalItem.isSimilar(item))
			return false;
		finalItem.setAmount(finalItem.getAmount() - 1);
		switch (hand) {
		case HAND:
			p.getInventory().setItemInMainHand(finalItem);
			break;
		case OFF_HAND:
			p.getInventory().setItemInOffHand(finalItem);
		default:
			return false;
		}

		updateInventory(p);

		return true;
	}
}
