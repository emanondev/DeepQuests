package emanondev.quests.utils;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.EditorButtonFactory;

public abstract class YmlLoadableWithCooldown extends YmlLoadable{
	protected final static String PATH_DISPLAY = "display";
	protected final static String PATH_COOLDOWN_IS_ENABLED = "cooldown.enable";
	protected final static String PATH_COOLDOWN_AMOUNT = "cooldown.minutes";
	
	private long minutes;
	private boolean repeatable;
	public YmlLoadableWithCooldown(MemorySection m) {
		super(m);
		repeatable = loadCooldownAllowed(m);
		minutes = Math.max(0, loadCooldownMinutes(m));
		this.addToEditor(8,new CooldownEditorFactory());
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
	
	private long loadCooldownMinutes(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		return m.getLong(PATH_COOLDOWN_AMOUNT, getDefaultCooldownMinutes());
	}
	
	protected abstract boolean getDefaultCooldownUse();
	protected abstract boolean shouldCooldownAutogen();
	protected abstract long getDefaultCooldownMinutes();
	
	public boolean setRepeatable(boolean value) {
		if (this.repeatable != value) {
			this.repeatable = value;
			this.getSection().set(PATH_COOLDOWN_IS_ENABLED, this.repeatable);
			this.setDirty(true);
			return true;
		}
		return false;
	}
	
	public boolean setCooldownMinutes(long value){
		value = Math.max(0,value);
		if (this.minutes != value) {
			this.minutes = value;
			this.getSection().set(PATH_COOLDOWN_AMOUNT, this.minutes);
			this.setDirty(true);
			return true;
		}
		return false;
			
	}
	public long getCooldownMinutes() {
		return minutes;
	}
	/**
	 * 
	 * @return the cooldowntime (milliseconds) to wait when the object (mission/quest) has been completed
	 * 
	 * note: this will still return a number even if the object is not repeatable
	 */
	public long getCooldownTime() {
		return minutes*60*1000;
	}
	/**
	 * 
	 * @return true if the object (mission/quest) is repeatable
	 */
	public boolean isRepetable() {
		return repeatable;
	}
	public abstract DisplayStateInfo getDisplayInfo();
	private class CooldownEditorFactory implements EditorButtonFactory {
		private class CooldownEditorButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.WATCH);
			public CooldownEditorButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lCooldown Editor");
				desc.add("&6Click to edit");
				if (isRepetable()) {
					desc.add("&eCooldown is &aEnabled");
					desc.add("&eTime &a"+StringUtils.getStringCooldown(getCooldownTime()));
				}
				else {
					desc.add("&eCooldown is &cDisabled");
					desc.add("&7Time &m"+StringUtils.getStringCooldown(getCooldownTime()));
				}
				StringUtils.setDescription(item, desc);
			}
			@SuppressWarnings("rawtypes")
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new CooldownEditorGui(clicker,
						(EditorGui) this.getParent()).getInventory());
			}
			private class CooldownEditorGui extends CustomLinkedGui<CustomButton> {
				public CooldownEditorGui(Player p, @SuppressWarnings("rawtypes") EditorGui previusHolder) {
					super(p,previusHolder, 6);
					this.addButton(2, new RepeatableButton());
					this.addButton(6, new ClockButton());
					this.addButton(19, new TimerEditorButton(1));//1 min
					this.addButton(20, new TimerEditorButton(10));// 10 min
					this.addButton(21, new TimerEditorButton(60));// 1 h
					this.addButton(22, new TimerEditorButton(360));// 6 h
					this.addButton(23, new TimerEditorButton(1440)); // 1 d
					this.addButton(24, new TimerEditorButton(10080)); // 1 w
					this.addButton(25, new TimerEditorButton(120960)); //12 w
					this.addButton(28, new TimerEditorButton(-1));//1 min
					this.addButton(29, new TimerEditorButton(-10));// 10 min
					this.addButton(30, new TimerEditorButton(-60));// 1 h
					this.addButton(31, new TimerEditorButton(-360));// 6 h
					this.addButton(32, new TimerEditorButton(-1440)); // 1 d
					this.addButton(33, new TimerEditorButton(-10080)); // 1 w
					this.addButton(34, new TimerEditorButton(-120960)); //12 w
					this.setFromEndCloseButtonPosition(8);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&8Cooldown Editor"));
					reloadInventory();
				}
				private class ClockButton extends CustomButton {
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
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&eTime: &a"+StringUtils.getStringCooldown(getCooldownTime()));
						desc.add("&7("+minutes+" min)");
						StringUtils.setDescription(item, desc);
					}
					@Override
					public void onClick(Player clicker, ClickType click) {}
				}
				
				private class RepeatableButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.WOOL);
					public RepeatableButton() {
						super(CooldownEditorGui.this);
						update();
					}
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
						update();
						getParent().reloadInventory();
					}
				}
				private class TimerEditorButton extends CustomButton {
					private long mins;
					private ItemStack item = new ItemStack(Material.WOOL);
					public TimerEditorButton(long min) {
						super(CooldownEditorGui.this);
						this.mins = min;
						ArrayList<String> desc = new ArrayList<String>();
						if (this.mins>0) {
							this.item.setDurability((short) 5);
							desc.add("&aAdd "+StringUtils.getStringCooldown(((long) mins)*1000*60));
							desc.add("&7("+mins+" min)");
						}
						else {
							this.item.setDurability((short) 14);
							desc.add("&cRemove "+StringUtils.getStringCooldown(((long) -mins)*1000*60));
							desc.add("&7("+(-mins)+" min)");
						}
						StringUtils.setDescription(item, desc);
					}
					@Override
					public ItemStack getItem() {
						return item;
					}
					public void update() {}
					@Override
					public void onClick(Player clicker, ClickType click) {
						if (setCooldownMinutes(getCooldownMinutes()+this.mins))
							getParent().update();
					}
				}
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new CooldownEditorButton(parent);
		}
	}
	
}
