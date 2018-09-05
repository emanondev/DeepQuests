package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.button.AmountEditorButtonFactory;
import emanondev.quests.gui.button.JobEditorButtonFactory;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public class JobsLvRequireType extends AbstractRequireType implements RequireType {
	private final static String KEY = "JOBSLEVEL";

	public JobsLvRequireType() {
		super(KEY);
	}

	@Override
	public Require getInstance(ConfigSection section, YmlLoadableWithCooldown parent) {
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

	private final static String PATH_JOB = "job_name";
	private final static String PATH_LEVEL = "level";
	
	public class JobsLvRequire extends AbstractRequire implements Require {
		private Job job;
		private int level;

		public JobsLvRequire(ConfigSection section, YmlLoadableWithCooldown parent) {
			super(section, parent);
			try {
				String jobName = getSection().getString(PATH_JOB,null);
				if (jobName!=null)
					job = Jobs.getJob(jobName);
				else
					job = null;
			} catch (Exception e) {
				job = null;
			}
			level = getSection().getInt(PATH_LEVEL,1);
			this.addToEditor(17,new LevelEditor());
			this.addToEditor(16,new JobEditor());
		}

		public Job getJob() {
			return job;
		}
		public boolean setJob(Job job) {
			if (job == null && this.job== null)
				return false;
			if (this.job != null && this.job.equals(job))
				return false;
			this.job = job;
			if (job==null)
				getSection().set(PATH_JOB,null);
			else
				getSection().set(PATH_JOB,job.getName());
			getParent().setDirty(true);
			return true;
		}
		
		@Override
		public boolean isAllowed(QuestPlayer p) {
			if (job==null)
				return false;
			JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p.getPlayer());
			if (jobsPlayer!=null && jobsPlayer.isInJob(job) && jobsPlayer.getJobProgression(job).getLevel()>=level)
				return true;
			return false;
		}

		@Override
		public RequireType getType() {
			return JobsLvRequireType.this;
		}

		@Override
		public String getInfo() {
			if (job == null)
				return "Require Job not set";
			return "Require Job "+job.getName()+" lv "+level;
		}

		public int getLevel() {
			return level;
		}
		public boolean setLevel(int level) {
			if (level < 0)
				return false;
			if (level == this.level)
				return false;
			this.level = level;
			getSection().set(PATH_LEVEL,level);
			getParent().setDirty(true);
			return true;
		}
		private class LevelEditor extends AmountEditorButtonFactory {
			public LevelEditor() {
				super("Level Editor", Material.DIODE);
			}
			@Override
			protected boolean onChange(int amount) {
				return setLevel(amount);
			}
			@Override
			protected int getAmount() {
				return level;
			}
			@Override
			protected ArrayList<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lLevel Editor");
				desc.add("&6Click to edit");
				desc.add("&7Lv required is &e"+level);
				return desc;
			}
		}
		private class JobEditor extends JobEditorButtonFactory {
			public JobEditor() {
				super();
			}
			@Override
			protected boolean onSelection(Job job) {
				return setJob(job);
			}

			@Override
			protected Job getObject() {
				return job;
			}
		}
	}

}
