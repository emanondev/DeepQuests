package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.SoundData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.QuestComponent;

public class SoundRewardType extends AbstractRewardType {
	private final static String KEY = "PLAYSOUND";

	public SoundRewardType() {
		super(KEY);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.NOTE_BLOCK;
	}

	@Override
	public Reward getInstance(ConfigSection section, QuestComponent parent) {
		return new SoundReward(section, parent);
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Play sound for Player");
		return desc;
	}

	public class SoundReward extends AbstractReward {
		private final SoundData soundData;

		public SoundReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			this.soundData = new SoundData(getSection(), this);
		}

		@Override
		public RewardType getType() {
			return SoundRewardType.this;
		}

		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			if (soundData.getSound() != null)
				qPlayer.getPlayer().playSound(qPlayer.getPlayer().getLocation(), soundData.getSound(),
						soundData.getVolume(), soundData.getPitch());
		}

		
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (soundData.getSound() == null)
				info.add("&9Sound: &cnot setted");
			else {
				info.add("&9Sound: &e" + soundData.getSound());
				info.add("  &9Volume: &e" + soundData.getVolume());
				info.add("  &9Pitch: &e" + soundData.getPitch());
			}
			return info;
		}
		

		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(0, soundData.getSoundEditorButton(gui));
			return gui;
		}

	}
}
