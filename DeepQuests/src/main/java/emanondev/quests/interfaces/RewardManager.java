package emanondev.quests.interfaces;

import java.util.Collection;

public interface RewardManager<T extends User<T>> extends QuestComponentTypeManager<T,Reward<T>,RewardType<T>> {

	public void registerQuestType(RewardType<T> rewardType);
	public void registerMissionType(RewardType<T> rewardType);
	public void registerTaskType(RewardType<T> rewardType);
	
	public Collection<RewardType<T>> getQuestTypes(RewardType<T> rewardType);
	public Collection<RewardType<T>> getMissionTypes(RewardType<T> rewardType);
	public Collection<RewardType<T>> getTaskTypes(RewardType<T> rewardType);
}
