package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;
import emanondev.quests.reward.MissionReward;
import emanondev.quests.reward.MissionRewardType;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;

public class ForceStartAnotherQuestMissionRewardType extends AbstractRewardType implements MissionRewardType {

	private final static String PATH_TARGET_MISSION_ID = "target-mission";
	private final static String PATH_TARGET_QUEST_ID = "target-quest";

	public ForceStartAnotherQuestMissionRewardType() {
		super("FORCESTARTANOTHERQUESTMISSION");
	}

	public class ForceStartAnotherQuestMissionReward extends AbstractReward implements MissionReward {
		private String targetMissionID;
		private String targetQuestID;

		public ForceStartAnotherQuestMissionReward(ConfigSection section, Mission parent) {
			super(section, parent);
			this.targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID, null);
			this.targetQuestID = getSection().getString(PATH_TARGET_QUEST_ID, null);

			this.addToEditor(9, new RewardButtonFactory());
		}

		public String getInfo() {
			Quest quest;
			if (targetQuestID == null)
				return "Quest (" + targetQuestID + ") Mission (" + targetMissionID + ")";
			quest = getParent().getParent().getParent().getQuestByNameID(targetQuestID);
			if (quest == null)
				return "Quest (" + targetQuestID + ") Mission (" + targetMissionID + ")";
			if (targetMissionID == null || quest.getMissionByNameID(targetMissionID) == null)
				return "Quest " + quest.getDisplayName() + " (" + targetQuestID + ") Mission (" + targetMissionID + ")";
			return "Quest " + quest.getDisplayName() + " (" + targetQuestID + ") Mission "
					+ quest.getMissionByNameID(targetMissionID).getDisplayName() + " (" + targetMissionID + ")";
		}

		public Mission getParent() {
			return (Mission) super.getParent();
		}

		@Override
		public MissionRewardType getType() {
			return ForceStartAnotherQuestMissionRewardType.this;
		}

		public boolean setTargetMission(Mission mission) {
			if (mission != null && mission.getParent().equals(this.getParent().getParent()))
				return false;
			if (mission == null) {
				targetMissionID = null;
				targetQuestID = null;
			} else {
				targetMissionID = mission.getNameID();
				targetQuestID = mission.getParent().getNameID();
			}

			getSection().set(PATH_TARGET_MISSION_ID, targetMissionID);
			getSection().set(PATH_TARGET_QUEST_ID, targetQuestID);
			getParent().setDirty(true);

			return true;
		}

		private class RewardButtonFactory implements EditorButtonFactory {
			private class RewardButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.PAPER);

				public RewardButton(CustomGui parent) {
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
					clicker.openInventory(new QuestSelectorGui(clicker).getInventory());

				}

				private class QuestSelectorGui extends CustomMultiPageGui<CustomButton> {

					public QuestSelectorGui(Player p) {
						super(p, RewardButton.this.getParent(), 6, 1);
						this.setTitle(null, StringUtils.fixColorsAndHolders("&8Select which quest"));
						for (Quest quest : ForceStartAnotherQuestMissionReward.this.getParent().getParent().getParent()
								.getQuests())
							this.addButton(new QuestButton(quest));
						this.reloadInventory();
					}

					private class QuestButton extends CustomButton {
						private Quest quest;
						private ItemStack item = new ItemStack(Material.BOOK);

						public QuestButton(Quest quest) {
							super(QuestSelectorGui.this);
							this.quest = quest;
							ArrayList<String> desc = new ArrayList<String>();
							desc.add("&Quest: '&e" + quest.getDisplayName() + "&6'");
							desc.add("&6Click to open editor");
							desc.add("");
							desc.add("&7 contains &e" + quest.getMissions().size() + " &7missions");
							for (Mission mission : quest.getMissions()) {
								desc.add("&7 - &e" + mission.getDisplayName());
							}
							StringUtils.setDescription(item, desc);
						}

						@Override
						public ItemStack getItem() {
							return item;
						}

						@Override
						public void onClick(Player clicker, ClickType click) {
							clicker.openInventory(new MissionSelectorGui(clicker).getInventory());
						}
						private class MissionSelectorGui extends CustomMultiPageGui<CustomButton> {

							public MissionSelectorGui(Player p) {
								super(p, RewardButton.this.getParent(), 6, 1);
								this.setTitle(null,
										StringUtils.fixColorsAndHolders("&8Select which mission is autostarted"));
								for (Mission mission : quest.getMissions())
									this.addButton(new MissionButton(mission));
								this.reloadInventory();
							}

							private class MissionButton extends CustomButton {
								private Mission mission;
								private ItemStack item = new ItemStack(Material.PAPER);

								public MissionButton(Mission mission) {
									super(MissionSelectorGui.this);
									this.mission = mission;
									ArrayList<String> desc = new ArrayList<String>();
									desc.add("&6Mission: '&e" + mission.getDisplayName() + "&6'");
									desc.add("&6Click to open editor");
									desc.add("");
									desc.add("&7 contains &e" + mission.getTasks().size() + " &7tasks");
									for (Task task : mission.getTasks()) {
										desc.add("&7 - &e" + task.getDisplayName() + " &7("
												+ task.getTaskType().getKey() + ")");
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
									clicker.openInventory(RewardButton.this
											.getParent().getInventory());
								}
							}
						}
					}
				}
			}

			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new RewardButton(parent);
			}
		}

		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			if (amount<=0)
				return;
			if (targetMissionID == null || targetQuestID == null)
				return;
			Quest quest = getParent().getParent().getParent().getQuestByNameID(targetQuestID);
			if (quest == null)
				return;
			Mission target = quest.getMissionByNameID(targetMissionID);
			if (target == null)
				return;
			qPlayer.startMission(target, true);
		}
	}

	@Override
	public MissionReward getInstance(ConfigSection m, YmlLoadable mission) {
		if(!(mission instanceof Mission))
			throw new IllegalArgumentException();
		return new ForceStartAnotherQuestMissionReward(m,(Mission) mission);
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.ARROW;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList("&7Force the player to start selected mission", "&7of selected quest");
	}

}
