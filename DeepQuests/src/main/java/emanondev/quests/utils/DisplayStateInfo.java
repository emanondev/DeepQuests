package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.QCData;

public abstract class DisplayStateInfo extends QCData {

	private final static String PATH_DESC = ".desc";
	private final static String PATH_ITEM = ".item";
	private final static String PATH_HIDE = ".hide";

	private EnumMap<DisplayState, Info> infos = new EnumMap<DisplayState, Info>(DisplayState.class);

	public DisplayStateInfo(ConfigSection m, QuestComponent parent) {
		super(m.loadSection("display"),parent);
		for (int i = 0; i < DisplayState.values().length; i++) {
			infos.put(DisplayState.values()[i], new Info(m, DisplayState.values()[i]));
		}
	}

	private class Info {
		public Info(ConfigSection m, DisplayState state) {
			String basePath = state.toString();
			craftDisplayItem(basePath + PATH_ITEM, getDefaultItem(state), shouldItemAutogen(state));
			craftDisplayDescription(basePath + PATH_DESC, getDefaultDescription(state),
					shouldDescriptionAutogen(state));
			craftDisplayHide(basePath + PATH_HIDE, getDefaultHide(state), shouldHideAutogen(state));
		}

		private ItemStack item;
		private List<String> desc;
		private boolean hide;

		private void craftDisplayItem(String path, ItemStack defaults, boolean autosave) {
			if (getSection().isItemStack(path)) {
				item = getSection().getItemStack(path);
				if (item.getType()!=Material.AIR)
					return;
			}
			String tempItem = getSection().getString(path, null);
			ItemStack tempStack = null;
			if (tempItem != null && !tempItem.isEmpty()) {
				try {
					tempStack = MemoryUtils.getGuiItem(tempItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (tempStack == null || tempStack.getType() == Material.AIR) {
				tempStack = defaults;
				if (autosave) {
					getSection().set(path, MemoryUtils.getGuiItemString(tempStack));
					setDirtyLoad();
				}
			}
			item = tempStack;
		}

		private void craftDisplayDescription(String path, List<String> defaults, boolean autosave) {

			List<String> tempList = getSection().getStringList(path);
			if (tempList == null) {
				tempList = defaults;
				if (tempList != null && autosave) {
					getSection().set(path, tempList);
					setDirtyLoad();
				}
			}
			desc = tempList;
		}

		private void craftDisplayHide(String path, boolean defaults, boolean autosave) {
			boolean tempBool;
			if (getSection().isBoolean(path))
				tempBool = getSection().getBoolean(path, defaults);
			else {
				tempBool = defaults;
				if (autosave) {
					getSection().set(path, tempBool);
					setDirtyLoad();
				}
			}
			hide = tempBool;
		}

	}

	protected abstract boolean shouldHideAutogen(DisplayState state);

	protected abstract boolean shouldItemAutogen(DisplayState state);

	protected abstract boolean shouldDescriptionAutogen(DisplayState state);

	protected abstract boolean getDefaultHide(DisplayState state);

	protected abstract ItemStack getDefaultItem(DisplayState state);

	protected abstract List<String> getDefaultDescription(DisplayState state);

	public abstract ItemStack getGuiItem(Player p, DisplayState state);

	public ItemStack getItem(DisplayState state) {
		return new ItemStack(infos.get(state).item);
	}

	public ArrayList<String> getDescription(DisplayState state) {
		return new ArrayList<String>(infos.get(state).desc);
	}

	public boolean isHidden(DisplayState state) {
		return infos.get(state).hide;
	}

	public void setItem(DisplayState state, ItemStack item) {
		if (item == null)
			throw new NullPointerException();
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore())
				meta.setLore(null);
			if (meta.hasDisplayName())
				meta.setDisplayName(null);
			item.setItemMeta(meta);
		}
		infos.get(state).item = item;
		getSection().set(state.toString()+PATH_ITEM, item);
		setDirty(true);
	}

	public void setHide(DisplayState state, boolean hide) {
		infos.get(state).hide = hide;
		getSection().set(state.toString()+PATH_HIDE, hide);
		setDirty(true);
	}

	public void setDescription(DisplayState state, List<String> desc) {
				
		infos.get(state).desc = desc;
		getSection().set(state.toString()+PATH_DESC, desc);
		setDirty(true);
	}

}
