package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class DisplayStateInfo {
	
	private final static String PATH_LORE = ".lore";
	private final static String PATH_ITEM = ".item";
	private final static String PATH_TITLE = ".title";
	private final static String PATH_HIDE = ".hide";
	
	protected boolean shouldSave = false;//utility
	public boolean shouldSave(){
		return shouldSave;
	}
	
	private EnumMap<DisplayState,Info> infos = new EnumMap<DisplayState,Info>(DisplayState.class);
	

	private final YmlLoadableWithDisplay parent;
	protected YmlLoadableWithDisplay getParent() {
		return parent;
	}
	public DisplayStateInfo(MemorySection m,YmlLoadableWithDisplay parent) {
		if (m == null || parent == null)
			throw new NullPointerException();
		MemorySection m2 = (MemorySection) m.get("display");
		if (m2==null)
			m2 = (MemorySection) m.createSection("display");
		this.parent = parent;
		for (int i = 0; i< DisplayState.values().length; i++) {
			infos.put(DisplayState.values()[i],new Info(m2,DisplayState.values()[i]));
		}
	}
	
	private class Info {
		public Info(MemorySection m,DisplayState state) {
			String basePath = state.toString().toLowerCase();
			craftDisplayItem(m,basePath+PATH_ITEM,getDefaultItem(state),shouldItemAutogen(state));
			craftDisplayTitle(m,basePath+PATH_TITLE,getDefaultTitle(state),shouldTitleAutogen(state));
			craftDisplayLore(m,basePath+PATH_LORE,getDefaultLore(state),shouldLoreAutogen(state));
			craftDisplayHide(m,basePath+PATH_HIDE,getDefaultHide(state),shouldHideAutogen(state));
		}
		private ItemStack item;
		private List<String> lore;
		private String title;
		private boolean hide;
		
		private void craftDisplayItem(MemorySection m, String path, ItemStack defaults,boolean autosave) {
			String tempItem = m.getString(path,null);
			ItemStack tempStack = null;
			if (tempItem!=null&&!tempItem.isEmpty()) {
				try {
					tempStack = MemoryUtils.getGuiItem(tempItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (tempStack==null || tempStack.getType()==Material.AIR) {
				tempStack = defaults;
				if (autosave) {
					m.set(path, MemoryUtils.getGuiItemString(tempStack));
					shouldSave = true;
				}
			}
			item = tempStack;
		}

		private void craftDisplayLore(MemorySection m, String path,List<String> defaults, boolean autosave) {
		
			List<String> tempList = MemoryUtils.getStringList(m,path);
			if (tempList == null) {
				tempList = defaults;
				if (tempList!=null && autosave) {
					m.set(path, tempList);
					shouldSave = true;
				}
			}
			lore = tempList;
		}

		private void craftDisplayTitle(MemorySection m, String path,String defaults, boolean autosave) {
			String tempText = m.getString(path);
			if (tempText==null) {
				tempText = defaults;
				if (tempText==null)
					tempText = getParent().getDisplayName();
				if (autosave) {
					m.set(path, tempText);
					shouldSave = true;
				}
			}
			title = tempText;
		}

		private void craftDisplayHide(MemorySection m, String path, boolean defaults,boolean autosave) {
			boolean tempBool;
			if (m.isBoolean(path))
				tempBool = m.getBoolean(path,defaults);
			else {
				tempBool = defaults;
				if (autosave) {
					m.set(path,tempBool);
					shouldSave = true;
				}
			}
			hide = tempBool;
		}
		
	}
	
	protected abstract boolean shouldHideAutogen(DisplayState state);
	protected abstract boolean shouldItemAutogen(DisplayState state);
	protected abstract boolean shouldLoreAutogen(DisplayState state);
	protected abstract boolean shouldTitleAutogen(DisplayState state);
	protected abstract boolean getDefaultHide(DisplayState state);
	
	protected abstract ItemStack getDefaultItem(DisplayState state);
	
	protected abstract List<String> getDefaultLore(DisplayState state);
	protected abstract String getDefaultTitle(DisplayState state);
	public abstract ItemStack getGuiItem(Player p,DisplayState state);

	public ItemStack getItem(DisplayState state) {
		return new ItemStack(infos.get(state).item);
	}
	public List<String> getLore(DisplayState state){
		return new ArrayList<String>(infos.get(state).lore);
	}
	public boolean isHidden(DisplayState state) {
		return infos.get(state).hide;
	}
	public String getTitle(DisplayState state) {
		return infos.get(state).title;
	}
	
	public void setItem(DisplayState state,ItemStack item) {
		infos.get(state).item = item;
	}
	public void setLore(DisplayState state,List<String> lore) {
		infos.get(state).lore = lore;
	}
	public void setHide(DisplayState state,boolean hide) {
		infos.get(state).hide = hide;
	}
	public void setTitle(DisplayState state,String title) {
		infos.get(state).title = title;
	}

}
