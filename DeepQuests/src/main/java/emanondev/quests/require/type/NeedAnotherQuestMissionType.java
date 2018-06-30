package emanondev.quests.require.type;

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
import emanondev.quests.quest.Quest;
import emanondev.quests.quest.QuestManager;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.Require;
import emanondev.quests.require.RequireType;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadableWithCooldown;

public class NeedAnotherQuestMissionType extends AbstractRequireType implements RequireType {

	private final static String PATH_TARGET_MISSION_ID = "target-mission";
	private final static String PATH_TARGET_QUEST_ID = "target-quest";

	public NeedAnotherQuestMissionType() {
		super("NEEDANOTHERQUESTMISSION");
	}

	public class NeedAnotherQuestMissionRequire extends AbstractRequire implements Require {
		private String targetMissionID;
		private String targetQuestID;

		public NeedAnotherQuestMissionRequire(MemorySection section, YmlLoadableWithCooldown parent) {
			super(section, parent);
			if (!((parent instanceof Quest) || (parent instanceof Mission)))
				throw new IllegalArgumentException();
			this.targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID, null);
			this.targetQuestID = getSection().getString(PATH_TARGET_QUEST_ID, null);

			this.addToEditor(9, new RequireButtonFactory());
		}
		private QuestManager getQuestManager() {
			if (getParent() instanceof Mission)
				return ((Mission) getParent()).getParent().getParent();
			return ((Quest) getParent()).getParent();
		}

		public String getInfo() {
			Quest quest;
			if (targetQuestID == null)
				return "Quest (" + targetQuestID + ") Mission (" + targetMissionID + ")";
			quest = getQuestManager().getQuestByNameID(targetQuestID);
			if (quest == null)
				return "Quest ??? Mission ???";
			if (targetMissionID == null || quest.getMissionByNameID(targetMissionID) == null)
				return "Quest " + quest.getDisplayName() + " Mission ???";
			return "Quest " + quest.getDisplayName() + " Mission "
				+ quest.getMissionByNameID(targetMissionID).getDisplayName();
		}

		@Override
		public RequireType getType() {
			return NeedAnotherQuestMissionType.this;
		}

		public boolean setTargetMission(Mission mission) {
			if (mission != null && (getParent() instanceof Mission) && mission.getParent().equals(((Mission) this.getParent()).getParent()))
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

		private class RequireButtonFactory implements EditorButtonFactory {
			private class RequireButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.PAPER);

				public RequireButton(CustomGui parent) {
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
						super(p, RequireButton.this.getParent(), 6, 1);
						this.setTitle(null, StringUtils.fixColorsAndHolders("&8Select which quest"));
						for (Quest quest : getQuestManager().getQuests())
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
							desc.add("&6Quest: '&e" + quest.getDisplayName() + "&6'");
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
								super(p, RequireButton.this.getParent(), 6, 1);
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
									clicker.openInventory(RequireButton.this
											.getParent().getInventory());
								}
							}
						}
					}
				}
			}

			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new RequireButton(parent);
			}
		}

		@Override
		public boolean isAllowed(QuestPlayer qPlayer) {
			try {
				Quest targetQuest = getQuestManager().getQuestByNameID(targetQuestID);
				switch (qPlayer.getDisplayState(targetQuest.getMissionByNameID(targetMissionID))) {
				case COMPLETED:
				case COOLDOWN:
					return true;
				default:
					break;
				}
				return false;
				
			}catch (Exception e) {
				return false;
			}
		}
	}

	@Override
	public Require getInstance(MemorySection m, YmlLoadableWithCooldown loadable) {
		return new NeedAnotherQuestMissionRequire(m,loadable);
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