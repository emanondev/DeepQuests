package emanondev.quests.task;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class NPCTaskInfo {
	public final static String PATH_NPC_ID = "npc-id";
	public final static String PATH_NPC_NAME = "npc-name";
	public final static String PATH_NPC_NAME_CONTAINS = "npc-name-contains";

	private final HashSet<Integer> ids = new HashSet<Integer>();
	private final String name;
	private final String nameContains;
	private final Task parent;
	private final MemorySection section;

	public NPCTaskInfo(MemorySection m, Task t) {
		if (t == null || m == null)
			throw new NullPointerException();
		this.parent = t;
		this.section = m;
		if (m.isInt(PATH_NPC_ID))
			ids.add(m.getInt(PATH_NPC_ID));
		else
			try {
				if (m.isList(PATH_NPC_ID))
					ids.addAll(m.getIntegerList(PATH_NPC_ID));
			} catch (Exception e) {
			}
		name = m.getString(PATH_NPC_NAME, null);
		nameContains = m.getString(PATH_NPC_NAME_CONTAINS, null);

	}

	public boolean hasId(int id) {
		return ids.contains(id);
	}

	public boolean addId(int id) {
		if (ids.contains(id))
			return false;
		ids.add(id);
		section.set(PATH_NPC_ID, new ArrayList<Integer>(ids));
		parent.setDirty(true);
		return true;
	}

	public boolean removeId(int id) {
		if (!ids.contains(id))
			return false;
		ids.remove(id);
		section.set(PATH_NPC_ID, new ArrayList<Integer>(ids));
		parent.setDirty(true);
		return true;
	}

	public EditorButtonFactory getIdSelectorButtonFactory() {
		return new NPCSelectorButtonFactory();
	}

	private class NPCSelectorButtonFactory implements EditorButtonFactory {
		private class NPCSelectorButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.SKULL_ITEM);

			public NPCSelectorButton(CustomGui parent) {
				super(parent);
				item.setDurability((short) 3);
				update();
			}

			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lNPC ID Selector");
				desc.add("&6Click to edit");
				if (ids.isEmpty())
					desc.add("&7No id restrictions are set");
				else {
					NPCRegistry registry = CitizensAPI.getNPCRegistry();
					desc.add("&7All listed npc id are &aAllowed");
					for (Integer id : ids)
						if (registry.getById(id) == null)
							desc.add(" &7- &a" + id);
						else
							desc.add(" &7- ID &a" + id + " &7(" + registry.getById(id).getName()+")");
				}
				StringUtils.setDescription(item, desc);
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new NPCSelectorGui(clicker).getInventory());
			}

			private class NPCSelectorGui extends CustomMultiPageGui<NPCButton> {
				public NPCSelectorGui(Player p) {
					super(p, NPCSelectorButton.this.getParent(), 6, 1);
					this.setFromEndCloseButtonPosition(8);
					this.setTitle(null, StringUtils.fixColorsAndHolders("&8NPC Selector"));
					for (NPC npc : CitizensAPI.getNPCRegistry().sorted())
						this.addButton(new NPCButton(this, npc));
					reloadInventory();
				}
			}
		}

		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new NPCSelectorButton(parent);
		}
	}

	private class NPCButton extends CustomButton {
		private ItemStack item;
		private NPC npc;

		public NPCButton(CustomGui parent, NPC npc) {
			super(parent);
			this.npc = npc;
			item = new ItemStack(Material.WOOL);
			item.setDurability((short) 6);
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			update();
		}

		public void update() {
			ArrayList<String> desc = new ArrayList<String>();
			if (hasId(npc.getId())) {
				desc.add("&6Npc: &f" + npc.getFullName());
				desc.add("&6ID: &a" + npc.getId());
				desc.add("&aAllowed");
				item.addUnsafeEnchantment(Enchantment.DURABILITY,1);
				
			} else {
				desc.add("&6Npc: &f" + npc.getFullName());
				desc.add("&6ID: &c" + npc.getId());
				desc.add("&cUnallowed");
				item.removeEnchantment(Enchantment.DURABILITY);
			}
			StringUtils.setDescription(item, desc);
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (hasId(npc.getId())) {
				if (removeId(npc.getId())) {
					update();
					getParent().reloadInventory();
				}
			} else {
				if (addId(npc.getId())) {
					update();
					getParent().reloadInventory();
				}
			}
		}
	}

	private boolean checkID(NPC npc) {
		return ids.contains(npc.getId());
	}

	private boolean checkName(NPC npc) {
		if (name == null || npc.getFullName().equals(name))
			return true;
		return false;
	}

	private boolean checkNameContains(NPC npc) {
		if (nameContains == null || npc.getFullName().contains(nameContains))
			return true;
		return false;
	}

	public boolean isValidNPC(NPC npc) {
		return checkID(npc) && checkName(npc) && checkNameContains(npc);
	}

}
