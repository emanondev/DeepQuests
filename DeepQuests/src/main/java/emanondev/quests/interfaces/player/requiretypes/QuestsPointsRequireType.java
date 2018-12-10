package emanondev.quests.interfaces.player.requiretypes;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.interfaces.ARequire;
import emanondev.quests.interfaces.ARequireType;
import emanondev.quests.interfaces.Paths;
import emanondev.quests.interfaces.data.LevelData;
import emanondev.quests.interfaces.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;

public class QuestsPointsRequireType extends ARequireType<QuestPlayer> {

	public QuestsPointsRequireType() {
		super(ID);
	}
	
	private static final String ID = "player_quests_points_require";

	@Override
	public ItemStack getGuiItem() {
		return new ItemBuilder(Material.TRIPWIRE_HOOK).setGuiProperty().build();
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Player require missions points");
	}

	@Override
	public QuestsPointsRequire getInstance(Map<String, Object> map) {
		return new QuestsPointsRequire(map);
	}
	
	public class QuestsPointsRequire extends ARequire<QuestPlayer> {
		
		private LevelData levelData = null;

		public QuestsPointsRequire(Map<String, Object> map) {
			super(map);
			if (map == null)
				map = new LinkedHashMap<>();
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
		
		public LevelData getLevelData() {
			return levelData;
		}
		
		@Override
		public QuestsPointsRequireType getType() {
			return QuestsPointsRequireType.this;
		}
		
		public boolean isAllowed(QuestPlayer p) {
			int points = p.getData().getQuestsPoints();
			return points>=levelData.getLevel();
		}
		
		public List<String> getInfo() {
			List<String> info = super.getInfo();
			info.add("&9Required Quests Points: &e"+levelData.getLevel());
			return info;
		}
	}

}