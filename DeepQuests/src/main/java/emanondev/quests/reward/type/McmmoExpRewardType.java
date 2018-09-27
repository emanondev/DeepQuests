package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.ExperienceData;
import emanondev.quests.data.MCMMOSkillTypeData;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.QuestComponent;

public class McmmoExpRewardType extends AbstractRewardType implements RewardType  {
	private final static String KEY = "MCMMOEXP";

	public McmmoExpRewardType() {
		super(KEY);
	}

	@Override
	public Reward getInstance(ConfigSection section, QuestComponent parent) {
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
	public class McmmoExpReward extends AbstractReward implements Reward {
		private MCMMOSkillTypeData skillTypeData;
		private ExperienceData expData;

		public McmmoExpReward(ConfigSection section, QuestComponent parent) {
			super(section, parent);
			skillTypeData = new MCMMOSkillTypeData(section, this);
			expData = new ExperienceData(section, this);
		}

		@Override
		public String getInfo() {
			if (skillTypeData.getSkillType() == null)
				return "Reward Mcmmo experience not set";
			return "Reward "+expData.getExperience()+" exp on Mcmmo "+skillTypeData.getSkillType().toString();
		}

		@Override
		public void applyReward(QuestPlayer p,int amount) {
			if (amount<=0||expData.getExperience()<=0||skillTypeData.getSkillType()==null)
				return;
			try {
				McMMOPlayer mcmmoPlayer = UserManager.getPlayer(p.getPlayer());
				mcmmoPlayer.addXp(skillTypeData.getSkillType(), expData.getExperience()*amount);
				mcmmoPlayer.getProfile().registerXpGain(skillTypeData.getSkillType(), expData.getExperience()*amount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public MCMMOSkillTypeData getSkillTypeData() {
			return skillTypeData;
		}
		public ExperienceData getExperienceData() {
			return expData;
		}
		@Override
		public RewardType getType() {
			return McmmoExpRewardType.this;
		}
		
		public RewardEditor createEditorGui(Player p,Gui parent) {
			RewardEditor gui = super.createEditorGui(p, parent);
			gui.putButton(9, expData.getExpEditorButton(gui));
			gui.putButton(10, skillTypeData.getSkillTypeSelectorButton(gui));
			return gui;
		}
	}
}
