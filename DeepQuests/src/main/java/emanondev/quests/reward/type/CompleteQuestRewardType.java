package emanondev.quests.reward.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.reward.MissionRewardType;
import emanondev.quests.utils.YmlLoadable;

public class CompleteQuestRewardType extends AbstractRewardType implements MissionRewardType {

	public CompleteQuestRewardType() {
		super("COMPLETEQUEST");
	}
	
	public class CompleteQuestReward extends AbstractReward implements MissionReward{
		public CompleteQuestReward(MemorySection m, Mission mission) {
			super(m,mission);
		}

		@Override
		public String getInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Mission getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MissionRewardType getType() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.BOOK;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Complete the quest that contains this Mission");
	}

	@Override
	public MissionReward getInstance(MemorySection section, YmlLoadable parent) {
		// TODO Auto-generated method stub
		return null;
	}

}

