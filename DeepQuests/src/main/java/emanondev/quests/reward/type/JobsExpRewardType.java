package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.ExperienceData;
import emanondev.quests.data.JobTypeData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.QuestComponent;

public class JobsExpRewardType extends AbstractRewardType implements RewardType {
	private final static String KEY = "JOBSEXP";

	public JobsExpRewardType() {
		super(KEY);
	}

	@Override
	public Reward getInstance(ConfigSection section, QuestComponent parent) {
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
	
	public class JobsExpReward extends AbstractReward implements Reward {
		private JobTypeData jobData;
		private ExperienceData expData;

		public JobsExpReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			jobData = new JobTypeData(section,this);
			expData = new ExperienceData(section,this);
		}
		
		public JobTypeData getJobTypeData() {
			return jobData;
		}
		public ExperienceData getExperienceData() {
			return expData;
		}
		

		@Override
		public String getInfo() {
			if (jobData.getJob()==null)
				return "Job reward not set";
			return "Reward "+expData.getExperience()+" experience on Job "+jobData.getJob().getName();
		}

		@Override
		public void applyReward(QuestPlayer p,int amount) {
			if (jobData.getJob()==null||amount<=0||expData.getExperience()<=0)
				return;
			
			JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p.getPlayer());
			if (jobsPlayer!=null && jobsPlayer.isInJob(jobData.getJob()))
				jobsPlayer.getJobProgression(jobData.getJob()).addExperience(expData.getExperience()*amount);
		}

		@Override
		public RewardType getType() {
			return JobsExpRewardType.this;
		}

		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, expData.getExpEditorButton(gui));
			gui.putButton(10, jobData.getJobSelectorButton(gui));
			return gui;
		}
	}
}
