package emanondev.quests.interfaces;

public abstract class AUserManager<T extends User<T>> implements UserManager<T> {

	private final QuestManager<T> questManager;
	public AUserManager(QuestManager<T> questManager) {
		if (questManager==null)
			throw new NullPointerException();
		this.questManager = questManager;
	}

	@Override
	public QuestManager<T> getQuestManager() {
		return questManager;
	}
	

}
