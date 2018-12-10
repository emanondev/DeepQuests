package emanondev.quests.interfaces.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;

import emanondev.quests.interfaces.Paths;

public class JobTypeData extends QuestComponentData {
	
	private Job job = null;
	
	public JobTypeData(Map<String,Object> map) {
		if (map==null)
			return;
		try {
			String jobName = (String) map.getOrDefault(Paths.DATA_JOB_TYPE, null);
			if (jobName!=null)
				job = Jobs.getJob(jobName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new LinkedHashMap<>();
		if (job !=null)
			map.put(Paths.DATA_JOB_TYPE,job.getName());
		return map;
	}
	
	public Job getJob() {
		return job;
	}
	
	public boolean setJob(Job job) {
		if (this.job==job)
			return false;
		if (this.job!=null && this.job.equals(job)) 
			return false;
		this.job = job;
		return true;
	}
}
