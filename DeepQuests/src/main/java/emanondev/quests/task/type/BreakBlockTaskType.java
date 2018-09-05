package emanondev.quests.task.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.hooks.Hooks;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.AbstractTask;
import emanondev.quests.task.BlocksTaskInfo;
import emanondev.quests.task.DropsTaskInfo;
import emanondev.quests.task.Task;
import emanondev.quests.task.TaskType;
import emanondev.quests.utils.StringUtils;

public class BreakBlockTaskType extends TaskType {

	public BreakBlockTaskType() {
		super("breakblock");
	}
	
	
	@EventHandler (ignoreCancelled=true,priority = EventPriority.HIGHEST)
	private void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer()==null)
			return;
		QuestPlayer qPlayer = Quests.getInstance().getPlayerManager()
				.getQuestPlayer(event.getPlayer());
		List<Task> tasks = qPlayer.getActiveTasks(Quests.getInstance().getTaskManager()
				.getTaskType(key));
		if (tasks ==null||tasks.isEmpty())
			return;
		for (int i = 0; i < tasks.size(); i++) {
			BreakBlockTask task = (BreakBlockTask) tasks.get(i);
			if (task.isWorldAllowed(event.getPlayer().getWorld()) 
					 && task.isValidBlock(event.getBlock())) {
				if (task.onProgress(qPlayer)) {
					if (task.drops.areDropsRemoved())
						event.setDropItems(false);
					if (task.drops.isExpRemoved())
						event.setExpToDrop(0);
				}
			}
		}
	}
	
	public class BreakBlockTask extends AbstractTask {
		private final static String PATH_CHECK_VIRGIN = "check-virgin-block";
		private boolean checkVirgin;
		private final DropsTaskInfo drops;
		private final BlocksTaskInfo blocks;
		//TODO option CHECKTOOL
		public BreakBlockTask(ConfigSection m, Mission parent) {
			super(m, parent,BreakBlockTaskType.this);
			drops = new DropsTaskInfo(m,this);
			blocks = new BlocksTaskInfo(m,this);
			checkVirgin = m.getBoolean(PATH_CHECK_VIRGIN,true);
			this.addToEditor(27,drops.getRemoveDropsEditorButtonFactory());
			this.addToEditor(28,drops.getRemoveExpEditorButtonFactory());
			this.addToEditor(9,blocks.getBlocksSelectorButtonFactory());
			this.addToEditor(29,new VirginCheckButtonFactory());
		}
		public boolean setVirginCheck(boolean value) {
			if (checkVirgin == value)
				return false;
			checkVirgin = value;
			getSection().getBoolean(PATH_CHECK_VIRGIN,checkVirgin);
			getParent().setDirty(true);
			return true;
		}
		public boolean isVirginCheckEnabled() {
			return checkVirgin;
		}
		
		public boolean isValidBlock(Block block) {
			if (checkVirgin)
				return blocks.isValidBlock(block)&&Hooks.isBlockVirgin(block);
			else
				return blocks.isValidBlock(block);
		}
		
		private class VirginCheckButtonFactory implements EditorButtonFactory {
			private class VirginCheckButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.WOOL);
				public VirginCheckButton(CustomGui parent) {
					super(parent);
					item.setDurability((short) 3);
					update();
				}
				@Override
				public ItemStack getItem() {
					return item;
				}
				public void update() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lVirgin Block Flag");
					desc.add("&6Click to toggle");
					if (!checkVirgin) {
						desc.add("&7No restrintions on broken blocks");
						desc.add("&7Blocks previusly placed by players are &aAllowed");
						item.setDurability((short) 14);
					}
					else {
						desc.add("&7Now only blocks naturally generated");
						desc.add("&7or growed blocks (like trees) are allowed");
						desc.add("&7All blocks previusly placed by players");
						desc.add("&7are &cnot allowed");
						item.setDurability((short) 5);
					}
					desc.add("");
					desc.add("This flag is usefull to prevent players from");
					desc.add("destroy, place and destroy again the same block");
					desc.add("multiple times to advance the task");
					StringUtils.setDescription(item,desc);
				}
				@Override
				public void onClick(Player clicker, ClickType click) {
					setVirginCheck(!checkVirgin);
					update();
					getParent().reloadInventory();
				}
			}
			
			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				if (Hooks.isVirginBlockPluginEnabled())
					return new VirginCheckButton(parent);
				else
					return null;
			}
		}
	}

	@Override
	public Task getTaskInstance(ConfigSection m, Mission parent) {
		return new BreakBlockTask(m,parent);
	}


	@Override
	public Material getGuiItemMaterial() {
		return Material.IRON_PICKAXE;
	}

	private static final List<String> description = Arrays.asList(
			"&7Has to Break a specified number",
			"&7of Blocks of the selected type"
			);
	@Override
	public List<String> getDescription() {
		return description;
	}
}
