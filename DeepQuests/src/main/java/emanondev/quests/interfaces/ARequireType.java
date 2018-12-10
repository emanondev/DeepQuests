package emanondev.quests.interfaces;

public abstract class ARequireType<T extends User<T>> extends AType<T,Require<T>> implements RequireType<T> {

	public ARequireType(String id) {
		super(id);
	}
}
