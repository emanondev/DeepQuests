package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.JobTypeData;
import emanondev.quests.data.LevelData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.QCWithCooldown;

public class JobsLvRequireType extends AbstractRequireType implements RequireType {
	private final static String KEY = "JOBSLEVEL";

	public JobsLvRequireType() {
		super(KEY);
	}

	@Override
	public Require getInstance(ConfigSection section, QCWithCooldown parent) {
		return new JobsLvRequire(section,parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_PICKAXE;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has joined the selected job");
		desc.add("&7And if selected level is reached by the player");
		return desc;
	}
	
	public class JobsLvRequire extends AbstractRequire implements Require {
		private JobTypeData jobData;
		private LevelData levelData;

		public JobsLvRequire(ConfigSection section, QCWithCooldown parent) {
			super(section, parent);
			jobData = new JobTypeData(section,this);
			levelData = new LevelData(section,this);
		}

		public JobTypeData getJobTypeData() {
			return jobData;
		}
		public LevelData getLevelData() {
			return levelData;
		}
		@Override
		public boolean isAllowed(QuestPlayer p) {
			if (jobData.getJob()==null)
				return false;
			JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p.getPlayer());
			if (jobsPlayer!=null && jobsPlayer.isInJob(jobData.getJob()) && jobsPlayer.getJobProgression(jobData.getJob()).getLevel()>=levelData.getLevel())
				return true;
			return false;
		}

		@Override
		public RequireType getType() {
			return JobsLvRequireType.this;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (jobData.getJob()==null)
				info.add("&9Required Job level: &cnot setted");
			else {
				info.add("&9Required Level: &e"+levelData.getLevel());
				info.add("  &9on Job: &e"+jobData.getJob().getName());
			}
			return info;
		}
		
		@Override
		public RequireEditor createEditorGui(Player player, Gui parent) {
			RequireEditor gui = super.createEditorGui(player,parent);
			gui.putButton(16,jobData.getJobSelectorButton(gui));
			gui.putButton(17,levelData.getLevelEditorButton(gui));
			return gui;
		}
	}

}
