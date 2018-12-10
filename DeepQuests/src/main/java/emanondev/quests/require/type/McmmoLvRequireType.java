package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.LevelData;
import emanondev.quests.data.MCMMOSkillTypeData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.QCWithCooldown;

public class McmmoLvRequireType extends AbstractRequireType implements RequireType {
	private final static String KEY = "MCMMOLEVEL";

	public McmmoLvRequireType() {
		super(KEY);
	}

	@Override
	public Require getInstance(ConfigSection section, QCWithCooldown parent) {
		return new McmmoLvRequire(section,parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.EXPERIENCE_BOTTLE;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has at least level on selected skill");
		return desc;
	}
	
	public class McmmoLvRequire extends AbstractRequire implements Require {
		
		private MCMMOSkillTypeData skillTypeData;
		private LevelData levelData;
		
		public McmmoLvRequire(ConfigSection section, QCWithCooldown parent) {
			super(section, parent);
			
			skillTypeData = new MCMMOSkillTypeData(section, this);
			levelData = new LevelData(section, this);
		}
		@Override
		public boolean isAllowed(QuestPlayer p) {
			if (skillTypeData.getSkillType()==null)
				return false;
			try {
				McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
				if (mcmmoPlayer.getSkillLevel(skillTypeData.getSkillType())>=levelData.getLevel())
					return true;
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public RequireType getType() {
			return McmmoLvRequireType.this;
		}

		public List<String> getInfo() {
			List<String> info = super.getInfo();
			if (skillTypeData.getSkillType()==null)
				info.add("&9Required Skill level: &cnot setted");
			else {
				info.add("&9Required Level: &e"+levelData.getLevel());
				info.add("  &9on Skill: &e"+skillTypeData.getSkillType().toString());
				
			}
			return info;
		}

		public MCMMOSkillTypeData getSkillTypeData() {
			return skillTypeData;
		}
		public LevelData getLevelData() {
			return levelData;
		}

		public RequireEditor createEditorGui(Player p,Gui parent) {
			RequireEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, levelData.getLevelEditorButton(gui));
			gui.putButton(10, skillTypeData.getSkillTypeSelectorButton(gui));
			return gui;
		}
	}
}
