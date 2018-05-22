package emanondev.quests.utils;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomGuiItem;
import emanondev.quests.gui.CustomLinkedGuiHolder;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.EditorGuiItemFactory;

public abstract class YmlLoadableWithCooldown extends YmlLoadable{
	protected final static String PATH_DISPLAY = "display";
	protected final static String PATH_COOLDOWN_IS_ENABLED = "cooldown.enable";
	protected final static String PATH_COOLDOWN_AMOUNT = "cooldown.minutes";
	
	private int minutes;
	private boolean repeatable;
	@SuppressWarnings("rawtypes")
	public YmlLoadableWithCooldown(MemorySection m) {
		super(m);
		repeatable = loadCooldownAllowed(m);
		minutes = Math.max(0, loadCooldownMinutes(m));
		this.addToEditor(new EditCooldownFactory());
	}
	private boolean loadCooldownAllowed(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		if (!m.isBoolean(PATH_COOLDOWN_IS_ENABLED) && shouldCooldownAutogen()) {
			m.set(PATH_COOLDOWN_IS_ENABLED, getDefaultCooldownUse());
			dirty = true;
		}
		return m.getBoolean(PATH_COOLDOWN_IS_ENABLED,getDefaultCooldownUse());
	}
	
	private int loadCooldownMinutes(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		if (!m.isInt(PATH_COOLDOWN_AMOUNT) && shouldCooldownAutogen()) {
			m.set(PATH_COOLDOWN_AMOUNT, getDefaultCooldownMinutes());
			dirty = true;
		}
		return m.getInt(PATH_COOLDOWN_AMOUNT, getDefaultCooldownMinutes());
	}
	
	protected abstract boolean getDefaultCooldownUse();
	protected abstract boolean shouldCooldownAutogen();
	protected abstract int getDefaultCooldownMinutes();
	
	public boolean setRepeatable(boolean value) {
		if (this.repeatable != value) {
			this.repeatable = value;
			this.getSection().set(PATH_COOLDOWN_IS_ENABLED, this.repeatable);
			this.setDirty(true);
			return true;
		}
		return false;
	}
	
	public boolean setCooldownTime(int minutes){
		minutes = Math.max(0,minutes);
		if (this.minutes!= minutes) {
			this.minutes = minutes;
			this.getSection().set(PATH_COOLDOWN_AMOUNT, this.minutes);
			this.setDirty(true);
			return true;
		}
		return false;
			
	}
	
	/**
	 * 
	 * @return the cooldowntime (milliseconds) to wait when the object (mission/quest) has been completed
	 * 
	 * note: this will still return a number even if the object is not repeatable
	 */
	public long getCooldownTime() {
		return (long) (minutes*60*1000);
	}
	/**
	 * 
	 * @return true if the object (mission/quest) is repeatable
	 */
	public boolean isRepetable() {
		return repeatable;
	}
	public abstract DisplayStateInfo getDisplayInfo();
	private class EditCooldownFactory<T extends YmlLoadable> implements EditorGuiItemFactory<T> {
		private class EditCooldownButton extends CustomGuiItem {
			private ItemStack item = new ItemStack(Material.WATCH);
			public EditCooldownButton(EditorGui<?> parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lCooldown editor"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to edit cooldown"));
				if (isRepetable())
					lore.add(StringUtils.fixColorsAndHolders("&aCooldown is &aEnabled"));
				else
					lore.add(StringUtils.fixColorsAndHolders("&aCooldown is &cDisabled"));
				lore.add(StringUtils.fixColorsAndHolders("&aTime &a"+StringUtils.getStringCooldown(getCooldownTime())));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new CooldownEditorGui(clicker,
						(EditorGui<?>) this.getParent()).getInventory());
			}
		}
		@Override
		public CustomGuiItem getCustomGuiItem(EditorGui<T> parent) {
			return new EditCooldownButton(parent);
		}
	}
	private class CooldownEditorGui extends CustomLinkedGuiHolder<CustomGuiItem> {
		public CooldownEditorGui(Player p, EditorGui<?> previusHolder) {
			super(p,previusHolder, 6);
			items.put(2, new RepeatableButton());
			items.put(6, new ClockButton());
			items.put(19, new TimerEditorButton(1));//1 min
			items.put(20, new TimerEditorButton(10));// 10 min
			items.put(21, new TimerEditorButton(60));// 1 h
			items.put(22, new TimerEditorButton(360));// 6 h
			items.put(23, new TimerEditorButton(1440)); // 1 d
			items.put(24, new TimerEditorButton(10080)); // 1 w
			items.put(25, new TimerEditorButton(120960)); //12 w
			items.put(28, new TimerEditorButton(-1));//1 min
			items.put(29, new TimerEditorButton(-10));// 10 min
			items.put(30, new TimerEditorButton(-60));// 1 h
			items.put(31, new TimerEditorButton(-360));// 6 h
			items.put(32, new TimerEditorButton(-1440)); // 1 d
			items.put(33, new TimerEditorButton(-10080)); // 1 w
			items.put(34, new TimerEditorButton(-120960)); //12 w
			this.setFromEndCloseButtonPosition(8);
			reloadInventory();
		}
		private class ClockButton extends CustomGuiItem {
			private ItemStack item = new ItemStack(Material.WATCH);
			public ClockButton() {
				super(CooldownEditorGui.this);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&aTime: "+StringUtils.getStringCooldown(getCooldownTime())));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&7("+getCooldownTime()/1000+" min)"));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {}
		}
		
		private class RepeatableButton extends CustomGuiItem {
			public RepeatableButton() {
				super(CooldownEditorGui.this);
				update();
			}
			private ItemStack item = new ItemStack(Material.WOOL);
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				if (isRepetable()) {
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Repeatable: &aTRUE"));
					item.setDurability((short) 5);
				}
				else {
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Repeatable: &cFALSE"));
					item.setDurability((short) 14);
				}
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				setRepeatable(!isRepetable());
				getParent().reloadInventory();
			}
		}
		private class TimerEditorButton extends CustomGuiItem {
			private int minutes;
			public TimerEditorButton(int min) {
				super(CooldownEditorGui.this);
				this.minutes = min;
				
				ItemMeta meta = item.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				if (minutes>0) {
					this.item.setDurability((short) 5);
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&aAdd "+StringUtils.getStringCooldown(((long) minutes)*1000*60)));
					lore.add(StringUtils.fixColorsAndHolders("&7("+minutes+" min)"));
				}
				else {
					this.item.setDurability((short) 14);
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&cRemove "+StringUtils.getStringCooldown(((long) -minutes)*1000*60)));
					lore.add(StringUtils.fixColorsAndHolders("&7("+(-minutes)+" min)"));
				}
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			private ItemStack item = new ItemStack(Material.WOOL);
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {}
			@Override
			public void onClick(Player clicker, ClickType click) {
				if (setCooldownTime(((int) (getCooldownTime()/1000/60))+minutes))
					getParent().update();
			}
		}
		
	}
	
}
