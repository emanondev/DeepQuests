package emanondev.quests.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ARequireManager<T extends User<T>> implements RequireManager<T>{

	private Map<String,RequireType<T>> missionTypes = new HashMap<>();
	private Map<String,RequireType<T>> questTypes = new HashMap<>();
	private Map<String,RequireType<T>> taskTypes = new HashMap<>();
	private Map<String,RequireType<T>> types = new HashMap<>();

	@Override
	public RequireType<T> getType(String id) {
		return types.get(id);
	}

	@Override
	public Collection<RequireType<T>> getTypes() {
		return types.values();
	}

	@Override
	public void registerQuestType(RequireType<T> requireType) {
		types.put(requireType.getID(),requireType);
		questTypes.put(requireType.getID(),requireType);
	}

	@Override
	public void registerMissionType(RequireType<T> requireType) {
		types.put(requireType.getID(),requireType);
		missionTypes.put(requireType.getID(),requireType);
		
	}

	@Override
	public void registerTaskType(RequireType<T> requireType) {
		types.put(requireType.getID(),requireType);
		taskTypes.put(requireType.getID(),requireType);
		
	}
	

	@Override
	public void registerType(RequireType<T> requireType) {
		types.put(requireType.getID(),requireType);
		questTypes.put(requireType.getID(),requireType);
		missionTypes.put(requireType.getID(),requireType);
		taskTypes.put(requireType.getID(),requireType);
	}

	@Override
	public Collection<RequireType<T>> getQuestTypes(RequireType<T> requireType) {
		return Collections.unmodifiableCollection(questTypes.values());
	}

	@Override
	public Collection<RequireType<T>> getMissionTypes(RequireType<T> requireType) {
		return Collections.unmodifiableCollection(missionTypes.values());
	}

	@Override
	public Collection<RequireType<T>> getTaskTypes(RequireType<T> requireType) {
		return Collections.unmodifiableCollection(taskTypes.values());
	}

}