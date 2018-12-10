package emanondev.quests.interfaces.player.requiretypes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobsPlayer;

import emanondev.quests.interfaces.ARequire;
import emanondev.quests.interfaces.ARequireType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Require;
import emanondev.quests.interfaces.RequireType;
import emanondev.quests.interfaces.data.JobTypeData;
import emanondev.quests.interfaces.data.LevelData;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class JobLevelRequireType extends ARequireType<QuestPlayer> {

	public JobLevelRequireType() {
		super(ID);
	}
	
	private static final String ID = "player_job_level_require";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.IRON_PICKAXE).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require a certain lv on a job");
	}

	@Override
	public Require<QuestPlayer> getInstance(Map<String, Object> map) {
		return new JobLevelRequire(map);
	}
	
	public class JobLevelRequire extends ARequire<QuestPlayer> {
		
		private JobTypeData jobData = null;
		private LevelData levelData = null;

		public JobLevelRequire(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.REQUIRE_INFO_JOBDATA))
					jobData = (JobTypeData) map.get(Paths.REQUIRE_INFO_JOBDATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (jobData == null)
				jobData = new JobTypeData(null);
			jobData.setParent(this);
			try {
				if (map.containsKey(Paths.REQUIRE_INFO_LEVELDATA))
					levelData = (LevelData) map.get(Paths.REQUIRE_INFO_LEVELDATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (levelData == null)
				levelData = new LevelData(null);
			levelData.setParent(this);
		}
		
		public JobTypeData getJobTypeData() {
			return jobData;
		}
		public LevelData getLevelData() {
			return levelData;
		}

		@Override
		public RequireType<QuestPlayer> getType() {
			return JobLevelRequireType.this;
		}

		@Override
		public boolean isAllowed(QuestPlayer user) {
			if (user.getPlayer()==null)
				return false;
			if (jobData.getJob()==null)
				return false;
			JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(user.getPlayer());
			if (jobsPlayer!=null && jobsPlayer.isInJob(jobData.getJob()) 
					&& jobsPlayer.getJobProgression(jobData.getJob()).getLevel()>=levelData.getLevel())
				return true;
			return false;
		}

		@Override
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
		
	}
	
}
