package emanondev.quests.interfaces.player.requiretypes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.interfaces.ARequire;
import emanondev.quests.interfaces.ARequireType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Require;
import emanondev.quests.interfaces.data.PermissionData;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class PermissionRequireType extends ARequireType<QuestPlayer> {

	public PermissionRequireType() {
		super(ID);
	}
	
	private static final String ID = "player_permission_require";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.TRIPWIRE_HOOK).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require a certain permission");
	}

	@Override
	public Require<QuestPlayer> getInstance(Map<String, Object> map) {
		return new PermissionRequire(map);
	}
	
	public class PermissionRequire extends ARequire<QuestPlayer> {
		
		private PermissionData permissionData = null;

		public PermissionRequire(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.REQUIRE_INFO_PERMISSIONDATA))
					permissionData = (PermissionData) map.get(Paths.REQUIRE_INFO_PERMISSIONDATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (permissionData == null)
				permissionData = new PermissionData(null);
			permissionData.setParent(this);
		}
		
		public PermissionData getPermissionData() {
			return permissionData;
		}
		
		@Override
		public PermissionRequireType getType() {
			return PermissionRequireType.this;
		}

		public boolean isAllowed(QuestPlayer p) {
			return permissionData.hasPermission(p.getPlayer());
		}

		@Override
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (permissionData.getPermission() == null)
				info.add("&9Permission &cnot setted");
			else
				info.add("&9Permission &e" + permissionData.getPermission());
			return info;
		}
	}

}