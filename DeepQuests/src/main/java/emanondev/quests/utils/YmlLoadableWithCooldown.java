package emanondev.quests.utils;

import org.bukkit.configuration.MemorySection;

public abstract class YmlLoadableWithCooldown extends YmlLoadable{
	protected final static String PATH_DISPLAY = "display";
	protected final static String PATH_COOLDOWN_IS_ENABLED = "cooldown.enable";
	protected final static String PATH_COOLDOWN_AMOUNT = "cooldown.minutes";
	
	private int minutes;
	private boolean repeatable;
	public YmlLoadableWithCooldown(MemorySection m) {
		super(m);
		repeatable = loadCooldownAllowed(m);
		minutes = loadCooldownMinutes(m);
	}
	private boolean loadCooldownAllowed(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		if (!m.isBoolean(PATH_COOLDOWN_IS_ENABLED) && shouldCooldownAutogen()) {
			m.set(PATH_COOLDOWN_IS_ENABLED, getDefaultCooldownUse());
			setDirty(true);
		}
		return m.getBoolean(PATH_COOLDOWN_IS_ENABLED,getDefaultCooldownUse());
	}
	
	private int loadCooldownMinutes(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		if (!m.isInt(PATH_COOLDOWN_AMOUNT) && shouldCooldownAutogen()) {
			m.set(PATH_COOLDOWN_AMOUNT, getDefaultCooldownMinutes());
			setDirty(true);
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
		if (this.minutes!=minutes) {
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
	
}
