package emanondev.quests.interfaces;

import java.util.Collection;

public interface UserManager<T extends User<T>> {
	
	public Collection<T> getUsers();
	public T getUser(String id);
	
	public default void saveAll() {
		for(T user:getUsers())
			user.save();
	}
	
	public QuestManager<T> getQuestManager();
	
}
