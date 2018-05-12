package emanondev.quests.require.type;

import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;

public class NeedPermissionType extends AbstractRequireType implements RequireType {
	private final static String ID = "PERMISSION";
	
	public NeedPermissionType() {
		super(ID);
	}
	
	@Override
	public Require getRequireInstance(String info) {
		return new NeedPermission(info);
	}

	public class NeedPermission implements Require {
		private final String permission;
		public NeedPermission(String permission) {
			if (permission==null)
				throw new NullPointerException("invalid permission");
			if (permission.isEmpty())
				throw new IllegalArgumentException("void permission is not a valid permission");
			this.permission = permission.toLowerCase();
		}
		@Override
		public boolean isAllowed(QuestPlayer p) {
			return p.getPlayer().hasPermission(permission);
		}
		@Override
		public String toText() {
			return ID+":"+permission;
		}
		
	}

}
