package emanondev.quests.reward;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.QuestComponent;

public class RewardManager {
	private final static HashMap<String, RewardType> rewardsType = new HashMap<String, RewardType>();
	private final static HashMap<String, RewardType> missionRewardsType = new HashMap<String, RewardType>();
	private final static HashMap<String, RewardType> questRewardsType = new HashMap<String, RewardType>();

	public void registerRewardType(RewardType type) {
		rewardsType.put(type.getKey(), type);
		missionRewardsType.put(type.getKey(), type);
		questRewardsType.put(type.getKey(), type);
	}

	public void registerMissionRewardType(MissionRewardType type) {
		missionRewardsType.put(type.getKey(), type);
	}

	public void registerQuestRewardType(QuestRewardType type) {
		questRewardsType.put(type.getKey(), type);
	}

	public LinkedHashMap<String, Reward> loadRewards(Mission m, ConfigSection section) {
		LinkedHashMap<String, Reward> rewards = new LinkedHashMap<String, Reward>();
		if (section != null) {
			Set<String> keys = section.getKeys(false);
			if (keys != null)
				keys.forEach((id) -> {
					try {
						String key = section.getString(id + ".type");
						if (key == null || !missionRewardsType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Reward rew = missionRewardsType.get(key.toUpperCase())
								.getInstance(section.loadSection(id), m);
						if (rew != null)
							rewards.put(rew.getID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return rewards;
	}

	public LinkedHashMap<String, Reward> loadRewards(Quest q, ConfigSection section) {
		LinkedHashMap<String, Reward> rewards = new LinkedHashMap<String, Reward>();
		if (section != null) {
			Set<String> keys = section.getKeys(false);
			if (keys != null)
				keys.forEach((id) -> {
					try {
						String key = section.getString(id + ".type");
						if (key == null || !questRewardsType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Reward rew = questRewardsType.get(key.toUpperCase())
								.getInstance(section.loadSection(id), q);
						if (rew != null)
							rewards.put(rew.getID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return rewards;
	}

	public LinkedHashMap<String, Reward> loadRewards(QuestComponent gui, ConfigSection section) {
		LinkedHashMap<String, Reward> rewards = new LinkedHashMap<String, Reward>();
		if (section != null) {
			Set<String> keys = section.getKeys(false);
			if (keys != null)
				keys.forEach((id) -> {
					try {
						String key = section.getString(id + ".type");
						if (key == null || !rewardsType.containsKey(key.toUpperCase()))
							throw new NullPointerException();
						Reward rew = rewardsType.get(key.toUpperCase()).getInstance(section.loadSection(id),
								gui);
						if (rew != null)
							rewards.put(rew.getID(), rew);
					} catch (Exception e) {

					}
				});
		}
		return rewards;
	}

	public void giveRewards(QuestPlayer p, List<Reward> list, int amount) {
		if (list == null || list.isEmpty())
			return;
		for (Reward reward : list)
			reward.applyReward(p, amount);
	}

	public void giveRewards(QuestPlayer p, List<Reward> list) {
		giveRewards(p, list, 1);
	}

	public Collection<RewardType> getMissionRewardsTypes() {
		return Collections.unmodifiableCollection(missionRewardsType.values());
	}

}
