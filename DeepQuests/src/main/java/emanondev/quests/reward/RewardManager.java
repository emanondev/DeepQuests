package emanondev.quests.reward;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.MemorySection;

import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.YmlLoadable;

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
	public void registerMissionRewardType(MissionRewardType type) {
		missionRewardsType.put(type.getKey(),type);
	}
	public void registerQuestRewardType(QuestRewardType type) {
		questRewardsType.put(type.getKey(),type);
	}

	/*
	 * something: <-
	 * 		id:
	 * 			type:
	 * 			additional info
	 */
	public LinkedHashMap<String,MissionReward> loadRewards(Mission m,MemorySection section){
		LinkedHashMap<String,MissionReward> rewards = new LinkedHashMap<String,MissionReward>();
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
							rewards.put(rew.getNameID(),rew);
					} catch (Exception e) {
						
					}
				});
		}
		return rewards;
	}
	public LinkedHashMap<String,QuestReward> loadRewards(Quest q,MemorySection section){
		LinkedHashMap<String,QuestReward> rewards = new LinkedHashMap<String,QuestReward>();
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
							rewards.put(rew.getNameID(),rew);
					} catch (Exception e) {
						
					}
				});
		}
		return rewards;
	}
	public LinkedHashMap<String,Reward> loadRewards(YmlLoadable gui,MemorySection section){
		LinkedHashMap<String,Reward> rewards  = new LinkedHashMap<String,Reward>();
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
							rewards.put(rew.getNameID(),rew);
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
	public Collection<MissionRewardType> getMissionRewardsTypes() {
		return Collections.unmodifiableCollection(missionRewardsType.values());
	}

}
