package emanondev.quests.require.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.TargetMissionData;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.NoLoopQC;
import emanondev.quests.utils.QCWithCooldown;

public class NeedCompletedMissionRequireType extends AbstractRequireType implements RequireType {

	public NeedCompletedMissionRequireType() {
		super("NEEDCOMPLETEDMISSION");
	}

	public class NeedCompletedMissionRequire extends AbstractRequire implements Require,NoLoopQC {

		private TargetMissionData missionData;
		
		public NeedCompletedMissionRequire(ConfigSection section, QCWithCooldown parent) {
			super(section, parent);
			if (!((parent instanceof Quest) || (parent instanceof Mission)))
				throw new IllegalArgumentException();
			missionData = new TargetMissionData(getSection(),this);
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			Mission m = missionData.getTargetMission();
			if (m == null)
				info.add("&9Required Mission: &cnot setted");
			else {
				info.add("&9Required Mission: &e" + m.getDisplayName());
				info.add("  &9of Quest: &e"+m.getParent().getDisplayName());
			}
			return info;
		}

		@Override
		public RequireType getType() {
			return NeedCompletedMissionRequireType.this;
		}

		
		@Override
		public boolean isAllowed(QuestPlayer qPlayer) {
			try {
				Mission m = missionData.getTargetMission();
				if (m==null)
					return false;
				switch (qPlayer.getDisplayState(m)) {
				case COMPLETED:
				case COOLDOWN:
					return true;
				default:
					break;
				}
				return false;
				
			}catch (Exception e) {
				return false;
			}
		}
		
		public boolean isLoopSafe(Mission mission) {
			if (mission==null)
				return true;
			if (mission.equals(getParent()))
				return false;
			
			for (Require req : mission.getRequires()) {
				if (!(req instanceof NeedCompletedMissionRequire))
					continue;
				NeedCompletedMissionRequire require = (NeedCompletedMissionRequire) req;
				
				Mission targetMission = require.missionData.getTargetMission();
				if (getParent().equals(targetMission))
					return false;
				if (!isLoopSafe(targetMission))
					return false;
			}
			return true;
		}
		
		public RequireEditor createEditorGui(Player p,Gui parent) {
			RequireEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, missionData.getMissionSelectorButton(gui));
			return gui;
		}
	}

	@Override
	public Require getInstance(ConfigSection m, QCWithCooldown qc) {
		return new NeedCompletedMissionRequire(m,qc);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.ARROW;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require to complete mission", "&7of selected quest");
	}

}