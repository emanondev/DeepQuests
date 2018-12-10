package emanondev.quests.interfaces;

public abstract class ATaskType<T extends User<T>> extends AType<T,Task<T>> implements TaskType<T> {

	public ATaskType(String id) {
		super(id);
	}

	@Override
	public String getDefaultUnstartedDescription(Task<T> task) {
		return null;
	}

	@Override
	public String getDefaultProgressDescription(Task<T> task) {
		return null;
	}
	

}
