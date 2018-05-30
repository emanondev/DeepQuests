package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.TextEditorButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.quest.Quest;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.WithGui;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;

public class ConsoleCommandRewardType extends AbstractRewardType implements RewardType {
	
	public ConsoleCommandRewardType() {
		super("CONSOLECOMMAND");
	}

	public class ConsoleCommandReward extends AbstractReward implements Reward {
		private static final String PATH_COMMAND = "command";
		private String command;
		public ConsoleCommandReward(MemorySection section,WithGui gui) {
			super(section, gui);
			command = section.getString(PATH_COMMAND);
			this.addToEditor(1, new CommandEditorButtonFactory());
		}
		@Override
		public void applyReward(QuestPlayer p) {
			if (command!=null)
				Bukkit.dispatchCommand(
					Bukkit.getConsoleSender(),
					command.replace("%player%", p.getPlayer().getName())
					);
		}
		@Override
		public RewardType getRewardType() {
			return ConsoleCommandRewardType.this;
		}
		public boolean setCommand(String cmd) {
			if (cmd!=null && cmd.equals(command))
				return false;
			if (cmd==null && command==null)
				return false;
			this.command = cmd;
			getSection().set(PATH_COMMAND,command);
			if (getParent() instanceof Savable)
				((Savable) getParent()).setDirty(true);
			return true;
		}
		private class CommandEditorButtonFactory implements EditorButtonFactory {
			private class CommandEditorButton extends TextEditorButton {
				private ItemStack item = new ItemStack(Material.COMMAND);
				public CommandEditorButton(CustomGui parent) {
					super(parent);
					update();
				}
				@Override
				public ItemStack getItem() {
					return item;
				}
				public void update() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lReward Command Editor");
					desc.add("&6Click to edit");
					desc.add("&7Current value:");
					if (command!=null)
						desc.add("&7'&f"+command+"&7'");
					else
						desc.add("&7no command is set");
					desc.add("");
					desc.add("&7Represent the command of the Reward");
					StringUtils.setDescription(item, desc);
				}
				@Override
				public void onClick(Player clicker, ClickType click) {
					this.requestText(clicker, StringUtils.revertColors(command), changeCommandHelp);
				}
				@SuppressWarnings("rawtypes")
				@Override
				public void onReicevedText(String text) {
					if (text == null)
						text = "";
					if (setCommand(text)) {
						update();
						getParent().reloadInventory();
						((EditorGui) getParent()).updateTitle();
					}
					else
						getOwner().sendMessage(StringUtils.fixColorsAndHolders(
								"&cSelected command was not a valid command"));
				}
			}
			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new CommandEditorButton(parent);
			}
		}
		
		
	}
	private static final BaseComponent[] changeCommandHelp = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command executed by console to set as reward\n\n"+
			ChatColor.GOLD+"or just Change override old command with new command\n"+
			ChatColor.YELLOW+"/questtext <new command>\n\n"+
			ChatColor.GRAY+"use %player% as playername holder"
			).create();
		
	@Override
	public Material getGuiItemMaterial() {
		return Material.COMMAND;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(
				"&7Perform a console command,",
				"&7%player% is replaced with player name",
				"&7Command should not contain the starting '/'",
				"&7Example: 'give %player diamond'%"
				);
	}

	@Override
	public Reward getRewardInstance(MemorySection section,WithGui gui) {
		return new ConsoleCommandReward(section,gui);
	}

	@Override
	public Reward getRewardInstance(MemorySection section, Mission mission) {
		return new ConsoleCommandReward(section,mission);
	}

	@Override
	public Reward getRewardInstance(MemorySection section, Quest q) {
		return new ConsoleCommandReward(section,q);
	}
	

}
