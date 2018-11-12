package emanondev.quests.interfaces;

import java.util.Collection;

public interface RequireManager<T extends User<T>> extends QuestComponentTypeManager<T,Require<T>,RequireType<T>> {
	
	public default void registerType(RequireType<T> requireType) {
		registerQuestType(requireType);
		registerMissionType(requireType);
	}
	public void registerQuestType(RequireType<T> requireType);
	public void registerMissionType(RequireType<T> requireType);
	public void registerTaskType(RequireType<T> requireType);
	public Collection<RequireType<T>> getQuestTypes(RequireType<T> requireType);
	public Collection<RequireType<T>> getMissionTypes(RequireType<T> requireType);
	public Collection<RequireType<T>> getTaskTypes(RequireType<T> requireType);

}
