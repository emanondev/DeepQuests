package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.IntegerPositiveAmountData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.QCWithCooldown;

public class QuestPointRequireType extends AbstractRequireType implements RequireType {
	private final static String ID = "QUEST_POINT_REQUIRE";

	public QuestPointRequireType() {
		super(ID);
	}

	@Override
	public Require getInstance(ConfigSection section, QCWithCooldown mission) {
		return new QuestPointRequire(section, mission);
	}

	public class QuestPointRequire extends AbstractRequire {
		private final IntegerPositiveAmountData quantity;

		public QuestPointRequire(ConfigSection section, QCWithCooldown parent) {
			super(section, parent);
			this.quantity = new IntegerPositiveAmountData(getSection(), this);
		}

		@Override
		public boolean isAllowed(QuestPlayer p) {
			return p.getQuestPoints()>=quantity.getAmount();
		}

		@Override
		public RequireType getType() {
			return QuestPointRequireType.this;
		}

		
		@Override
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Required Quest Points: &e"+quantity.getAmount());
			return info;
		}

		public RequireEditor createEditorGui(Player p,Gui parent) {
			RequireEditor gui = super.createEditorGui(p, parent);
			gui.putButton(0, quantity.getAmountEditorButton(gui));
			return gui;
		}
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.DIAMOND_BLOCK;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has at least");
		desc.add("&7selected amount of Quest Points");
		return desc;
	}
}