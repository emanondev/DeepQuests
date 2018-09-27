package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;

public class JobTypeData extends QCData {
	private final static String PATH_JOB = "job_name";
	private Job job;
	public JobTypeData(ConfigSection m, QuestComponent parent) {
		super(m,parent);
		
		try {
			String jobName = getSection().getString(PATH_JOB,null);
			if (jobName!=null)
				job = Jobs.getJob(jobName);
			else
				job = null;
		} catch (Exception e) {
			e.printStackTrace();
			job = null;
		}
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
	public Button getJobSelectorButton(Gui parent) {
		return new JobsSelectorButton(parent);
	}
	
	private class JobsSelectorButton extends SelectOneElementButton<Job> {

		public JobsSelectorButton(Gui parent) {
			super("&9Job Editor", new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build(), parent, Jobs.getJobs(), true, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lJob editor button");
			desc.add("&6click to edit selected job");
			if (job == null)
				desc.add("&cNo job has been selected yet");
			else
				desc.add("&7Current selected job is &a"+job.getName());
			return desc;
		}

		@Override
		public List<String> getElementDescription(Job job) {
			List<String> desc = new ArrayList<String>();
			desc.add("&6Click to select Job "+job.getName());
			return desc;
		}

		@Override
		public ItemStack getElementItem(Job job) {
			if (job==null)
				return new ItemBuilder(Material.BARRIER).setGuiProperty().build();
			return job.getGuiItem();
		}

		@Override
		public void onElementSelectRequest(Job job) {
			setJob(job);
		}
		
	}

}
