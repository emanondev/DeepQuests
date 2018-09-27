package emanondev.quests.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.CollectionElementsSelectorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.task.Task;
import emanondev.quests.utils.ItemBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class NPCTaskInfo extends QCData {
	public final static String PATH_NPC_ID = "npc-id";
	public final static String PATH_NPC_NAME = "npc-name";
	public final static String PATH_NPC_NAME_CONTAINS = "npc-name-contains";

	private final HashSet<Integer> ids = new HashSet<Integer>();
	private final String name;
	private final String nameContains;

	public NPCTaskInfo(ConfigSection m, Task t) {
		super(m,t);
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
		getSection().set(PATH_NPC_ID, new ArrayList<Integer>(ids));
		setDirty(true);
		return true;
	}

	public boolean removeId(int id) {
		if (!ids.contains(id))
			return false;
		ids.remove(id);
		getSection().set(PATH_NPC_ID, new ArrayList<Integer>(ids));
		setDirty(true);
		return true;
	}
	private static Set<Integer> getIdSet() {
		Set<Integer> set = new HashSet<Integer>();
		for (NPC npc : CitizensAPI.getNPCRegistry().sorted())
			set.add(npc.getId());
		return set;
	}
	public Button getNpcSelectorButton(Gui parent) {
		return new NPCSelectorButton(parent);
	}
	private class NPCSelectorButton extends CollectionElementsSelectorButton<Integer> {

		public NPCSelectorButton(Gui parent) {
			super("&8NPC Selector", 
				new ItemBuilder(Material.SKULL_ITEM).setDamage(3).setGuiProperty().build(), 
				parent, getIdSet(), false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lTarget Npc Selector");
			desc.add("&6Click to edit");
			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			if (ids.size()>0) {
				desc.add("&7Valid Targets");
				for (int id: ids) {
					NPC npc = registry.getById(id);
					if (npc != null)
						desc.add(" - &e"+npc.getFullName()+ " &8ID: &7"+id);
					else
						desc.add(" - &9ID: &e"+id);
					
				}
			}
			else
				desc.add("&7No Target restrictions are set");
				
			return desc;
		}

		@Override
		public List<String> getElementDescription(Integer element) {
			ArrayList<String> desc = new ArrayList<String>();
			NPC npc = CitizensAPI.getNPCRegistry().getById(element);
			if (npc != null) {
				desc.add("&9FullName: &e"+npc.getFullName());
				desc.add("&9Name: &e"+npc.getName());
				desc.add("&9ID: &e"+element);
				Entity en = npc.getEntity();
				if (en != null) {
					desc.add("&9Type: &e"+en.getType());
					Location l = en.getLocation();
					desc.add("&9Location: &e"+l.getWorld().getName()+" "+l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
				}
			}
			else
				desc.add("&9ID: &e"+element);
			return desc;
		}

		@Override
		public ItemStack getElementItem(Integer element) {
			return new ItemBuilder(Material.WOOL).setGuiProperty().setDamage(1).build();
		}

		@Override
		public boolean getIsWhitelist() {
			return true;
		}

		@Override
		public boolean onToggleElementRequest(Integer element) {
			if (hasId(element))
				return removeId(element);
			return addId(element);
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return false;
		}

		@Override
		public boolean currentCollectionContains(Integer element) {
			return ids.contains(element);
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
