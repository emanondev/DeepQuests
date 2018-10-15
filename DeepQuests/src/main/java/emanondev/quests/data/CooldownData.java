package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.AmountSelectorButton;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.Utils;

public class CooldownData extends QCData {
	protected final static String PATH_COOLDOWN_IS_ENABLED = "cooldown.enable";
	protected final static String PATH_COOLDOWN_AMOUNT = "cooldown.minutes";
	//private final long defaultTime;
	//private final boolean defaultUse;
	
	private long minutes;
	private boolean repeatable;

	public CooldownData(ConfigSection section, QuestComponent parent,long defaultTime,boolean defaultUse) {
		super(section, parent);
		//this.defaultTime = Math.max(0,defaultTime);
		//this.defaultUse = defaultUse;
		repeatable = getSection().getBoolean(PATH_COOLDOWN_IS_ENABLED, defaultUse);
		minutes = Math.max(0,getSection().getLong(PATH_COOLDOWN_AMOUNT, defaultTime));
	}
	public boolean setRepeatable(boolean value) {
		if (this.repeatable != value) {
			this.repeatable = value;
			this.getSection().set(PATH_COOLDOWN_IS_ENABLED, this.repeatable);
			this.setDirty(true);
			return true;
		}
		return false;
	}

	public boolean setCooldownMinutes(long value) {
		value = Math.max(0, value);
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
	 * @return the cooldowntime (milliseconds) to wait when the object
	 *         (mission/quest) has been completed
	 * 
	 *         note: this will still return a number even if the object is not
	 *         repeatable
	 */
	public long getCooldownTime() {
		return minutes * 60 * 1000;
	}

	/**
	 * 
	 * @return true if the object (mission/quest) is repeatable
	 */
	public boolean isRepeatable() {
		return repeatable;
	}
	
	public Button getCooldownEditorButton(Gui gui) {
		return new CooldownEditorButton(gui);
	}
	private class CooldownEditorButton extends AmountSelectorButton {

		public CooldownEditorButton(Gui gui) {
			super("&8Cooldown Editor", new ItemBuilder(Material.WATCH).setGuiProperty().build(),gui,
					//1min, 15 min, 2h,24h,1w,1mo,1y
					1L,15L,120L,1440,10080,43200,525600);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lCooldown Editor");
			desc.add("&6Click to edit");
			if (isRepeatable()) {
				desc.add("&eCooldown is &aEnabled");
				desc.add("&eTime &a" + StringUtils.getStringCooldown(getCooldownTime()));
			} else {
				desc.add("&eCooldown is &cDisabled");
				desc.add("&7Time &m" + StringUtils.getStringCooldown(getCooldownTime()));
			}
			return desc;
		}

		@Override
		public long getCurrentAmount() {
			return (int) getCooldownMinutes();
		}

		@Override
		public boolean onAmountChangeRequest(long i) {
			return setCooldownMinutes((int) i);
		}

	}
	
	public Button getCooldownTogglerButton(Gui gui) {
		return new CooldownTogglerButton(gui);
	}
	private class CooldownTogglerButton extends StaticFlagButton {

		public CooldownTogglerButton(Gui parent) {
			super(Utils.setDescription(
					new ItemBuilder(Material.WOOL).setDamage(15).setGuiProperty().build(),
					Arrays.asList("&6Cooldown is &cDisabled"),null,true),
					Utils.setDescription(
					new ItemBuilder(Material.WOOL).setGuiProperty().build(),
					Arrays.asList("&6Cooldown is &aEnabled"),null,true),parent);
		}

		@Override
		public boolean getCurrentValue() {
			return isRepeatable();
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			return setRepeatable(value);
		}
		
	}

}
