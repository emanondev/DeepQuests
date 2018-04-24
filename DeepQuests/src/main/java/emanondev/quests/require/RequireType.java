package emanondev.quests.require;

public interface RequireType extends MissionRequireType,QuestRequireType {
	public String getNameID();
	public Require getRequireInstance(String info);
}
