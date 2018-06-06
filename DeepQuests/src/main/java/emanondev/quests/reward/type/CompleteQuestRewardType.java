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
import emanondev.quests.reward.RewardType;

public class CompleteQuestRewardType extends AbstractRewardType implements MissionRewardType {

	public CompleteQuestRewardType() {
		super("COMPLETEQUEST");
	}
	
	public class CompleteQuestReward extends AbstractReward implements MissionReward{
		public CompleteQuestReward(MemorySection m, Mission mission) {
			super(m,mission);
		}

		@Override
		public void applyReward(QuestPlayer p, Mission q) {
			//TODO
			
		}

		@Override
		public RewardType getRewardType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getKey() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getInfo() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public MissionReward getRewardInstance(MemorySection m, Mission mission) {
		return new CompleteQuestReward(m,mission);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.BOOK;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Complete the quest that contains this Mission");
	}

}

