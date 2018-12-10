package emanondev.quests.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.CollectionElementsSelectorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;

public class CropsData extends QCData {
	private final static String PATH_CROP_TYPES = "crops.list";
	

	public CropsData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		List<String> values = getSection().getStringList(PATH_CROP_TYPES);
		if (values!=null && !values.isEmpty())
			for (String value: values) {
				try {
					validCrops.add(Material.valueOf(value.toUpperCase()));
				} catch (Exception e) {}
			}
	}
	
	public boolean toggleCrop(Material mat) {
		if (mat == null || !validMaterials.contains(mat))
			return false;
		if (!validCrops.contains(mat))
			validCrops.add(mat);
		else
			validCrops.remove(mat);
		ArrayList<String> list = new ArrayList<String>();
		for (Material material:validCrops)
			list.add(material.toString());
		getSection().set(PATH_CROP_TYPES,list);
		setDirty(true);
		return true;
	}
	
	private final EnumSet<Material> validCrops = EnumSet.noneOf(Material.class);

	public Button getCropsSelectorButton(Gui gui) {
		return new CropsSelectorButton(gui);
	}
	
	private class CropsSelectorButton extends CollectionElementsSelectorButton<Material> {

		public CropsSelectorButton(Gui parent) {
			super("&9Crops Selector", new ItemBuilder(Material.IRON_HOE).setGuiProperty().build(), parent, validMaterials, false);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lCrops Selector");
			desc.add("&6Click to edit");
			if (!validCrops.isEmpty()) {
				desc.add("&6Valid types:");
				for (Material type:validCrops)
					desc.add("&6 - &e"+type);
			}
			else
				desc.add("&6No restrinctions on Crops type");
			return desc;
		}

		@Override
		public List<String> getElementDescription(Material element) {
			return Arrays.asList("&6"+element);
		}

		@Override
		public ItemStack getElementItem(Material element) {
			Material mat = Material.WHEAT_SEEDS;
			/*switch (element) {
			case CROPS:
				mat = Material.WHEAT;
				break;
			case POTATO:
				mat = Material.POTATO_ITEM;
				break;
			case CARROT:
				mat = Material.CARROT_ITEM;
				break;
			case BEETROOT_BLOCK:
				mat = Material.BEETROOT;
				break;
			case NETHER_WARTS:
				mat = Material.NETHER_STALK;
				break;
			default:
				break;
			}*/
			return new ItemBuilder(mat).setGuiProperty().build();
		}

		@Override
		public boolean currentCollectionContains(Material element) {
			return validCrops.contains(element);
		}

		@Override
		public boolean getIsWhitelist() {
			return true;
		}

		@Override
		public boolean onToggleElementRequest(Material element) {
			return toggleCrop(element);
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return false;
		}
		
	}

	public boolean isValidType(Material mat) {
		if(!validMaterials.contains(mat))
			return false;
		return validCrops.isEmpty() || validCrops.contains(mat);
	}
	
	private final static EnumSet<Material> validMaterials = EnumSet.of(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,Material.NETHER_WART);

}
