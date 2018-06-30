package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;

import emanondev.quests.gui.button.AmountEditorButtonFactory;
import emanondev.quests.gui.button.McmmoSkillEditorButtonFactory;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public class McmmoLvRequireType extends AbstractRequireType implements RequireType {
	private final static String KEY = "MCMMOLEVEL";

	public McmmoLvRequireType() {
		super(KEY);
	}

	@Override
	public Require getInstance(MemorySection section, YmlLoadableWithCooldown parent) {
		return new McmmoLvRequire(section,parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.EXP_BOTTLE;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has at least level on selected skill");
		return desc;
	}

	private final static String PATH_SKILLTYPE = "skilltype";
	private final static String PATH_LEVEL = "level";
	
	public class McmmoLvRequire extends AbstractRequire implements Require {
		private SkillType skillType;
		private int level;
		public McmmoLvRequire(MemorySection section, YmlLoadableWithCooldown parent) {
			super(section, parent);
			try {
				skillType = SkillType.valueOf(getSection().getString(PATH_SKILLTYPE,null));
			} catch (Exception e) {
				skillType = null;
			}
			level = getSection().getInt(PATH_LEVEL,1);
			this.addToEditor(17,new LevelEditor());
			this.addToEditor(16, new SkillTypeEditor());
		}
		private class SkillTypeEditor extends McmmoSkillEditorButtonFactory {
			public SkillTypeEditor() {
				super();
			}
			@Override
			protected boolean onSelection(SkillType skillType) {
				return setSkillType(skillType);
			}
			@Override
			protected SkillType getObject() {
				return skillType;
			}
		}
		@Override
		public boolean isAllowed(QuestPlayer p) {
			if (skillType==null)
				return false;
			try {
				McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
				if (mcmmoPlayer.getSkillLevel(skillType)>=level)
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

		@Override
		public String getInfo() {
			if (skillType==null)
				return "Require Mcmmo Level not set";
			return "Require Level "+level+" on "+skillType.toString()+" Mcmmo skill";
		}

		public SkillType getSkillType() {
			return skillType;
		}
		public boolean setSkillType(SkillType skillType) {
			if (skillType==null && this.skillType==null)
				return false;
			if (this.skillType!= null && this.skillType.equals(skillType) )
				return false;
			this.skillType = skillType;
			if (skillType==null)
				getSection().set(PATH_SKILLTYPE,null);
			else
				getSection().set(PATH_SKILLTYPE,skillType.toString());
			getParent().setDirty(true);
			return true;
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
	}
}
