package emanondev.quests.interfaces.player.requiretypes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import emanondev.quests.interfaces.ARequire;
import emanondev.quests.interfaces.ARequireType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.Require;
import emanondev.quests.interfaces.RequireType;
import emanondev.quests.interfaces.data.LevelData;
import emanondev.quests.interfaces.data.McMMOSkillTypeData;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class McmmoLevelRequireType extends ARequireType<QuestPlayer> {

	public McmmoLevelRequireType() {
		super(ID);
	}
	
	private static final String ID = "player_mcmmo_level_require";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.EXPERIENCE_BOTTLE).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require a certain lv on a mcmmo skill");
	}

	@Override
	public Require<QuestPlayer> getInstance(Map<String, Object> map) {
		return new McmmoLevelRequire(map);
	}
	
	public class McmmoLevelRequire extends ARequire<QuestPlayer> {
		
		private McMMOSkillTypeData skillData = null;
		private LevelData levelData = null;

		public McmmoLevelRequire(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
			try {
				if (map.containsKey(Paths.REQUIRE_INFO_MCMMODATA))
					skillData = (McMMOSkillTypeData) map.get(Paths.REQUIRE_INFO_JOBDATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (skillData == null)
				skillData = new McMMOSkillTypeData(null);
			skillData.setParent(this);
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
		
		public McMMOSkillTypeData getJobTypeData() {
			return skillData;
		}
		public LevelData getLevelData() {
			return levelData;
		}

		@Override
		public RequireType<QuestPlayer> getType() {
			return McmmoLevelRequireType.this;
		}

		public boolean isAllowed(QuestPlayer p) {
			if (skillData.getSkillType()==null)
				return false;
			try {
				McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
				if (mcmmoPlayer.getSkillLevel(skillData.getSkillType())>=levelData.getLevel())
					return true;
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}


		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (skillData.getSkillType()==null)
				info.add("&9Required Skill level: &cnot setted");
			else {
				info.add("&9Required Level: &e"+levelData.getLevel());
				info.add("  &9on Skill: &e"+skillData.getSkillType().toString());
				
			}
			return info;
		}
	}

}
