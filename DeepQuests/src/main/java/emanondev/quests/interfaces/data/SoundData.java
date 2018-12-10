package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Sound;

import emanondev.quests.interfaces.Paths;

public class SoundData extends QuestComponentData {

	private Sound sound = null;
	private float volume = 1;
	private float pitch = 1;
	
	public SoundData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			volume = Math.min(Math.max( (float)
					map.getOrDefault(Paths.DATA_SOUND_VOLUME, 1F), 0.05F),1F);
			pitch = Math.min(Math.max( (float)
					map.getOrDefault(Paths.DATA_SOUND_PITCH, 1F), 0.05F),20F);
			String soundName = (String) map.getOrDefault(Paths.DATA_SOUND_NAME, null);
			if (soundName!=null)
				sound = Sound.valueOf(soundName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		map.put(Paths.DATA_SOUND_VOLUME, volume);
		map.put(Paths.DATA_SOUND_PITCH, pitch);
		if (sound!=null)
			map.put(Paths.DATA_SOUND_NAME, sound.toString());
		return map;
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
		return true;
	}
}