package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.IntegerPositiveAmountData;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.require.MissionRequireType;
import emanondev.quests.utils.QCWithCooldown;

public class MissionPointRequireType extends AbstractRequireType implements MissionRequireType {
	private final static String ID = "MISSION_POINT_REQUIRE";

	public MissionPointRequireType() {
		super(ID);
	}

	@Override
	public MissionRequire getInstance(ConfigSection section, QCWithCooldown mission) {
		return new MissionPointRequire(section, mission);
	}

	public class MissionPointRequire extends AbstractRequire implements MissionRequire {
		private final IntegerPositiveAmountData quantity;

		public MissionPointRequire(ConfigSection section, QCWithCooldown parent) {
			super(section, parent);
			this.quantity = new IntegerPositiveAmountData(getSection(), this);
		}

		@Override
		public boolean isAllowed(QuestPlayer p) {
			return p.getMissionPoints(getParent().getParent())>=quantity.getAmount();
		}

		@Override
		public MissionRequireType getType() {
			return MissionPointRequireType.this;
		}

		
		@Override
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Required Mission Points: &e"+quantity.getAmount());
			return info;
		}

		public RequireEditor createEditorGui(Player p,Gui parent) {
			RequireEditor gui = super.createEditorGui(p, parent);
			gui.putButton(0, quantity.getAmountEditorButton(gui));
			return gui;
		}

		@Override
		public Mission getParent() {
			return (Mission) super.getParent();
		}
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.DIAMOND;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has at least");
		desc.add("&7selected amount of Mission Points");
		return desc;
	}
}