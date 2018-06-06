package emanondev.quests.task.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Quests;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.utils.StringUtils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class MythicMobKillTaskType extends TaskType{
	public MythicMobKillTaskType() {
		super("KillMythicMob");
	}
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onKill(MythicMobDeathEvent event) {
		if (event.getKiller()==null || !(event.getKiller() instanceof Player))
			return;
		Player p = (Player) event.getKiller();
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(p);
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			MythicMobKillTask task = (MythicMobKillTask) tasks.get(i);
			if (task.isWorldAllowed(p.getWorld()) 
					 && task.isValidMythicMob(event.getMobType().getInternalName())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.areDropsRemoved())
						event.setDrops(new ArrayList<ItemStack>());
					if (task.drops.isExpRemoved())
						event.setExp(0);
				}
			}
		}
	}
	
	public class MythicMobKillTask extends AbstractTask {
		private final static String PATH_INTERNAL_NAMES = "mob-id";
		private final DropsTaskInfo drops;
		private final HashSet<String> internalNames = new HashSet<String>();
		//TODO option CHECKTOOL
		public MythicMobKillTask(MemorySection m, Mission parent) {
			super(m, parent,MythicMobKillTaskType.this);
			drops = new DropsTaskInfo(m,this);
			List<String> list = m.getStringList(PATH_INTERNAL_NAMES);
			if (list!=null)
				internalNames.addAll(list);
			this.addToEditor(27,drops.getRemoveDropsEditorButtonFactory());
			this.addToEditor(28,drops.getRemoveExpEditorButtonFactory());
			this.addToEditor(9,new MythicMobSelectorButtonFactory());
		}
		public boolean isValidMythicMob(String internalName) {
			if (internalNames.contains(internalName))
				return true;
			return false;
		}
		
		public boolean hasInternalName(String internalName) {
			return internalNames.contains(internalName);
		}

		public boolean addInternalName(String internalName) {
			if (internalNames.contains(internalName))
				return false;
			internalNames.add(internalName);
			getSection().set(PATH_INTERNAL_NAMES, new ArrayList<String>(internalNames));
			setDirty(true);
			return true;
		}

		public boolean removeInternalName(String internalName) {
			if (!internalNames.contains(internalName))
				return false;
			internalNames.remove(internalName);
			getSection().set(PATH_INTERNAL_NAMES, new ArrayList<String>(internalNames));
			setDirty(true);
			return true;
		}

		public EditorButtonFactory getIdSelectorButtonFactory() {
			return new MythicMobSelectorButtonFactory();
		}

		private class MythicMobSelectorButtonFactory implements EditorButtonFactory {
			private class MythicMobSelectorButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.SKULL_ITEM);

				public MythicMobSelectorButton(CustomGui parent) {
					super(parent);
					item.setDurability((short) 1);
					update();
				}

				public void update() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lMythicMob Selector");
					desc.add("&6Click to edit");
					if (internalNames.isEmpty())
						desc.add("&7No id restrictions are set");
					else {
						desc.add("&7All listed MythicMob id are &aAllowed");
						for (String internalName : internalNames)
							if (MythicMobs.inst().getMobManager().getMythicMob(internalName) == null)
								desc.add(" &7- &a" + internalName);
							else
								desc.add(" &7- ID &a" + internalName + " &7(" + MythicMobs.inst().getMobManager().getMythicMob(internalName).getDisplayName()+")");
					}
					StringUtils.setDescription(item, desc);
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					clicker.openInventory(new MythicMobSelectorGui(clicker).getInventory());
				}

				private class MythicMobSelectorGui extends CustomMultiPageGui<MobButton> {
					public MythicMobSelectorGui(Player p) {
						super(p, MythicMobSelectorButton.this.getParent(), 6, 1);
						this.setFromEndCloseButtonPosition(8);
						this.setTitle(null, StringUtils.fixColorsAndHolders("&8MythicMob Selector"));
						for (MythicMob mob : MythicMobs.inst().getMobManager().getMobTypes())
							this.addButton(new MobButton(this, mob));
						reloadInventory();
					}
				}
			}

			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new MythicMobSelectorButton(parent);
			}
		}

		private class MobButton extends CustomButton {
			private ItemStack item;
			private MythicMob mob;

			public MobButton(CustomGui parent, MythicMob mob) {
				super(parent);
				this.mob = mob;
				item = new ItemStack(Material.WOOL);
				item.setDurability((short) 6);
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				update();
			}

			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				if (hasInternalName(mob.getInternalName())) {
					desc.add("&6Mob: &f" + mob.getDisplayName());
					desc.add("&6ID: &a" + mob.getInternalName());
					desc.add("&aAllowed");
					item.addUnsafeEnchantment(Enchantment.DURABILITY,1);
					
				} else {
					desc.add("&6Mob: &f" + mob.getDisplayName());
					desc.add("&6ID: &a" + mob.getInternalName());
					desc.add("&cUnallowed");
					item.removeEnchantment(Enchantment.DURABILITY);
				}
				StringUtils.setDescription(item, desc);
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				if (hasInternalName(mob.getInternalName())) {
					if (removeInternalName(mob.getInternalName())) {
						update();
						getParent().reloadInventory();
					}
				} else {
					if (addInternalName(mob.getInternalName())) {
						update();
						getParent().reloadInventory();
					}
				}
			}
		}
		
	}

	@Override
	public Task getTaskInstance(MemorySection m, Mission parent) {
		return new MythicMobKillTask(m,parent);
	}
	@Override
	public Material getGuiItemMaterial() {
		return Material.GOLD_SWORD;
	}

	private static final List<String> description = Arrays.asList(
			"&7Player has to kill a specified number",
			"&7of mithicmobs of selected type"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
