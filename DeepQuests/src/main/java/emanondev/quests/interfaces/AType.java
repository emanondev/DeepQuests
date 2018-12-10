package emanondev.quests.interfaces;

public abstract class AType<T extends User<T>,E extends QuestComponent<T>> implements QuestComponentType<T,E> {
	
	private final String ID;
	
	public AType(String id) {
		if(id==null || id.isEmpty() || !Paths.ALPHANUMERIC.matcher(id).matches())
			throw new IllegalArgumentException("InvalidId");
		ID = id;
	}
	
	@Override
	public String getID() {
		return ID;
	}
}
