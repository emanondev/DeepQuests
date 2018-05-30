package emanondev.quests.reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.WithGui;

public class RewardManager {
	private final static HashMap<String,RewardType> rewardsType = 
			new HashMap<String,RewardType>();
	private final static HashMap<String,MissionRewardType> missionRewardsType =
			new HashMap<String,MissionRewardType>();
	private final static HashMap<String,QuestRewardType> questRewardsType =
			new HashMap<String,QuestRewardType>();
			

	public void registerRewardType(RewardType type) {
		rewardsType.put(type.getKey(),type);
		missionRewardsType.put(type.getKey(),type);
		questRewardsType.put(type.getKey(),type);
	}
	public void registerRewardType(MissionRewardType type) {
		missionRewardsType.put(type.getKey(),type);
	}
	public void registerRewardType(QuestRewardType type) {
		questRewardsType.put(type.getKey(),type);
	}

	/*
	 * something: <-
	 * 		id:
	 * 			type:
	 * 			additional info
	 */
	public List<MissionReward> loadRewards(Mission m,MemorySection section){
		ArrayList<MissionReward> rewards = new ArrayList<MissionReward>();
		if (section!=null) {
			Set<String> keys = section.getKeys(false);
			if (keys!=null)
				keys.forEach((id)->{
					try {
						String key = section.getString(id+".type");
						if (key== null || !missionRewardsType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						MissionReward rew = missionRewardsType.get(key.toUpperCase()).getRewardInstance((MemorySection) section.get(id), m);
						if (rew!=null)
							rewards.add(rew);
					} catch (Exception e) {
						
					}
				});
		}
		return rewards;
	}
	public List<QuestReward> loadRewards(Quest q,MemorySection section){
		ArrayList<QuestReward> rewards = new ArrayList<QuestReward>();
		if (section!=null) {
			Set<String> keys = section.getKeys(false);
			if (keys!=null)
				keys.forEach((id)->{
					try {
						String key = section.getString(id+".type");
						if (key== null || !questRewardsType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						QuestReward rew = questRewardsType.get(key.toUpperCase()).getRewardInstance((MemorySection) section.get(id), q);
						if (rew!=null)
							rewards.add(rew);
					} catch (Exception e) {
						
					}
				});
		}
		return rewards;
	}
	public List<Reward> loadRewards(WithGui gui,MemorySection section){
		ArrayList<Reward> rewards  = new ArrayList<Reward>();
		if (section!=null) {
			Set<String> keys = section.getKeys(false);
			if (keys!=null)
				keys.forEach((id)->{
					try {
						String key = section.getString(id+".type");
						if (key== null || !rewardsType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Reward rew = rewardsType.get(key.toUpperCase()).getRewardInstance((MemorySection) section.get(id), gui);
						if (rew!=null)
							rewards.add(rew);
					} catch (Exception e) {
						
					}
				});
		}
		return rewards;
	}
	public void giveRewards(QuestPlayer p, List<Reward> list) {
		if (list==null || list.isEmpty())
			return;
		for (Reward reward : list)
			reward.applyReward(p);
	}
	public void giveRewards(QuestPlayer p, List<MissionReward> list,Mission m) {
		if (list==null || list.isEmpty())
			return;
		for (MissionReward reward : list)
			reward.applyReward(p,m);
	}
	public void giveRewards(QuestPlayer p, List<QuestReward> list,Quest q) {
		if (list==null || list.isEmpty())
			return;
		for (QuestReward reward : list)
			reward.applyReward(p,q);
	}

}
