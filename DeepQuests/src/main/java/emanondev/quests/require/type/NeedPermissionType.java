package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.PermissionData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.QCWithCooldown;

public class NeedPermissionType extends AbstractRequireType implements RequireType {
	private final static String ID = "PERMISSION";

	public NeedPermissionType() {
		super(ID);
	}

	@Override
	public Require getInstance(ConfigSection section, QCWithCooldown mission) {
		return new NeedPermission(section, mission);
	}

	public class NeedPermission extends AbstractRequire implements Require {
		private PermissionData perm;

		public NeedPermission(ConfigSection section, QCWithCooldown parent) {
			super(section, parent);
			this.perm = new PermissionData(section, this);
		}

		@Override
		public boolean isAllowed(QuestPlayer p) {
			if (perm.getPermission() == null || perm.getPermission().isEmpty())
				return true;
			return p.getPlayer().hasPermission(perm.getPermission());
		}

		@Override
		public RequireType getType() {
			return NeedPermissionType.this;
		}

		
		@Override
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (perm.getPermission() == null)
				info.add("&9Permission &cnot setted");
			else
				info.add("&9Permission &e" + perm.getPermission());
			return info;
		}
		public PermissionData getPermissionData() {
			return perm;
		}

		public RequireEditor createEditorGui(Player p,Gui parent) {
			RequireEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, perm.getPermissionEditorButton(gui));
			return gui;
		}
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.TRIPWIRE_HOOK;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has the selected permission");
		return desc;
	}
}
