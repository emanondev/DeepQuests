package emanondev.quests.utils;

import org.bukkit.configuration.MemorySection;

public abstract class YmlLoadableWithDisplay extends YmlLoadable{
	protected final static String PATH_DISPLAY = "display";
	protected final static String PATH_COOLDOWN_IS_ENABLED = "cooldown.enable";
	protected final static String PATH_COOLDOWN_AMOUNT = "cooldown.minutes";
	//private final DisplayStateInfo displayInfo;
	private final int minutes;
	private final boolean cooldown;
	public YmlLoadableWithDisplay(MemorySection m) {
		super(m);
		//displayInfo = loadDisplayInfo((MemorySection) m.get(PATH_DISPLAY));
		cooldown = loadCooldownAllowed(m);
		if (cooldown)
			minutes = loadCooldownMinutes(m);
		else
			minutes = -1;
	}
	protected boolean loadCooldownAllowed(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		if (!m.isBoolean(PATH_COOLDOWN_IS_ENABLED) && shouldCooldownAutogen()) {
			m.set(PATH_COOLDOWN_IS_ENABLED, getDefaultCooldownUse());
			shouldSave = true;
		}
		return m.getBoolean(PATH_COOLDOWN_IS_ENABLED,getDefaultCooldownUse());
	}
	
	protected abstract boolean getDefaultCooldownUse();
	protected abstract boolean shouldCooldownAutogen();
	protected int loadCooldownMinutes(MemorySection m) {
		if (m==null)
			throw new NullPointerException();
		if (!m.isInt(PATH_COOLDOWN_AMOUNT) && shouldCooldownAutogen()) {
			m.set(PATH_COOLDOWN_AMOUNT, getDefaultCooldownMinutes());
			shouldSave = true;
		}
		return m.getInt(PATH_COOLDOWN_AMOUNT, getDefaultCooldownMinutes());
		
	}
	protected abstract int getDefaultCooldownMinutes();

	protected abstract DisplayStateInfo loadDisplayInfo(MemorySection m);
		
	public long getCooldownTime() {
		if (cooldown == false)
			return -1L;
		else
			return (long) (minutes*60*1000);
	}
	public boolean isRepetable() {
		return cooldown;
	}
	public abstract DisplayStateInfo getDisplayInfo();
	
}
