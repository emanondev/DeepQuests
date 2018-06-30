package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobsPlayer;

import emanondev.quests.gui.button.AmountEditorButtonFactory;
import emanondev.quests.gui.button.JobEditorButtonFactory;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.YmlLoadable;

public class JobsExpRewardType extends AbstractRewardType implements RewardType {
	private final static String KEY = "JOBSEXP";

	public JobsExpRewardType() {
		super(KEY);
	}

	@Override
	public Reward getInstance(MemorySection section, YmlLoadable parent) {
		return new JobsExpReward(section,parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_PICKAXE;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7If the player has joined the selected job");
		desc.add("&7give selected amount of exp on that job");
		return desc;
	}
	private final static String PATH_JOB = "job_name";
	private final static String PATH_EXPERIENCE = "experience_reward";
	
	
	public class JobsExpReward extends AbstractReward implements Reward {
		private Job job;
		private int exp;

		public JobsExpReward(MemorySection section, YmlLoadable parent) {
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
			exp = getSection().getInt(PATH_EXPERIENCE,1);
			this.addToEditor(17,new ExpEditor());
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
		public String getInfo() {
			if (job==null)
				return "Job reward not set";
			return "Reward "+exp+" experience on Job "+job.getName();
		}

		@Override
		public void applyReward(QuestPlayer p,int amount) {
			if (job==null||amount<=0||exp<=0)
				return;
			
			JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p.getPlayer());
			if (jobsPlayer!=null && jobsPlayer.isInJob(job))
				jobsPlayer.getJobProgression(job).addExperience(exp*amount);
		}

		@Override
		public RewardType getType() {
			return JobsExpRewardType.this;
		}

		public int getExperience() {
			return exp;
		}
		public boolean setExperience(int exp) {
			if (exp < 0)
				return false;
			if (exp == this.exp)
				return false;
			this.exp = exp;
			getSection().set(PATH_EXPERIENCE,exp);
			getParent().setDirty(true);
			return true;
		}

		private class ExpEditor extends AmountEditorButtonFactory {
			public ExpEditor() {
				super("Exp Amount Editor", Material.DIODE);
			}
			@Override
			protected boolean onChange(int amount) {
				return setExperience(amount);
			}
			@Override
			protected int getAmount() {
				return exp;
			}
			@Override
			protected ArrayList<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lExperience Reward Editor");
				desc.add("&6Click to edit");
				desc.add("&7Experience as reward is &e"+exp);
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
