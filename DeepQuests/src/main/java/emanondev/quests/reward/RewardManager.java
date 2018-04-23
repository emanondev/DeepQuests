package emanondev.quests.reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import emanondev.quests.Quests;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;

public class RewardManager {
	private final static HashMap<String,Class<? extends Reward>> rewards = 
			new HashMap<String,Class<? extends Reward>>();
	private final static HashMap<String,Class<? extends MissionReward>> missionRewards =
			new HashMap<String,Class<? extends MissionReward>>();
	private final static HashMap<String,Class<? extends QuestReward>> questRewards =
			new HashMap<String,Class<? extends QuestReward>>();
			

	public void registerRewardType(RewardType type) {
		rewards.put(type.getNameID(),type.getRewardClass());
		missionRewards.put(type.getNameID(),type.getRewardClass());
		questRewards.put(type.getNameID(),type.getRewardClass());
	}
	public void registerRewardType(MissionRewardType type) {
		missionRewards.put(type.getNameID(),type.getRewardClass());
	}
	public void registerRewardType(QuestRewardType type) {
		questRewards.put(type.getNameID(),type.getRewardClass());
	}

	public List<Reward> convertRewards(List<String> list) {
		ArrayList<Reward> rews = new ArrayList<Reward>();
		if (list==null||list.isEmpty())
			return rews;
		for (String rawReward : list) {
			try {
				int index = rawReward.indexOf(":");
				String key;
				String trueReward;
				if (index == -1) {
					key = rawReward;
					trueReward = "";
				}
				else {
					key = rawReward.substring(0,index);
					trueReward = rawReward.substring(index+1);
				}
				rews.add(rewards.get(key.toUpperCase()).getConstructor(String.class)
						.newInstance(trueReward));
			} catch (Exception e) {
				Quests.getLogger("errors").log("Error while creating reward: '"+rawReward+"'");
				Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
			}
			
		}
		return rews;
	}

	public List<MissionReward> convertMissionRewards(List<String> list) {
		ArrayList<MissionReward> rews = new ArrayList<MissionReward>();
		if (list==null||list.isEmpty())
			return rews;
		for (String rawReward : list) {
			try {
				int index = rawReward.indexOf(":");
				String key;
				String trueReward;
				if (index == -1) {
					key = rawReward;
					trueReward = "";
				}
				else {
					key = rawReward.substring(0,index);
					trueReward = rawReward.substring(index+1);
				}
				rews.add(missionRewards.get(key.toUpperCase()).getConstructor(String.class)
						.newInstance(trueReward));
			} catch (Exception e) {
				Quests.getLogger("errors").log("Error while creating reward: '"+rawReward+"'");
				Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
			}
			
		}
		return rews;
	}

	public List<QuestReward> convertQuestReward(List<String> list) {
		ArrayList<QuestReward> rews = new ArrayList<QuestReward>();
		if (list==null||list.isEmpty())
			return rews;
		for (String rawReward : list) {
			try {
				int index = rawReward.indexOf(":");
				String key;
				String trueReward;
				if (index == -1) {
					key = rawReward;
					trueReward = "";
				}
				else {
					key = rawReward.substring(0,index);
					trueReward = rawReward.substring(index+1);
				}
				rews.add(questRewards.get(key.toUpperCase()).getConstructor(String.class)
						.newInstance(trueReward));
			} catch (Exception e) {
				Quests.getLogger("errors").log("Error while creating reward: '"+rawReward+"'");
				Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
			}
		}
		return rews;
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
