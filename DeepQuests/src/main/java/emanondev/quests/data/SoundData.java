package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.AButton;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.DoubleAmountEditorButton;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;
import emanondev.quests.utils.Utils;

public class SoundData extends QCData {
	private final static String PATH_SOUND = "sound.type";
	private final static String PATH_VOLUME = "sound.volume";
	private final static String PATH_PITCH = "sound.pitch";
	
	private Sound sound;
	private float volume;
	private float pitch;

	public SoundData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		try {
			this.sound = Sound.valueOf(getSection().getString(PATH_SOUND,null).toUpperCase());
		}catch (Exception e) {
			this.sound = null;
		}
		this.volume = (float) getSection().getDouble(PATH_VOLUME,1);
		this.pitch = (float) getSection().getDouble(PATH_PITCH,1);
	}
	
	public Sound getSound(){
		return sound;
	}
	public boolean setSound(Sound sound){
		if (this.sound==null && sound == null)
			return false;
		if (this.sound != null && this.sound.equals(sound))
			return false;
		this.sound = sound;
		if (sound == null)
			getSection().set(PATH_SOUND,sound);
		else
			getSection().set(PATH_SOUND,sound.toString());
		setDirty(true);
		return true;
	}
	public float getVolume() {
		return volume;
	}
	public boolean setVolume(float volume){
		volume = Math.min(Math.max(volume, 0.05F),1F);
		if (this.volume == volume)
			return false;
		this.volume = volume;
		getSection().set(PATH_VOLUME,volume);
		setDirty(true);
		return true;
	}
	public float getPitch(){
		return pitch;
	}
	public boolean setPitch(float pitch){
		pitch = Math.min(Math.max(pitch, 0.05F),20F);
		if (this.pitch == pitch)
			return false;
		this.pitch = pitch;
		getSection().set(PATH_PITCH,pitch);
		setDirty(true);
		return true;
	}
	
	public Button getSoundEditorButton(Gui gui) {
		return new SoundEditorButton(gui);
	}
	
	private class SoundEditorButton extends AButton {
		private ItemStack item;

		public SoundEditorButton(Gui parent) {
			super(parent);
			item = new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build();
			update();
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public boolean update() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSound Editor Button");
			desc.add("&6Click to edit");
			if (sound==null)
				desc.add("&7No Sound is set");
			else {
				desc.add("");
				desc.add("&6Sound: &e"+sound);
				desc.add("&6Volume: &e"+volume);
				desc.add("&6Pitch: &e"+pitch);
			}
			Utils.updateDescription(item,desc,null,true);
			return true;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new SoundGui(clicker,getParent()).getInventory());
		}
		private class SoundGui extends MapGui {

			public SoundGui(Player p, Gui previusHolder) {
				super("&8Sound Editor", 6, p, previusHolder);
				this.putButton(53,new BackButton(this));
				this.putButton(10,new SoundSelector(this));
				this.putButton(13,new VolumeEditor(this));
				this.putButton(16,new PitchEditor(this));
			}
			private class PitchEditor extends DoubleAmountEditorButton {

				public PitchEditor(Gui parent) {
					super("&9Pitch Editor", new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build(), parent, 0.01D, 0.1D, 1D);
				}

				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lPitch Editor Button");
					desc.add("&6Click to edit");
					desc.add("&6Pitch: &e"+pitch);
					return desc;
				}

				@Override
				public double getCurrentAmount() {
					return pitch;
				}

				@Override
				public boolean onAmountChangeRequest(double i) {
					return setPitch((float) i);
				}
			}
			
			private class VolumeEditor extends DoubleAmountEditorButton {

				public VolumeEditor(Gui parent) {
					super("&9Volume Editor", new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build(), parent, 0.01D, 0.05D, 0.25D);
				}

				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lVolume Editor Button");
					desc.add("&6Click to edit");
					desc.add("&6Volume: &e"+volume);
					return desc;
				}

				@Override
				public double getCurrentAmount() {
					return volume;
				}

				@Override
				public boolean onAmountChangeRequest(double i) {
					return setVolume((float) i);
				}
			}
			
			private class SoundSelector extends SelectOneElementButton<Sound> {

				public SoundSelector(Gui parent) {
					super("&8Sound Selector", new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build(), parent, SOUNDS, true, true, false);
				}

				@Override
				public List<String> getButtonDescription() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lSound Selector Button");
					desc.add("&6Click to edit");
					if (sound!=null)
						desc.add("&6Sound: &e"+sound.toString());
					else
						desc.add("&8No Sound is set");
					return desc;
				}

				@Override
				public List<String> getElementDescription(Sound element) {
					return Arrays.asList("&6Sound "+element.toString());
				}

				@Override
				public ItemStack getElementItem(Sound element) {
					try {
						switch(element) {
						case AMBIENT_CAVE:
							return new ItemBuilder(Material.COBWEB).setGuiProperty().build();
						case BLOCK_ANVIL_BREAK:
						case BLOCK_ANVIL_DESTROY:
						case BLOCK_ANVIL_FALL:
						case BLOCK_ANVIL_HIT:
						case BLOCK_ANVIL_LAND:
						case BLOCK_ANVIL_PLACE:
						case BLOCK_ANVIL_STEP:
						case BLOCK_ANVIL_USE:
							return new ItemBuilder(Material.ANVIL).setGuiProperty().build();
						case BLOCK_BREWING_STAND_BREW:
							return new ItemBuilder(Material.BREWING_STAND).setGuiProperty().build();
						case BLOCK_CHEST_CLOSE:
						case BLOCK_CHEST_LOCKED:
						case BLOCK_CHEST_OPEN:
							return new ItemBuilder(Material.CHEST).setGuiProperty().build();
						case BLOCK_CHORUS_FLOWER_DEATH:
						case BLOCK_CHORUS_FLOWER_GROW:
							return new ItemBuilder(Material.CHORUS_FLOWER).setGuiProperty().build();
						case ENTITY_ELDER_GUARDIAN_AMBIENT:
						case ENTITY_ELDER_GUARDIAN_AMBIENT_LAND:
						case ENTITY_ELDER_GUARDIAN_CURSE:
						case ENTITY_ELDER_GUARDIAN_DEATH:
						case ENTITY_ELDER_GUARDIAN_DEATH_LAND:
						case ENTITY_ELDER_GUARDIAN_FLOP:
						case ENTITY_ELDER_GUARDIAN_HURT:
						case ENTITY_ELDER_GUARDIAN_HURT_LAND:
							return new ItemBuilder(Material.PRISMARINE_SHARD).setGuiProperty().build();
						
						default:
							break;
						
						}
					}catch (Exception e) {}
					return new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build();
				}

				@Override
				public void onElementSelectRequest(Sound element) {
					if (setSound(element))
						getTargetPlayer().openInventory(getParent().getInventory());
					
				}
			}
		}
	}
	
	private static Collection<Sound> SOUNDS = getSounds();
	
	private static Collection<Sound> getSounds(){
		ArrayList<Sound> sounds = new ArrayList<Sound>();
		for (Sound sound:Sound.values())
			sounds.add(sound);
		return sounds;
	}

}
