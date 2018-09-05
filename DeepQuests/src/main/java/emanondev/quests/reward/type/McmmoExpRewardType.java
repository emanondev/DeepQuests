package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.button.AmountEditorButtonFactory;
import emanondev.quests.gui.button.McmmoSkillEditorButtonFactory;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.YmlLoadable;

public class McmmoExpRewardType extends AbstractRewardType implements RewardType  {
	private final static String KEY = "MCMMOEXP";

	public McmmoExpRewardType() {
		super(KEY);
	}

	@Override
	public Reward getInstance(ConfigSection section, YmlLoadable parent) {
		return new McmmoExpReward(section,parent);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.EXP_BOTTLE;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Give selected amount of exp on");
		desc.add("&7selected mcmmo skill");
		return desc;
	}
	private final static String PATH_SKILLTYPE = "skilltype";
	private final static String PATH_EXPERIENCE = "experience_reward";
	public class McmmoExpReward extends AbstractReward implements Reward {
		private SkillType skillType;
		private int exp;

		public McmmoExpReward(ConfigSection section, YmlLoadable parent) {
			super(section, parent);
			try {
				skillType = SkillType.valueOf(getSection().getString(PATH_SKILLTYPE,null));
			} catch (Exception e) {
				skillType = null;
			}
			exp = getSection().getInt(PATH_EXPERIENCE,1);
			this.addToEditor(17, new ExpEditor());
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

		@Override
		public String getInfo() {
			if (skillType == null)
				return "Reward Mcmmo experience not set";
			return "Reward "+exp+" exp on Mcmmo "+skillType.toString();
		}

		@Override
		public void applyReward(QuestPlayer p,int amount) {
			if (amount<=0||exp<=0||skillType==null)
				return;
			try {
				McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
				mcmmoPlayer.addXp(skillType, exp*amount);
				mcmmoPlayer.getProfile().registerXpGain(skillType, exp*amount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public RewardType getType() {
			return McmmoExpRewardType.this;
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
	}
}
