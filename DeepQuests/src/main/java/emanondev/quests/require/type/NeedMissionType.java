package emanondev.quests.require.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.require.AbstractRequire;
import emanondev.quests.require.AbstractRequireType;
import emanondev.quests.require.MissionRequire;
import emanondev.quests.require.MissionRequireType;
import emanondev.quests.task.Task;
import emanondev.quests.utils.StringUtils;

public class NeedMissionType extends AbstractRequireType implements MissionRequireType {
	private final static String ID = "MISSIONCOMPLETED";
	private final static String PATH_TARGET_MISSION_ID = "target-mission";

	public NeedMissionType() {
		super(ID);
	}

	@Override
	public MissionRequire getRequireInstance(MemorySection section, Mission mission) {
		return new NeedMission(section, mission);
	}

	public class NeedMission extends AbstractRequire implements MissionRequire {
		private String targetMissionID;

		public NeedMission(MemorySection section, Mission mission) {
			super(section, mission);
			targetMissionID = getSection().getString(PATH_TARGET_MISSION_ID);
			this.addToEditor(8, new NeedMissionEditorButtonFactory());
		}

		public Mission getParent() {
			return (Mission) super.getParent();
		}

		@Override
		public boolean isAllowed(QuestPlayer p) {// TODO avoid loops
			Mission target = getParent().getParent().getMissionByNameID(targetMissionID);
			if (target == null) {
				Quests.getLogger("errors").log("quest " + getParent().getParent().getNameID() + " -> mission "
						+ getParent().getNameID() + " -> require unexistent mission " + targetMissionID);
				return true;
			}
			switch (p.getDisplayState(target)) {
			case COMPLETED:
			case COOLDOWN:
				return true;
			default:
				break;
			}
			return false;
		}

		@Override
		public MissionRequireType getRequireType() {
			return NeedMissionType.this;
		}

		public String getKey() {
			return getRequireType().getKey();
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

		private class NeedMissionEditorButtonFactory implements EditorButtonFactory {
			private class NeedMissionEditorButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.PAPER);

				public NeedMissionEditorButton(CustomGui parent) {
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
						super(p, NeedMissionEditorButton.this.getParent(), 6, 1);
						this.setTitle(null, StringUtils.fixColorsAndHolders("&8Select which mission is required"));
						HashSet<String> blackList = new HashSet<String>();

						// can't select himself
						blackList.add(NeedMission.this.getParent().getNameID());
						Mission thisMission = NeedMission.this.getParent();

						// can't select already blocked missions
						for (MissionRequire req : thisMission.getRequires()) {
							if (!(req instanceof NeedMission))
								continue;
							if (((NeedMission) req).targetMissionID == null)
								continue;
							blackList.add(((NeedMission) req).targetMissionID);
						}

						for (Mission mission : thisMission.getParent().getMissions()) {
							if (blackList.contains(mission.getNameID()))
								continue;
							for (MissionRequire req : mission.getRequires()) {
								if (!(req instanceof NeedMission))
									continue;
								if (((NeedMission) req).targetMissionID == null)
									continue;
								if (((NeedMission) req).targetMissionID.equals(thisMission.getNameID())) {
									blackList.add(mission.getNameID());
									break;
								}
							}
							if (!blackList.contains(mission.getNameID()))
								this.addButton(new TargetMissionButton(mission));
						}
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
							clicker.openInventory(NeedMissionEditorButton.this.getParent().getInventory());
						}
					}
				}
			}

			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new NeedMissionEditorButton(parent);
			}
		}
	}

	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_FENCE;
	}

	@Override
	public List<String> getDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&7Check if the player has completed the selected mission");
		return desc;
	}

}
