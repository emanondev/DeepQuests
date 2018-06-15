package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.reward.MissionRewardType;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;

public class ForceStartMissionRewardType extends AbstractRewardType implements MissionRewardType {

	private final static String PATH_TARGET_MISSION_ID = "target-mission";
	public ForceStartMissionRewardType() {
		super("FORCESTARTMISSION");
	}
	public class ForceStartMissionReward extends AbstractReward implements MissionReward {
		private String targetMissionID;
		public ForceStartMissionReward(MemorySection section, Mission parent) {
			super(section, parent);
			this.targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID,null);
			this.addToEditor(9,new ForceStartMissionRewardEditorButtonFactory());
		}
		public String getInfo() {
			if (targetMissionID==null || getParent().getParent().getMissionByNameID(targetMissionID)==null)
				return "Mission ("+targetMissionID+")";
			return "Mission "+getParent().getParent().getMissionByNameID(targetMissionID).getDisplayName()+"("+targetMissionID+")";
		}
		public Mission getParent() {
			return (Mission) super.getParent();
		}
		@Override
		public void applyReward(QuestPlayer p, Mission m) {
			if (targetMissionID==null)
				return;
			Mission target = getParent().getParent().getMissionByNameID(targetMissionID);
			if (target==null)
				return;
			p.startMission(target,true);
		}
		@Override
		public MissionRewardType getRewardType() {
			return ForceStartMissionRewardType.this;
		}
		public String getKey() {
			return getRewardType().getKey();
		}
		public boolean setTargetMission(Mission mission) {
			if (mission == null) {
				targetMissionID = null;
			} else
				targetMissionID = mission.getNameID();

			getSection().set(PATH_TARGET_MISSION_ID, targetMissionID);
			getParent().setDirty(true);

			return true;
		}

		private class ForceStartMissionRewardEditorButtonFactory implements EditorButtonFactory {
			private class ForceStartMissionRewardEditorButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.PAPER);

				public ForceStartMissionRewardEditorButton(CustomGui parent) {
					super(parent);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lRequired Mission Editor"));
					item.setItemMeta(meta);
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					clicker.openInventory(new TargetMissionSelectorGui(clicker).getInventory());

				}

				private class TargetMissionSelectorGui extends CustomMultiPageGui<CustomButton> {

					public TargetMissionSelectorGui(Player p) {
						super(p, ForceStartMissionRewardEditorButton.this.getParent(), 6, 1);
						this.setTitle(null, StringUtils.fixColorsAndHolders("&8Select which mission is required"));
						for (Mission mission : ForceStartMissionReward.this.getParent().getParent().getMissions())
							this.addButton(new TargetMissionButton(mission));
						this.reloadInventory();
					}

					private class TargetMissionButton extends CustomButton {
						private Mission mission;
						private ItemStack item = new ItemStack(Material.PAPER);

						public TargetMissionButton(Mission mission) {
							super(TargetMissionSelectorGui.this);
							this.mission = mission;
							ArrayList<String> desc = new ArrayList<String>();
							desc.add("&6Mission: '&e" + mission.getDisplayName() + "&6'");
							desc.add("&6Click to open editor");
							desc.add("");
							desc.add("&7 contains &e" + mission.getTasks().size() + " &7tasks");
							for (Task task : mission.getTasks()) {
								desc.add("&7 - &e" + task.getDisplayName() + " &7(" + task.getTaskType().getKey() + ")");
							}
							StringUtils.setDescription(item, desc);
						}

						@Override
						public ItemStack getItem() {
							return item;
						}

						@Override
						public void onClick(Player clicker, ClickType click) {
							setTargetMission(mission);
							clicker.openInventory(ForceStartMissionRewardEditorButton.this.getParent().getInventory());
						}
					}
				}
			}

			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new ForceStartMissionRewardEditorButton(parent);
			}
		}
		
	}
	@Override
	public MissionReward getRewardInstance(MemorySection m, Mission mission) {
		return new ForceStartMissionReward(m,mission);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.ARROW;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Force the player to start selected mission");
	}

}
