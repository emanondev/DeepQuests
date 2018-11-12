package emanondev.quests.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ARewardManager<T extends User<T>> implements RewardManager<T>{

	private Map<String,RewardType<T>> missionTypes = new HashMap<>();
	private Map<String,RewardType<T>> questTypes = new HashMap<>();
	private Map<String,RewardType<T>> taskTypes = new HashMap<>();
	private Map<String,RewardType<T>> types = new HashMap<>();

	@Override
	public RewardType<T> getType(String id) {
		return types.get(id);
	}

	@Override
	public Collection<RewardType<T>> getTypes() {
		return types.values();
	}

	@Override
	public void registerQuestType(RewardType<T> rewardType) {
		types.put(rewardType.getID(),rewardType);
		questTypes.put(rewardType.getID(),rewardType);
	}

	@Override
	public void registerMissionType(RewardType<T> rewardType) {
		types.put(rewardType.getID(),rewardType);
		missionTypes.put(rewardType.getID(),rewardType);
		
	}

	@Override
	public void registerTaskType(RewardType<T> rewardType) {
		types.put(rewardType.getID(),rewardType);
		taskTypes.put(rewardType.getID(),rewardType);
		
	}
	

	@Override
	public void registerType(RewardType<T> rewardType) {
		types.put(rewardType.getID(),rewardType);
		questTypes.put(rewardType.getID(),rewardType);
		missionTypes.put(rewardType.getID(),rewardType);
		taskTypes.put(rewardType.getID(),rewardType);
	}

	@Override
	public Collection<RewardType<T>> getQuestTypes(RewardType<T> rewardType) {
		return Collections.unmodifiableCollection(questTypes.values());
	}

	@Override
	public Collection<RewardType<T>> getMissionTypes(RewardType<T> rewardType) {
		return Collections.unmodifiableCollection(missionTypes.values());
	}

	@Override
	public Collection<RewardType<T>> getTaskTypes(RewardType<T> rewardType) {
		return Collections.unmodifiableCollection(taskTypes.values());
	}

}
