package emanondev.quests.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.CollectionElementsSelectorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MythicMobData extends QCData {
	private final static String PATH_INTERNAL_NAMES = "mob-id";
	private final HashSet<String> internalNames = new HashSet<String>();

	public MythicMobData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		List<String> list = getSection().getStringList(PATH_INTERNAL_NAMES);
		if (list!=null)
			internalNames.addAll(list);
	}
	public boolean isValidMythicMob(String internalName) {
		if (internalNames.contains(internalName))
			return true;
		return false;
	}
	
	public boolean hasInternalName(String internalName) {
		return internalNames.contains(internalName);
	}

	public boolean addInternalName(String internalName) {
		if (internalNames.contains(internalName))
			return false;
		internalNames.add(internalName);
		getSection().set(PATH_INTERNAL_NAMES, new ArrayList<String>(internalNames));
		setDirty(true);
		return true;
	}

	public boolean removeInternalName(String internalName) {
		if (!internalNames.contains(internalName))
			return false;
		internalNames.remove(internalName);
		getSection().set(PATH_INTERNAL_NAMES, new ArrayList<String>(internalNames));
		setDirty(true);
		return true;
	}

	public Button getMythicMobSelectorButton(Gui parent) {
		return new MythicMobSelectorButton(parent);
	}
	
	private class MythicMobSelectorButton extends CollectionElementsSelectorButton<MythicMob> {

		public MythicMobSelectorButton(Gui parent) {
			super("&8MythicMob Selector", new ItemBuilder(Material.SKELETON_SKULL).setDamage(1).setGuiProperty().build(), parent, MythicMobs.inst().getMobManager().getMobTypes(), false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lMythicMob Selector");
			desc.add("&6Click to edit");
			if (internalNames.isEmpty())
				desc.add("&7No id restrictions are set");
			else {
				desc.add("&7All listed MythicMob id are &aAllowed");
				for (String internalName : internalNames)
					if (MythicMobs.inst().getMobManager().getMythicMob(internalName) == null)
						desc.add(" &7- &a" + internalName);
					else
						desc.add(" &7- ID &a" + internalName + " &7(" + MythicMobs.inst().getMobManager().getMythicMob(internalName).getDisplayName()+")");
			}
			return desc;
		}

		@Override
		public List<String> getElementDescription(MythicMob mob) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Mob: &f" + mob.getDisplayName());
			desc.add("&6ID: &a" + mob.getInternalName());
			desc.add("&6Type: &a" + mob.getEntityType());
			if (hasInternalName(mob.getInternalName()))
				desc.add("&aAllowed");
			else
				desc.add("&cUnallowed");
			return desc;
		}

		@Override
		public ItemStack getElementItem(MythicMob element) {
			return new ItemBuilder(Material.PINK_WOOL).setGuiProperty().build();
		}

		@Override
		public boolean currentCollectionContains(MythicMob element) {
			return hasInternalName(element.getInternalName());
		}

		@Override
		public boolean getIsWhitelist() {
			return false;
		}

		@Override
		public boolean onToggleElementRequest(MythicMob element) {
			if (hasInternalName(element.getInternalName()))
				return removeInternalName(element.getInternalName());
			return addInternalName(element.getInternalName());
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return false;
		}
		
	}

}
